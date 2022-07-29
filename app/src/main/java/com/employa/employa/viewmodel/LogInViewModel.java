package com.employa.employa.viewmodel;

import android.text.TextUtils;

import com.employa.employa.R;
import com.employa.employa.models.AuthenticationUser;
import com.employa.employa.utility.Validators;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class LogInViewModel extends ViewModel {
    private MutableLiveData<String> email = new MutableLiveData<>();
    private MutableLiveData<Integer> emailError = new MutableLiveData<>();
    private MutableLiveData<String> password = new MutableLiveData<>();
    private MutableLiveData<Integer> passwordError = new MutableLiveData<>();

    public MutableLiveData<String> getEmailMutableData() { return  email;}
    public MutableLiveData<Integer> getEmailError() { return  emailError;}
    public MutableLiveData<String> getPasswordMutableData() { return  password;}
    public MutableLiveData<Integer> getPasswordError() { return  passwordError;}

    public boolean isValidEmail() {
        String s = email.getValue();
        if(TextUtils.isEmpty(s) || !Validators.isValidEmail(s)) {
            emailError.setValue(R.string.error_field_email);
            return false;
        }
        emailError.setValue(null);
        return true;
    }

    private boolean isValidPassword() {
        String s = password.getValue();
        if(TextUtils.isEmpty(s) || s.length() <= 5){
            passwordError.setValue(R.string.error_field_password);
            return false;
        }
        passwordError.setValue(null);
        return true;
    }


    private boolean validate() {
        boolean email = isValidEmail();
        boolean password = isValidPassword();
        return email && password;
    }

    public AuthenticationUser getUser() {
        AuthenticationUser result = null;
        if(validate()) {
            result = new AuthenticationUser(email.getValue(), password.getValue());
        }
        return result;
    }
}
