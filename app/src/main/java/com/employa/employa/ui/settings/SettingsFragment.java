package com.employa.employa.ui.settings;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.employa.employa.R;
import com.employa.employa.databinding.FragmentSettingsBinding;
import com.employa.employa.ui.StartActivity;
import com.google.firebase.auth.FirebaseAuth;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

public class SettingsFragment extends Fragment implements SettingsListener {
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FragmentSettingsBinding binding = FragmentSettingsBinding.inflate(inflater, container, false);
        binding.setActionHandler(this);
        return binding.getRoot();
    }

    @Override
    public void logOut() {
        FirebaseAuth.getInstance().signOut();

        startActivity(new Intent(getActivity(), StartActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
        requireActivity().finish();
    }

    @Override
    public void changeEmail() {
        NavController navController = NavHostFragment.findNavController(SettingsFragment.this);
        navController.navigate(R.id.action_navigation_settings_to_changeEmailFragment);
    }

    @Override
    public void changePassword() {
        NavController navController = NavHostFragment.findNavController(SettingsFragment.this);
        navController.navigate(R.id.action_navigation_settings_to_changePasswordFragment);
    }
}
