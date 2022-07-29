package com.employa.employa.ui.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.fragment.NavHostFragment;

import com.employa.employa.R;
import com.employa.employa.databinding.FragmentRatingsOverviewBinding;
import com.employa.employa.models.Rating;
import com.employa.employa.ui.components.BackButtonListener;
import com.employa.employa.ui.profile.adapter.RatingsAdapter;
import com.employa.employa.viewmodel.ProfileViewModel;

public class RatingsOverviewFragment extends Fragment implements RatingsOverviewListener, BackButtonListener {

    private ProfileViewModel profileViewModel;
    private String uid;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        profileViewModel = ViewModelProviders.of(this).get(ProfileViewModel.class);
        uid = requireArguments().getString(ProfileFragment.UID_BUNDLE);
        profileViewModel.fillRatings(uid);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FragmentRatingsOverviewBinding binding = FragmentRatingsOverviewBinding.inflate(inflater, container, false);
        binding.setViewModel(profileViewModel);
        binding.setLifecycleOwner(this);
        binding.setBackButtonHandler(this);

        RatingsAdapter ratingsAdapter = new RatingsAdapter(getContext(), this);
        binding.ratingList.setAdapter(ratingsAdapter);
        profileViewModel.getRatings().observe(this, ratings -> {
            ratingsAdapter.submitList(ratings);
            ratingsAdapter.notifyItemInserted(ratings.size() - 1);
        });

        return binding.getRoot();
    }

    @Override
    public void editRating(Rating rating) {
        Bundle bundle = new Bundle();
        bundle.putString(ProfileFragment.UID_BUNDLE, uid);
        bundle.putString(RatingFormFragment.RATING_ID_BUNDLE, rating.getId());

        NavHostFragment.findNavController(this).navigate(R.id.action_ratings_to_rating_form, bundle);
    }

    @Override
    public void goToProfile(String uid) {
        Bundle bundle = new Bundle();
        bundle.putString(ProfileFragment.UID_BUNDLE, uid);
        NavHostFragment.findNavController(this).navigate(R.id.action_ratings_to_profile, bundle);
    }

    @Override
    public void goBack() {
        NavHostFragment.findNavController(this).popBackStack();
    }
}
