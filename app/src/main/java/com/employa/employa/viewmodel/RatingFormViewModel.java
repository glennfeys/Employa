package com.employa.employa.viewmodel;

import android.text.TextUtils;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.employa.employa.R;
import com.employa.employa.models.Rating;
import com.employa.employa.repository.Callback;
import com.employa.employa.repository.MainRepository;
import com.google.android.gms.tasks.Task;

public class RatingFormViewModel extends ViewModel {
    private MutableLiveData<Rating> rating = new MutableLiveData<>();
    private MutableLiveData<Integer> descriptionError = new MutableLiveData<>();
    private MutableLiveData<String> description = new MutableLiveData<>();
    private MutableLiveData<Integer> score = new MutableLiveData<>();

    public void setRating(String uid, String ratingID){
        MainRepository.getInstance().getRating(uid, ratingID, rating, description, score);
    }

    public MutableLiveData<Rating> getRating() {
        return rating;
    }

    public void createRating(String uid, int score, String description, Callback<Task> cb) {
        MainRepository.getInstance().createRating(uid, score, description, cb);
    }

    public void editRating(String uid, String ratingID, int score, String description, Callback<Task> cb) {
        MainRepository.getInstance().editRating(uid, ratingID, score, description, cb);
    }

    public boolean isValidDescription(String s) {
        if (TextUtils.isEmpty(s)) {
            descriptionError.setValue(R.string.form_description_requirements);
            return false;
        }
        descriptionError.setValue(null);
        return true;
    }

    public LiveData<Integer> getDescriptionError() {
        return descriptionError;
    }

    public MutableLiveData<String> getDescription() {
        return description;
    }

    public void setDescription(MutableLiveData<String> description) {
        this.description = description;
    }

    public MutableLiveData<Integer> getScore() {
        return score;
    }

    public void setScore(MutableLiveData<Integer> score) {
        this.score = score;
    }
}
