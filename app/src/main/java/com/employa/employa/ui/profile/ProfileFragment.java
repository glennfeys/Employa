package com.employa.employa.ui.profile;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.employa.employa.R;
import com.employa.employa.databinding.FragmentProfileBinding;
import com.employa.employa.models.Advertisement;
import com.employa.employa.repository.Callback;
import com.employa.employa.ui.components.ViewType;
import com.employa.employa.repository.MainRepository;
import com.employa.employa.repository.RegisteredListener;
import com.employa.employa.ui.discovery.AdvertisementDetailsFragment;
import com.employa.employa.ui.discovery.ButtonClickListener;
import com.employa.employa.ui.discovery.adapter.AdvertisementAdapter;
import com.employa.employa.viewmodel.ProfileViewModel;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Locale;

public class ProfileFragment extends Fragment implements ProfileListener, ButtonClickListener {
    public static final String UID_BUNDLE = "uid";

    private ProfileViewModel viewModel;
    private RegisteredListener advertisementListener;
    private RegisteredListener ratingsListener;
    private String uid;

    public static ProfileFragment newInstance() {
        return new ProfileFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(this).get(ProfileViewModel.class);

        if (getArguments() == null) {
            uid = FirebaseAuth.getInstance().getUid();
        } else {
            uid = getArguments().getString(MainRepository.UID_BUNDLE);
        }

        viewModel.fillUser(uid);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FragmentProfileBinding binding = FragmentProfileBinding.inflate(inflater, container, false);
        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(this);
        binding.setActionHandler(this);

        advertisementListener = viewModel.fillAdvertisements(uid);
        ratingsListener = viewModel.fillRatings(uid);

        viewModel.getUserData().observe(this, user -> {
            if(user == null) return; // User isn't loaded

            Glide.with(binding.profilePic.getContext())
                    .load(user.getProfilePicDownload())
                    .into(binding.profilePic);

            if (MainRepository.getInstance().isMe(user.getId())){
                binding.contactRateButtons.setVisibility(View.GONE);
                binding.editButton.setVisibility(View.VISIBLE);
            } else {
                binding.backButton.setVisibility(View.VISIBLE);
            }

            AdvertisementAdapter advertisementsAdapter = new AdvertisementAdapter(getContext(), ViewType.ADVERTISEMENT_SMALL, this);
            binding.advertisementList.setAdapter(advertisementsAdapter);
            viewModel.getAdvertisements().observe(this, advertisements -> {
                advertisementsAdapter.submitList(advertisements);
                advertisementsAdapter.notifyDataSetChanged();
            });
        });

        binding.avgScore.setText(getString(R.string.no_average_score));
        viewModel.getSize().observe(this, amount ->
            binding.avgScore.setText(
                getResources().getQuantityString(
                    R.plurals.average_score, amount, String.format(Locale.getDefault(), "%.1f", viewModel.getAvgScore().getValue()), amount
                )
            )
        );

        binding.advertisementTitle.setText(MainRepository.getInstance().isMe(uid) ? getString(R.string.profile_my_ads) : getString(R.string.profile_ads));
        binding.advertisementSubTitle.setText(MainRepository.getInstance().isMe(uid) ? getString(R.string.profile_no_my_ads) : getString(R.string.profile_no_ads));

        return binding.getRoot();
    }

    @Override
    public void editProfile() {
        Bundle bundle = new Bundle();
        bundle.putString(MainRepository.UID_BUNDLE, viewModel.getUserData().getValue().getId());
        NavHostFragment.findNavController(ProfileFragment.this).navigate(R.id.action_profile_to_edit_profile, bundle);
    }

    @Override
    public void addRating() {
        Bundle bundle = new Bundle();
        bundle.putString(MainRepository.UID_BUNDLE, uid);

        NavController navController = NavHostFragment.findNavController(ProfileFragment.this);
        navController.navigate(R.id.action_profile_to_rating_form, bundle);
    }

    @Override
    public void react() {
        viewModel.sendMessage(bundle ->
                NavHostFragment.findNavController(ProfileFragment.this).navigate(R.id.action_navigation_profile_to_chatFragment, bundle));
    }

    @Override
    public void goBack() {
        NavHostFragment.findNavController(this).popBackStack();
    }

    @Override
    public void onClickRatings() {
        if(viewModel.getSize().getValue() == null || viewModel.getSize().getValue() == 0) return;

        Bundle bundle = new Bundle();
        bundle.putString(UID_BUNDLE, uid);

        NavHostFragment.findNavController(this).navigate(R.id.action_profile_to_ratings, bundle);
    }

    @Override
    public void onMoreInfoClicked(Advertisement advertisement) {
        Bundle bundle = new Bundle();
        bundle.putString(AdvertisementDetailsFragment.ADV_ID_BUNDLE_NAME, advertisement.getId());

        NavHostFragment.findNavController(this).navigate(R.id.action_profile_to_details, bundle);
    }

    @Override
    public void onEditClicked(Advertisement advertisement) {
        Bundle bundle = new Bundle();
        bundle.putString(MainRepository.BUNDLE_ADVERTISEMENT_ID, advertisement.getId());

        NavHostFragment.findNavController(ProfileFragment.this).navigate(R.id.action_navigation_profile_to_editAdvertisementFragment, bundle);
    }

    @Override
    public void onDeleteClicked(Advertisement advertisement) {
        final MainRepository repo = MainRepository.getInstance();
        repo.deleteAdvertisement(advertisement, new Callback<Task>() {
            @Override
            public void onSuccess(Task task) {
                Toast.makeText(getContext(), "Verwijdered!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFail() {
                Toast.makeText(getContext(), "Verwijderen mislukt!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        advertisementListener.remove();
        ratingsListener.remove();
    }
}
