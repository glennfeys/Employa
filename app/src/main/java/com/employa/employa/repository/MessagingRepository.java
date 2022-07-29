package com.employa.employa.repository;

import android.os.Bundle;
import android.util.Log;

import com.employa.employa.models.Chat;
import com.employa.employa.models.ChatMessage;
import com.employa.employa.models.User;
import com.employa.employa.utility.Consumer;
import com.employa.employa.utility.Wrapper;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import androidx.lifecycle.MutableLiveData;

public class MessagingRepository {
    public static final String CHAT_ID_BUNDLE_NAME = "chatID";
    public static final String CHAT_NAME_BUNDLE_NAME = "chatName";
    public static final String CHAT_OTHER_ID_BUNDLE = "otherUID";
    public static final String CHAT_OTHER_URI_DOWNLOAD = "otherURI";

    private static final String TAG = "Messaging";

    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;

    private static MessagingRepository instance;

    private MessagingRepository() {
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
    }

    public static synchronized MessagingRepository getInstance() {
        if(instance == null)
            instance = new MessagingRepository();

        return instance;
    }

    /**
     * Fill chats conversations into livedata chat list
     * @param chatList The chat conversation list
     * @return Registered listener
     */
    public RegisteredListener fillChats(final MutableLiveData<List<Chat>> chatList) {
        final CollectionReference chatsCollectionReference = firebaseFirestore.collection("chats");
        final FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        final CollectionReference userCollectionReference = firebaseFirestore.collection("users");

        return snapshotListenerHelper(
            chatsCollectionReference.whereArrayContains("users", currentUser.getUid())
                    .orderBy("lastActivity", Query.Direction.DESCENDING),
            qs -> {
                List<Task<Chat>> enrichmentTasks = new ArrayList<>(qs.size());
                for(DocumentSnapshot snap : qs.getDocuments()) {
                    final Chat chat = snap.toObject(Chat.class);
                    chat.setId(snap.getId());
                    enrichmentTasks.add(enrichChatConversation(userCollectionReference, chat));
                }

                Tasks.<Chat>whenAllSuccess(enrichmentTasks).addOnSuccessListener(chatList::postValue);
            });
    }

    /**
     * Vul data vanuit firebase op
     * @param userCollection User collection
     * @param chat           Chat conversation message
     * @return De taak die de chat verder aanvult
     */
    private Task<Chat> enrichChatConversation(final CollectionReference userCollection, final Chat chat) {
        final FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        final FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        // Figure out which user we are inside the list
        // This depends on who started the conversation
        int indexOtherUser = chat.getUsers().get(0).equals(currentUser.getUid()) ? 1 : 0;
        String otherId = chat.getUsers().get(indexOtherUser);

        return userCollection.document(otherId).get().onSuccessTask(documentSnapshot -> {
            // Get user model from database document
            final User otherChatter = documentSnapshot.toObject(User.class);
            otherChatter.setId(chat.getUsers().get(indexOtherUser));

            // Profile picture
            StorageReference profilePic = firebaseStorage.getReferenceFromUrl(otherChatter.getProfilePicture());
            return profilePic.getDownloadUrl().continueWith(task -> {
                if(task.isSuccessful()) {
                    otherChatter.setProfilePicDownload(task.getResult());
                    chat.setOtherUser(otherChatter);
                    return chat;
                } else {
                    return null;
                }
            });
        });
    }

    /**
     * Adds a chat message to a conversation
     * @param chatMessage The chat message
     * @param chatID      The chat conversation Id
     */
    public void addMessage(ChatMessage chatMessage, String chatID) {
        DocumentReference userRef = firebaseFirestore.collection("users").document(chatMessage.getName());
        CollectionReference messagesReference = firebaseFirestore.collection("chats").document(chatID).collection("messages");

        userRef.get().addOnSuccessListener(documentSnapshot -> {
            User user = documentSnapshot.toObject(User.class);
            assert user != null;
            String name = user.getName();
            chatMessage.setName(name);
            messagesReference.add(chatMessage);
        });

        // Update last activity of chat
        String smallMsg = chatMessage.getText();
        if(smallMsg == null) {
            smallMsg = "image";
        }

        messagesReference.getParent().update("lastMessage", smallMsg, "lastActivity", chatMessage.getTimestamp());
    }

    /**
     * Converts a message document to a model and adds it to the list of messages
     * @param snap     The document snapshot
     * @param messages The messages
     * @param inserted Insert positie
     * @param end      Append at end
     */
    private void addMsgDocToList(DocumentSnapshot snap, final MutableLiveData<LinkedList<ChatMessage>> messages, final Wrapper<Integer> inserted, boolean end) {
        ChatMessage msg = snap.toObject(ChatMessage.class);
        assert msg != null;
        msg.setId(snap.getId());
        LinkedList<ChatMessage> loadedMessages = messages.getValue();
        assert loadedMessages != null;

        if (end) {
            inserted.setValue(loadedMessages.size());
            loadedMessages.add(msg);
        } else {
            inserted.setValue(0);
            loadedMessages.addFirst(msg);
        }

        messages.setValue(loadedMessages);
    }

    /**
     * Helper: maakt message query
     * @param chatID Chat ID
     * @param limit  Limiet van het aantal berichten in te laden
     * @return De query
     */
    private Query createMessageQuery(String chatID, int limit) {
        final CollectionReference messagesReference = firebaseFirestore.collection("chats").document(chatID).collection("messages");
        return messagesReference.orderBy("timestamp", Query.Direction.DESCENDING).limit(limit);
    }

    /**
     * Fills messages in live data
     * @param chatID   Chat ID
     * @param messages Messages collection live data
     * @param inserted Insert positie
     * @param limit    Message laad limit
     * @return Listener registration
     */
    public RegisteredListener fillMessages(String chatID, final MutableLiveData<LinkedList<ChatMessage>> messages, final Wrapper<Integer> inserted, int limit) {
        return snapshotListenerHelper(createMessageQuery(chatID, limit), qs -> {
            // Reverse loop om volgorde bij snel toekomende messages goed te hebben
            List<DocumentChange> changes = qs.getDocumentChanges();
            for (int i = changes.size() - 1; i >= 0; --i) {
                DocumentChange dc = changes.get(i);
                if (dc.getType() == DocumentChange.Type.ADDED)
                    addMsgDocToList(dc.getDocument(), messages, inserted, true);
            }
        });
    }

    /**
     * Laad meer messages
     * @param chatID   Chat ID
     * @param messages Messages collection live data
     * @param blocked  Als het laden van meer messages geblokkeerd is of niet
     * @param inserted Insert positie
     * @param limit    Limiet van aantal berichten in te laden
     */
    public void loadMoreMessages(String chatID, final MutableLiveData<LinkedList<ChatMessage>> messages, final Wrapper<Boolean> blocked, final Wrapper<Integer> inserted, int limit) {
        createMessageQuery(chatID, limit)
            .startAfter(messages.getValue().get(0).getTimestamp())
            .get().addOnSuccessListener(qs -> {
                for (DocumentSnapshot snap : qs.getDocuments())
                    addMsgDocToList(snap, messages, inserted, false);

                // Als er minder docs toekomen dan de limit, dan zijn er geen meer. Anders kunnen we nog doorgaan
                if (qs.size() == limit)
                    blocked.setValue(false);
        });
    }

    /**
     * Helper for snapshot listener events
     * @param q  Query
     * @param cb Callback
     * @return Listener registration
     */
    private RegisteredListener snapshotListenerHelper(Query q, Consumer<QuerySnapshot> cb) {
        return new RegisteredListener(q.addSnapshotListener(((queryDocumentSnapshots, e) -> {
            if (e != null) {
                Log.e(TAG, e.getMessage());
                return;
            }

            if (queryDocumentSnapshots != null)
                cb.accept(queryDocumentSnapshots);
        })));
    }

    /**
     * Add chat with two users
     * @param other    The other user
     * @param navigate Navigation callback
     */
    public void addChatWith(final User other, Callback<Bundle> navigate) {
        final String me = firebaseAuth.getUid();
        final CollectionReference chatReference = firebaseFirestore.collection("chats");

        // Een limitatie van Firebase: je kan geen meerdere items opzoeken in "whereArrayContains" en je kan ze niet chainen.
        // Je kan ook geen logical OR queries uitvoeren.
        chatReference.whereArrayContains("users", me).get().addOnSuccessListener(snapshot -> {
            // Zoek bestaande chat tussen de 2 users
            Chat existingChat = null;
            for (DocumentChange dc : snapshot.getDocumentChanges()) {
                Chat chat = dc.getDocument().toObject(Chat.class);

                if (chat.getUsers().contains(other.getId())) {
                    existingChat = chat;
                    existingChat.setId(dc.getDocument().getId());
                    break;
                }
            }

            // Als er nog geen chat is tussen de deelnemers van de conversatie
            if(existingChat == null) {
                List<String> users = new ArrayList<>();
                users.add(me);
                users.add(other.getId());
                Chat chat = new Chat(users, Timestamp.now(), null);
                chatReference.add(chat).addOnSuccessListener(documentReference -> {
                    String chatID = documentReference.getId();
                    navigate.onSuccess(createMessagingBundle(chatID, other));
                });
            }
            // Als er wel al een chat is
            else {
                navigate.onSuccess(createMessagingBundle(existingChat.getId(), other));
            }
        });
    }

    /**
     * Create messaging bundle
     *
     * @param chatID Chat ID
     * @param other  Other user
     * @return The bundle
     */
    public Bundle createMessagingBundle(String chatID, User other) {
        Bundle bundle = new Bundle();
        bundle.putString(CHAT_ID_BUNDLE_NAME, chatID);
        bundle.putString(CHAT_NAME_BUNDLE_NAME, other.getName());
        bundle.putString(CHAT_OTHER_ID_BUNDLE, other.getId());
        bundle.putString(CHAT_OTHER_URI_DOWNLOAD, other.getProfilePicDownload().toString());
        return bundle;
    }
}
