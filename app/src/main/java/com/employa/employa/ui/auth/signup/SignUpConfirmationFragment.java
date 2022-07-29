package com.employa.employa.ui.auth.signup;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.employa.employa.R;
import com.employa.employa.databinding.FragmentSignUpConfirmationBinding;
import com.employa.employa.models.Profile;
import com.employa.employa.repository.AuthenticationRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.navigation.fragment.NavHostFragment;

public class SignUpConfirmationFragment extends SignUpPageFragment implements View.OnClickListener {
    private Button continueButton;
    private AuthenticationRepository mAuthRepo;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment SignUpConfirmationFragment.
     */
    public static SignUpConfirmationFragment newInstance() {
        return new SignUpConfirmationFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FragmentSignUpConfirmationBinding binding = FragmentSignUpConfirmationBinding.inflate(inflater, container, false);
        binding.setViewModel(mViewModel);
        binding.setLifecycleOwner(this);

        mAuthRepo = new AuthenticationRepository(FirebaseAuth.getInstance());

        continueButton = binding.buttonContinue;
        continueButton.setOnClickListener(this);

        return binding.getRoot();
    }

    private void handleContinue() {
        final Profile mProfile = mViewModel.getProfile();
        continueButton.setEnabled(false);
        mListener.setBackEnabled(false);
        mAuthRepo.createUser(mProfile)
                .onSuccessTask(authResult -> {
                    final FirebaseUser user = authResult.getUser();
                    user.sendEmailVerification();
                    if (mProfile.getProfilePicture() != null) {
                        FirebaseStorage storage = FirebaseStorage.getInstance();
                        final StorageReference storageRef = storage.getReference();
                        String extension = mProfile.getProfilePicture().toString().substring(mProfile.getProfilePicture().toString().lastIndexOf("."));
                        final StorageReference ref = storageRef.child("profile_pictures/" + UUID.randomUUID().toString() + extension);
                        return ref.putFile(mProfile.getProfilePicture()).onSuccessTask(taskSnapshot -> mAuthRepo.addToFirebase(user.getUid(), mProfile.getUsername(), mProfile.getDescription(), ref.toString()));
                    } else {
                        return mAuthRepo.addToFirebase(user.getUid(), mProfile.getUsername(), mProfile.getDescription(), null);
                    }
                })
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        mAuthRepo.getAuth().signOut();
                        SignUpFragment fragment = (SignUpFragment) getParentFragment();
                        assert fragment != null;
                        fragment.reset();
                        new AlertDialog.Builder(getContext()).setMessage(R.string.toast_confirm_email)
                                .setPositiveButton(R.string.dialog_ok, null)
                                .show();
                        NavHostFragment.findNavController(this).navigate(R.id.action_signUpFragment_to_overviewFragment);
                    } else if (task.getException() != null) {
                        Toast.makeText(getActivity(), getContext().getString(R.string.toast_sign_up_error) + ": " + task.getException().getLocalizedMessage(), Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getActivity(), getContext().getString(R.string.toast_sign_up_error), Toast.LENGTH_SHORT).show();
                    }
                    continueButton.setEnabled(true);
                });
    }

    @Override
    public void onClick(View v) {
        handleContinue();
    }
}
