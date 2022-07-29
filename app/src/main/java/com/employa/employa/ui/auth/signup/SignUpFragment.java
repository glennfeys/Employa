package com.employa.employa.ui.auth.signup;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.employa.employa.databinding.FragmentSignUpBinding;
import com.employa.employa.ui.auth.AuthFragmentBase;
import com.employa.employa.viewmodel.SignUpViewModel;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.lifecycle.ViewModelProviders;

public class SignUpFragment extends AuthFragmentBase {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        FragmentSignUpBinding binding = FragmentSignUpBinding.inflate(inflater, container, false);

        binding.signUpViewPager.setAdapter(new PageAdapter(getChildFragmentManager()));
        binding.signUpViewPager.setOnTouchListener((v, event) -> true);
        binding.setLifecycleOwner(this);

        mListener.setBackEnabled(true);

        return binding.getRoot();
    }

    public void reset() {
        SignUpViewModel viewModel = ViewModelProviders.of(requireActivity()).get(SignUpViewModel.class);
        viewModel.reset();
    }

    class PageAdapter extends FragmentPagerAdapter {
        private static final int NUM_PAGES = 3;

        PageAdapter(FragmentManager m) {
            super(m, FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        }

        @Override
        @NonNull
        public Fragment getItem(int position) {
            switch (position) {
                case 0: return SignUpGeneralFragment.newInstance();
                case 1: return SignUpOptionalsFragment.newInstance();
                case 2: return SignUpConfirmationFragment.newInstance();
                default: throw new IllegalStateException();
            }
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }
}
