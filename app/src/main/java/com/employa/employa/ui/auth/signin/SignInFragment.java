package com.employa.employa.ui.auth.signin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.employa.employa.R;
import com.employa.employa.databinding.FragmentSignInBinding;
import com.employa.employa.models.AuthenticationUser;
import com.employa.employa.repository.AuthenticationRepository;
import com.employa.employa.ui.auth.AuthFragmentBase;
import com.employa.employa.ui.components.EditorConfirmHandler;
import com.employa.employa.viewmodel.LogInViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;

public class SignInFragment extends AuthFragmentBase {
    private LogInViewModel mViewModel;
    private AuthenticationRepository mAuthRepo;
    private Button signInButton;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(LogInViewModel.class);
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment SignInFragment.
     */
    public static SignInFragment newInstance() {
        return new SignInFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FragmentSignInBinding binding = FragmentSignInBinding.inflate(inflater, container, false);
        binding.setViewModel(mViewModel);
        binding.setLifecycleOwner(this);

        mAuthRepo = new AuthenticationRepository(FirebaseAuth.getInstance());

        mListener.setBackEnabled(true);

        // Attach listeners
        signInButton = binding.buttonSignin;
        binding.buttonSignin.setOnClickListener(v -> handleSignIn());
        binding.buttonReset.setOnClickListener(v -> handleResetPassword());
        binding.passwordField.setOnEditorActionListener(new EditorConfirmHandler(this::handleSignIn, getActivity(), binding.getRoot()));

        return binding.getRoot();
    }

    private void handleSignIn() {
        AuthenticationUser authenticationUser = mViewModel.getUser();
        if(authenticationUser == null) return;

        signInButton.setEnabled(false);

        mAuthRepo.signInUser(authenticationUser)
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    FirebaseUser user = task.getResult().getUser();
                    if (user.isEmailVerified()) {
                        Toast.makeText(getActivity(), getContext().getString(R.string.toast_sign_in),
                                Toast.LENGTH_SHORT).show();
                        notifyListener();
                    } else {
                        user.sendEmailVerification();
                        mAuthRepo.getAuth().signOut();
                        Toast.makeText(getActivity(), getContext().getString(R.string.toast_confirm_email),
                                Toast.LENGTH_LONG).show();

                    }
                } else if (task.getException() != null) {
                    Toast.makeText(getActivity(), getContext().getString(R.string.toast_sign_in_error) + ": " + task.getException().getLocalizedMessage(),
                            Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getActivity(), getContext().getString(R.string.toast_sign_in_error), Toast.LENGTH_SHORT).show();
                }
                signInButton.setEnabled(true);
            });
    }

    private void handleResetPassword() {
        String email = mViewModel.getEmailMutableData().getValue();

        if (mViewModel.isValidEmail()) {
            mAuthRepo.getAuth().sendPasswordResetEmail(email);
            Toast.makeText(getActivity(), getContext().getString(R.string.toast_reset_password),
                    Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getActivity(), getContext().getString(R.string.toast_reset_password_error),
                    Toast.LENGTH_LONG).show();
        }
    }
}
