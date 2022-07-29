package com.employa.employa.ui.auth.signup;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.employa.employa.R;
import com.employa.employa.databinding.FragmentSignUpGeneralBinding;
import com.employa.employa.ui.components.EditorConfirmHandler;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.ViewPager;

public class SignUpGeneralFragment extends SignUpPageFragment implements View.OnClickListener {

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment SignUpGeneralFragment.
     */
    public static SignUpGeneralFragment newInstance() {
        return new SignUpGeneralFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FragmentSignUpGeneralBinding binding = FragmentSignUpGeneralBinding.inflate(inflater, container, false);
        binding.setViewModel(mViewModel);
        binding.setLifecycleOwner(this);

        binding.buttonSignup.setOnClickListener(this);

        binding.passwordField.setOnEditorActionListener(new EditorConfirmHandler(this::handleContinue, getActivity(), binding.getRoot()));

        return binding.getRoot();
    }

    private void handleContinue() {
        if (mViewModel.validate()) {
            mViewModel.isRegisteredEmail().addOnSuccessListener(registered -> {
                if(!registered){
                    ViewPager pager = requireActivity().findViewById(R.id.sign_up_view_pager);
                    pager.setCurrentItem(pager.getCurrentItem() + 1);
                }
                else{
                    Toast.makeText(getContext(), getResources().getString(R.string.toast_check_form), Toast.LENGTH_LONG).show();
                }
            });
        } else {
            Toast.makeText(getContext(), getResources().getString(R.string.toast_check_form), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onClick(View v) {
        handleContinue();
    }
}
