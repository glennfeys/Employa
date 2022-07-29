package com.employa.employa.viewmodel;

import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.widget.ImageView;

import com.employa.employa.R;
import com.employa.employa.models.Profile;
import com.employa.employa.utility.Validators;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import java.io.IOException;

import androidx.databinding.BindingAdapter;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SignUpViewModel extends ViewModel {
    private MutableLiveData<String> name = new MutableLiveData<>();
    private MutableLiveData<Integer> usernameError = new MutableLiveData<>();
    private MutableLiveData<String> email = new MutableLiveData<>();
    private MutableLiveData<Integer> emailError = new MutableLiveData<>();
    private MutableLiveData<String> password = new MutableLiveData<>();
    private MutableLiveData<Integer> passwordError = new MutableLiveData<>();

    private MutableLiveData<Uri> profilePicture = new MutableLiveData<>();
    private MutableLiveData<String> description = new MutableLiveData<>();

    public SignUpViewModel() {
        name.setValue("");
        email.setValue("");
        password.setValue("");
    }

    private boolean isValidEmail() {
        boolean check = Validators.isValidEmail(email.getValue());
        emailError.setValue(check ? null : R.string.error_field_email);
        return check;
    }

    private boolean isValidPassword() {
        boolean check = Validators.isValidPassword(password.getValue());
        passwordError.setValue(check ? null : R.string.error_field_password);
        return check;
    }

    private boolean isValidName() {
        boolean check = Validators.isValidName(name.getValue());
        usernameError.setValue(check ? null : R.string.name_requirements);
        return check;
    }

    public MutableLiveData<String> getNameMutableData() {
        return name;
    }

    public LiveData<Integer> getUsernameError() {
        return usernameError;
    }

    public MutableLiveData<String> getEmailMutableData() {
        return email;
    }

    public LiveData<Integer> getEmailError() {
        return emailError;
    }

    public MutableLiveData<String> getPasswordMutableData() {
        return password;
    }

    public LiveData<Integer> getPasswordError() {
        return passwordError;
    }

    public MutableLiveData<Uri> getProfilePictureMutableData() {
        return profilePicture;
    }

    public MutableLiveData<String> getDescriptionMutableData() {
        return description;
    }

    @BindingAdapter({"imageUrl"})
    public static void loadImage(ImageView view, Uri imageUrl) {
        if (imageUrl != null) {
            Bitmap bitmap;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(view.getContext().getContentResolver(), imageUrl);
                view.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public Profile getProfile() {
        Profile result = new Profile(name.getValue(), email.getValue(), password.getValue());
        if (!TextUtils.isEmpty(description.getValue()))
            result.setDescription(description.getValue().replace("\n", ""));
        result.setProfilePicture(profilePicture.getValue());
        return result;
    }

    public Task<Boolean> isRegisteredEmail() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String s = email.getValue();
        assert s != null;
        return auth.fetchSignInMethodsForEmail(s).continueWith(task -> {
            if (task.isSuccessful() && task.getResult() != null
                    && task.getResult().getSignInMethods() != null) {
                if (task.getResult().getSignInMethods().size() != 0) {
                    emailError.setValue(R.string.error_email_registered);
                    return true;
                }
            }
            return false;
        });
    }

    public boolean validate() {
        boolean email = isValidEmail();
        boolean username = isValidName();
        boolean password = isValidPassword();
        return email && username && password;
    }

    public void reset() {
        name.setValue("");
        email.setValue("");
        password.setValue("");
        profilePicture.setValue(null);
        description.setValue(null);

        emailError.setValue(null);
        passwordError.setValue(null);
        usernameError.setValue(null);
    }
}
