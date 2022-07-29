package com.employa.employa.viewmodel;

import com.employa.employa.models.Chat;
import com.employa.employa.repository.MessagingRepository;
import com.employa.employa.repository.RegisteredListener;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MessagingViewModel extends ViewModel {
    private MutableLiveData<String> id = new MutableLiveData<>();
    private MutableLiveData<List<Chat>> chats = new MutableLiveData<>();

    public RegisteredListener fillChats() {
        return MessagingRepository.getInstance().fillChats(chats);
    }

    public LiveData<List<Chat>> getChats() {
        return chats;
    }

    public LiveData<String> getId() {
        return id;
    }

    public void setId(MutableLiveData<String> id) {
        this.id = id;
    }
}
