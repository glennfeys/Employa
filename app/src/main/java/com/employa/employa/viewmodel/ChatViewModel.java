package com.employa.employa.viewmodel;

import com.employa.employa.models.ChatMessage;
import com.employa.employa.utility.Wrapper;
import com.employa.employa.repository.MessagingRepository;
import com.employa.employa.repository.RegisteredListener;

import java.util.LinkedList;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ChatViewModel extends ViewModel {
    private static final int CHAT_LOAD_LIMIT = 20;

    private MutableLiveData<LinkedList<ChatMessage>> messages = new MutableLiveData<>();
    private Wrapper<Boolean> blocked = new Wrapper<>(false);
    private Wrapper<Integer> inserted = new Wrapper<>(0);

    public void reset() {
        messages.setValue(new LinkedList<>());
    }

    public LiveData<LinkedList<ChatMessage>> getMessages() {
        return messages;
    }

    public RegisteredListener fillMessages(String chatID) {
        return MessagingRepository.getInstance().fillMessages(chatID, messages, inserted, CHAT_LOAD_LIMIT);
    }

    public void loadMoreMessages(String chatID) {
        if(blocked.getValue()) return;
        blocked.setValue(true);
        MessagingRepository.getInstance().loadMoreMessages(chatID, messages, blocked, inserted, CHAT_LOAD_LIMIT);
    }

    public void addMessage(ChatMessage chatMessage, String chatID) {
        MessagingRepository.getInstance().addMessage(chatMessage, chatID);
    }

    public int getInsertPosition() {
        return inserted.getValue();
    }
}
