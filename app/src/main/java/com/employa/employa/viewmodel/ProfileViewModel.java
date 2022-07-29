package com.employa.employa.viewmodel;

import android.os.Bundle;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.employa.employa.models.Advertisement;
import com.employa.employa.models.Rating;
import com.employa.employa.models.User;
import com.employa.employa.repository.Callback;
import com.employa.employa.repository.MainRepository;
import com.employa.employa.repository.MessagingRepository;
import com.employa.employa.repository.RegisteredListener;

import java.util.List;

public class ProfileViewModel extends ViewModel {

    private MutableLiveData<User> userData = new MutableLiveData<>();
    private MutableLiveData<List<Rating>> ratings = new MutableLiveData<>();
    private MutableLiveData<Double> avgScore = new MutableLiveData<>();
    private MutableLiveData<Integer> size = new MutableLiveData<>();
    private MutableLiveData<List<Advertisement>> advertisements = new MutableLiveData<>();

    public void fillUser(String uid){
        MainRepository.getInstance().getUser(uid, userData);
    }

    public RegisteredListener fillRatings(String uid){
        return MainRepository.getInstance().getRatings(uid, ratings, avgScore, size);
    }

    public RegisteredListener fillAdvertisements(String uid){
        return MainRepository.getInstance().getAdvertisementsFromUser(uid, advertisements);
    }

    public LiveData<User> getUserData() {
        return userData;
    }

    public LiveData<List<Rating>> getRatings() {
        return ratings;
    }

    public LiveData<Double> getAvgScore() {
        return avgScore;
    }

    public LiveData<Integer> getSize() {
        return size;
    }

    public LiveData<List<Advertisement>> getAdvertisements() {
        return advertisements;
    }

    public void sendMessage(Callback<Bundle> callback) {
        MessagingRepository.getInstance().addChatWith(userData.getValue(), callback);
    }
}
