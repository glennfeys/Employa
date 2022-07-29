package com.employa.employa.ui.auth;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.employa.employa.R;
import com.employa.employa.databinding.FragmentAuthenticationOverviewBinding;

import androidx.annotation.NonNull;
import androidx.navigation.fragment.NavHostFragment;

public class AuthenticationOverview extends AuthFragmentBase {
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment AuthenticationOverview.
     */
    public static AuthenticationOverview newInstance() {
        return new AuthenticationOverview();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FragmentAuthenticationOverviewBinding binding = FragmentAuthenticationOverviewBinding.inflate(inflater, container, false);
        binding.buttonSignup.setOnClickListener(v -> onSignUpPressed());
        binding.buttonSignin.setOnClickListener(v -> onSignInPressed());

        mListener.setBackEnabled(false);

        return binding.getRoot();
    }

    private void onSignUpPressed() {
        NavHostFragment.findNavController(this).navigate(R.id.action_authenticationOverviewFragment_to_signUpFragment);
    }

    private void onSignInPressed() {
        NavHostFragment.findNavController(this).navigate(R.id.action_authenticationOverviewFragment_to_signInFragment);
    }
}
