package com.employa.employa.ui.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.employa.employa.R;
import com.employa.employa.databinding.FragmentRatingFormBinding;
import com.employa.employa.repository.Callback;
import com.employa.employa.repository.MainRepository;
import com.employa.employa.viewmodel.RatingFormViewModel;
import com.google.android.gms.tasks.Task;

public class RatingFormFragment extends Fragment implements RatingFormListener, Callback<Task> {
    public static final String RATING_ID_BUNDLE = "ratingID";
    private RatingFormViewModel ratingFormViewModel;
    private FragmentRatingFormBinding binding;
    private String uid;
    private String ratingID;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ratingFormViewModel = ViewModelProviders.of(this).get(RatingFormViewModel.class);
        uid = requireArguments().getString(MainRepository.UID_BUNDLE);
        ratingID = requireArguments().getString(RATING_ID_BUNDLE);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentRatingFormBinding.inflate(inflater, container, false);
        binding.setLifecycleOwner(this);
        binding.setActionHandler(this);
        binding.setViewModel(ratingFormViewModel);

        if (ratingID != null && ratingFormViewModel.getRating().getValue() == null){
            ratingFormViewModel.setRating(uid, ratingID);
        }

        binding.scoreInfo.setText(getString(R.string.rating_form_score, 0));
        ratingFormViewModel.getScore().observe(this, score -> {
            binding.scoreInfo.setText(getString(R.string.rating_form_score, ratingFormViewModel.getScore().getValue()));
        });

        return binding.getRoot();
    }

    private boolean isValid() {
        if (!ratingFormViewModel.isValidDescription(binding.description.getText().toString())) {
            Toast.makeText(getContext(), getResources().getString(R.string.toast_check_form), Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    @Override
    public void createRating() {
        if (isValid())
            ratingFormViewModel.createRating(uid, binding.score.getProgress(), binding.description.getText().toString(), this);
    }

    @Override
    public void editRating() {
        if (isValid())
            ratingFormViewModel.editRating(uid, ratingID, binding.score.getProgress(), binding.description.getText().toString(), this);
    }

    @Override
    public void onSuccess(Task task) {
        NavController navController = NavHostFragment.findNavController(RatingFormFragment.this);
        navController.popBackStack();
    }

    @Override
    public void onFail() {
        Toast.makeText(getContext(), getResources().getString(R.string.something_went_wrong), Toast.LENGTH_LONG).show();
    }
}
