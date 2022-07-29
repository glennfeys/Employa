package com.employa.employa.ui.settings;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.employa.employa.R;
import com.employa.employa.databinding.FragmentChangePasswordBinding;
import com.employa.employa.repository.AuthenticationRepository;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class ChangePasswordFragment extends Fragment implements View.OnClickListener {
    private AuthenticationRepository mAuthRepo;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuthRepo = new AuthenticationRepository(FirebaseAuth.getInstance());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FragmentChangePasswordBinding binding = FragmentChangePasswordBinding.inflate(inflater, container, false);
        binding.confirmButton.setOnClickListener(this);
        return binding.getRoot();
    }

    @Override
    public void onClick(View v) {
        String email = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail();
        if (email != null) {
            AlertDialog.Builder alert = new AlertDialog.Builder(getContext());

            alert.setMessage(getString(R.string.confirm_change_password));

            //customize alert dialog to allow desired input
            alert.setNegativeButton(getString(R.string.cancel), (dialog, which) -> dialog.dismiss());
            alert.setPositiveButton(getString(R.string.dialog_ok), (dialog, which) -> {
                mAuthRepo.getAuth().sendPasswordResetEmail(email);
                Toast.makeText(getActivity(), Objects.requireNonNull(getContext()).getString(R.string.toast_reset_password),
                        Toast.LENGTH_LONG).show();
            });
            alert.show();
        }
    }
}
