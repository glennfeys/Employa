package com.employa.employa.ui.auth.signup;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.employa.employa.R;
import com.employa.employa.databinding.FragmentSignUpOptionalsBinding;
import com.employa.employa.ui.components.ProfilePictureChooser;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

public class SignUpOptionalsFragment extends SignUpPageFragment implements ProfilePictureChooser, View.OnClickListener {
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment SignUpOptionalsFragment.
     */
    public static SignUpOptionalsFragment newInstance() {
        return new SignUpOptionalsFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FragmentSignUpOptionalsBinding binding = FragmentSignUpOptionalsBinding.inflate(inflater, container, false);
        binding.setViewModel(mViewModel);
        binding.setLifecycleOwner(this);
        binding.setProfilePictureChooser(this);
        binding.buttonContinue.setOnClickListener(this);
        return binding.getRoot();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        onActivityResultPictureHandler(requestCode, resultCode, data);
    }

    @Override
    public void setProfilePicture(Uri uri) {
        mViewModel.getProfilePictureMutableData().postValue(uri);
    }

    @Override
    public void onClick(View v) {
        ViewPager pager = requireActivity().findViewById(R.id.sign_up_view_pager);
        pager.setCurrentItem(pager.getCurrentItem() + 1);
    }
}
