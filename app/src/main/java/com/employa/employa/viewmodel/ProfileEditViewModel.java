package com.employa.employa.viewmodel;

import android.net.Uri;
import android.widget.ImageView;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.employa.employa.R;
import com.employa.employa.models.User;
import com.employa.employa.repository.Callback;
import com.employa.employa.repository.MainRepository;
import com.employa.employa.utility.Validators;
import com.google.android.gms.tasks.Task;

public class ProfileEditViewModel extends ViewModel {
    private MutableLiveData<User> user = new MutableLiveData<>();
    private MutableLiveData<String> photoRef = new MutableLiveData<>();
    private MutableLiveData<Integer> usernameError = new MutableLiveData<>();
    private MutableLiveData<String> name = new MutableLiveData<>();
    private MutableLiveData<String> description = new MutableLiveData<>();

    public LiveData<User> getUser() {
        return user;
    }

    public void setUser(String uid) {
        MainRepository.getInstance().getEditUser(uid, user, name, description);
    }

    public void editPhoto(Uri photoUri, ImageView imageView){
        MainRepository.getInstance().editPhoto(photoUri, imageView, photoRef);
    }

    public void editUser(String uid, String name, String description, Callback<Task> cb) {
        String photo = photoRef.getValue();
        if (photo == null) {
            assert user.getValue() != null;
            photo = user.getValue().getProfilePicture();
        }

        MainRepository.getInstance().editUser(uid, name, description, photo, cb);
    }

    public boolean checkValidity(String s) {
        boolean check = Validators.isValidName(s);
        usernameError.setValue(check ? null : R.string.name_requirements);
        return check;
    }

    public LiveData<Integer> getUsernameError() {
        return usernameError;
    }

    public MutableLiveData<String> getName() {
        return name;
    }

    public void setName(MutableLiveData<String> name) {
        this.name = name;
    }

    public MutableLiveData<String> getDescription() {
        return description;
    }

    public void setDescription(MutableLiveData<String> description) {
        this.description = description;
    }
}
