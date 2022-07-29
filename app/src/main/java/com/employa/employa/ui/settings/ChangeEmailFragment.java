package com.employa.employa.ui.settings;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.employa.employa.R;
import com.employa.employa.databinding.FragmentChangeEmailBinding;
import com.employa.employa.repository.Callback;
import com.employa.employa.repository.MainRepository;
import com.employa.employa.ui.StartActivity;
import com.employa.employa.viewmodel.ChangeEmailViewmodel;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ChangeEmailFragment extends Fragment implements View.OnClickListener, Callback<Task> {
    private ChangeEmailViewmodel viewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(this).get(ChangeEmailViewmodel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FragmentChangeEmailBinding binding = FragmentChangeEmailBinding.inflate(inflater, container, false);
        binding.setViewModel(viewModel);
        binding.changeButton.setOnClickListener(this);
        return binding.getRoot();
    }

    @Override
    public void onClick(View v) {
        if (viewModel.getPassword().getValue().length() == 0 || viewModel.getNewEmail().getValue().length() == 0) {
            Toast.makeText(getContext(), getString(R.string.make_sure_everything_is_filled_in), Toast.LENGTH_SHORT).show();
            return;
        }

        MainRepository repo = MainRepository.getInstance();
        String newEmail = viewModel.getNewEmail().getValue();
        String password = viewModel.getPassword().getValue();
        repo.updateEmail(newEmail, password, this);
    }

    @Override
    public void onSuccess(Task task) {
        viewModel.setCurrentEmail(viewModel.getNewEmail().getValue());
        Toast.makeText(getContext(), getString(R.string.toast_email_changed), Toast.LENGTH_LONG).show();
        FirebaseAuth.getInstance().signOut();

        startActivity(new Intent(getActivity(), StartActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
        requireActivity().finish();
    }

    @Override
    public void onFail() {
        Toast.makeText(getContext(), getString(R.string.toast_email_incorrect), Toast.LENGTH_LONG).show();
    }
}
