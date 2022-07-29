package com.employa.employa.ui.auth.signup;

import android.os.Bundle;

import com.employa.employa.ui.auth.AuthFragmentBase;
import com.employa.employa.viewmodel.SignUpViewModel;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;

abstract class SignUpPageFragment extends AuthFragmentBase {
    SignUpViewModel mViewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = ViewModelProviders.of(requireActivity()).get(SignUpViewModel.class);
    }
}
