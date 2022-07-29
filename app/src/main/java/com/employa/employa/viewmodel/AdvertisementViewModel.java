package com.employa.employa.viewmodel;

import android.net.Uri;
import android.os.Bundle;

import com.employa.employa.models.Advertisement;
import com.employa.employa.models.User;
import com.employa.employa.repository.Callback;
import com.employa.employa.repository.MainRepository;
import com.employa.employa.repository.MessagingRepository;
import com.google.android.gms.maps.model.LatLng;

import java.util.Objects;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class AdvertisementViewModel extends ViewModel {
    private MutableLiveData<User> user;
    private MutableLiveData<Advertisement> advertisement;
    private MutableLiveData<Double> avgScore;
    private MutableLiveData<Integer> avgScoreSize;

    public AdvertisementViewModel() {
        user = new MutableLiveData<>();
        advertisement = new MutableLiveData<>();
        avgScore = new MutableLiveData<>();
        avgScoreSize = new MutableLiveData<>();
        avgScoreSize.setValue(0);
        avgScore.setValue(0.0);
    }

    public void setAdvertisement(String advertisementID) {
        MainRepository.getInstance().setAdvertisement(advertisementID, advertisement, user, avgScore, avgScoreSize);
    }

    public LiveData<Double> getUserAvgScore() {
        return avgScore;
    }

    public LiveData<Integer> getUserAvgScoreSize() { return avgScoreSize; }

    public Uri getProfilePic(){
        return user.getValue().getProfilePicDownload();
    }

    public LatLng getAdLocation() {
        return new LatLng(Objects.requireNonNull(advertisement.getValue()).getLocation().getLatitude(), advertisement.getValue().getLocation().getLongitude());
    }

    public LiveData<User> getUser() {
        return user;
    }

    public void setUser(MutableLiveData<User> user) {
        this.user = user;
    }

    public LiveData<Advertisement> getAdvertisement() {
        return advertisement;
    }

    public void reactTo(Callback<Bundle> callback) {
        MessagingRepository.getInstance().addChatWith(user.getValue(), callback);
    }
}
