package com.employa.employa.ui.chat;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import com.bumptech.glide.Glide;
import com.employa.employa.R;
import com.employa.employa.databinding.FragmentChatBinding;
import com.employa.employa.models.ChatMessage;
import com.employa.employa.repository.MainRepository;
import com.employa.employa.repository.MessagingRepository;
import com.employa.employa.repository.RegisteredListener;
import com.employa.employa.ui.chat.adapter.MessageAdapter;
import com.employa.employa.viewmodel.ChatViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ChatFragment extends Fragment implements ChatConversationListener {
    private static final int DEFAULT_MSG_LENGTH_LIMIT = 1000;
    private static final int RC_PHOTO_PICKER = 2;
    private static final int MESSAGE_LIST_LOAD_THRESHOLD = 3;

    private ChatViewModel chatViewModel;
    private RegisteredListener messageListener;

    private StorageReference chatPhotoReference;
    private String chatID;
    private String otherID;
    private FirebaseUser user;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        assert user != null;

        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        chatPhotoReference = firebaseStorage.getReference().child("chat_photos");

        chatViewModel = ViewModelProviders.of(this).get(ChatViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        chatViewModel.getMessages().removeObservers(this);
        chatViewModel.reset();

        // Inflate the layout for this fragment
        FragmentChatBinding binding = FragmentChatBinding.inflate(inflater, container, false);
        binding.setViewModel(chatViewModel);
        binding.setLifecycleOwner(this);
        binding.setListener(this);
        binding.messageEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(DEFAULT_MSG_LENGTH_LIMIT)});

        // RecyclerView
        final RecyclerView messages = binding.messageRecyclerView;
        final LinearLayoutManager llm = (LinearLayoutManager) messages.getLayoutManager();
        assert llm != null;

        MessageAdapter adapter = new MessageAdapter();
        messages.setAdapter(adapter);
        chatViewModel.getMessages().observe(this, chatMessages -> {
            // "submitList" zal niet updaten als lijst zelfde is, voor performance redenen.
            // Dus roep notifyItemInserted i.p.v. een lijst opnieuw te maken of alles te notifyen.
            // Dat is meer performant. We moeten ook weten op welke plaats iets werd ingevoegd, vandaar een wrapper rond een int.
            adapter.submitList(chatMessages);
            adapter.notifyItemInserted(chatViewModel.getInsertPosition());
        });

        // Enkel scrollen naar recentste bericht als we aan de onderkant zitten
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                if(!messages.canScrollVertically(1))
                    llm.smoothScrollToPosition(messages, null, adapter.getItemCount());
            }
        });

        // Luister als we bijna aan de bovenkant zitten, dan moeten we meer inladen
        messages.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                // De gebruiker scrollt omhoog naar eerdere messages
                if(dy < 0 && llm.findFirstVisibleItemPosition() < MESSAGE_LIST_LOAD_THRESHOLD)
                    chatViewModel.loadMoreMessages(chatID);
            }
        });

        // Get arguments
        Bundle bundle = requireArguments();
        chatID = bundle.getString(MessagingRepository.CHAT_ID_BUNDLE_NAME);
        otherID = bundle.getString(MessagingRepository.CHAT_OTHER_ID_BUNDLE);
        String chatName = bundle.getString(MessagingRepository.CHAT_NAME_BUNDLE_NAME);
        Uri uri = Uri.parse(bundle.getString(MessagingRepository.CHAT_OTHER_URI_DOWNLOAD));

        // Load messages and content
        messageListener = chatViewModel.fillMessages(chatID);
        binding.setChatName(chatName);

        Glide.with(binding.photoImageView.getContext())
                .load(uri)
                .into(binding.photoImageView);

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        messageListener.remove();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == RC_PHOTO_PICKER && resultCode == Activity.RESULT_OK) {
            Uri selectedImageUri = data.getData();
            StorageReference photoRef = chatPhotoReference.child(selectedImageUri.getLastPathSegment());

            // Upload file to firebase storage
            photoRef.putFile(selectedImageUri).addOnSuccessListener(getActivity(), taskSnapshot ->
                taskSnapshot.getMetadata().getReference().getDownloadUrl().addOnSuccessListener(uri -> {
                    ChatMessage myChatMessage = new ChatMessage(null, user.getUid(), uri.toString(), otherID);
                    chatViewModel.addMessage(myChatMessage, chatID);
                }));
        }
    }

    @Override
    public void showImagePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/jpeg");
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        startActivityForResult(Intent.createChooser(intent, getString(R.string.complete_action_using)), RC_PHOTO_PICKER);
    }

    @Override
    public void goBack() {
        // Hide soft keyboard
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);

        NavHostFragment.findNavController(this).popBackStack();
    }

    @Override
    public void goProfile() {
        NavController navController = NavHostFragment.findNavController(this);

        Bundle bundle = new Bundle();
        bundle.putString(MainRepository.UID_BUNDLE, otherID);

        navController.navigate(R.id.action_chatFragment_to_navigation_profile, bundle);
    }

    @Override
    public void sendMessage(Editable msg) {
        ChatMessage chatMessage = new ChatMessage(msg.toString(), user.getUid(), null, otherID);
        chatViewModel.addMessage(chatMessage, chatID);
        msg.clear();
    }
}
