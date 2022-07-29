package com.employa.employa.ui.profile;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.fragment.NavHostFragment;

import com.bumptech.glide.Glide;
import com.employa.employa.R;
import com.employa.employa.databinding.FragmentProfileEditBinding;
import com.employa.employa.repository.Callback;
import com.employa.employa.repository.MainRepository;
import com.employa.employa.ui.components.ProfilePictureChooser;
import com.employa.employa.utility.Helpers;
import com.employa.employa.viewmodel.ProfileEditViewModel;
import com.google.android.gms.tasks.Task;

public class ProfileEditFragment extends Fragment implements ProfileEditListener, ProfilePictureChooser {
    private ProfileEditViewModel profileEditViewModel;
    private ImageView imageView;

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        onActivityResultPictureHandler(requestCode, resultCode, data);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Helpers.restoreDefaultKeyboardMode(this);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        profileEditViewModel = ViewModelProviders.of(this).get(ProfileEditViewModel.class);
        if (profileEditViewModel.getUser().getValue() == null) profileEditViewModel.setUser(requireArguments().getString(MainRepository.UID_BUNDLE));

        Helpers.setKeyboardMode(this, WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FragmentProfileEditBinding binding = FragmentProfileEditBinding.inflate(inflater, container, false);
        binding.setViewModel(profileEditViewModel);
        binding.setLifecycleOwner(this);
        binding.setActionHandler(this);
        binding.setProfilePictureChooser(this);

        imageView = binding.profilePicture;

        profileEditViewModel.getUser().observe(this, user -> {
            if (user == null) return;

            Glide.with(imageView.getContext())
                .load(user.getProfilePicDownload())
                .into(imageView);
        });

        return binding.getRoot();
    }

    @Override
    public void onSubmit(String name, String description) {
        if (!profileEditViewModel.checkValidity(name)) {
            Toast.makeText(getContext(), getResources().getString(R.string.toast_check_form), Toast.LENGTH_LONG).show();
            return;
        }

        profileEditViewModel.editUser(profileEditViewModel.getUser().getValue().getId(), name, description, new Callback<Task>() {
            @Override
            public void onSuccess(Task o) {
                NavHostFragment.findNavController(ProfileEditFragment.this).navigate(R.id.action_edit_profile_to_profile);
            }

            @Override
            public void onFail() {
                Toast.makeText(getContext(), getResources().getString(R.string.something_went_wrong), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void setProfilePicture(Uri uri) {
        profileEditViewModel.editPhoto(uri, imageView);
    }
}
