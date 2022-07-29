package com.employa.employa.ui.discovery;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.employa.employa.R;
import com.employa.employa.databinding.FragmentAdvertisementDetailsBinding;
import com.employa.employa.repository.MainRepository;
import com.employa.employa.ui.discovery.adapter.DateAdapter;
import com.employa.employa.viewmodel.AdvertisementViewModel;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.MarkerOptions;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.List;
import java.util.Locale;

public class AdvertisementDetailsFragment extends Fragment implements OnMapReadyCallback, AdvertisementDetailsListener {
    public static final String ADV_ID_BUNDLE_NAME = "advertisementID";
    private AdvertisementViewModel viewModel;

    private Geocoder geocoder;
    private GoogleMap mMap;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(this).get(AdvertisementViewModel.class);
        geocoder = new Geocoder(getContext(), Locale.getDefault());
        viewModel.setAdvertisement(requireArguments().getString(ADV_ID_BUNDLE_NAME));
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final FragmentAdvertisementDetailsBinding binding = FragmentAdvertisementDetailsBinding.inflate(inflater, container, false);
        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(this);
        binding.setActionHandler(this);

        viewModel.getUserAvgScore().observe(this, score -> {
            int amount = viewModel.getUserAvgScoreSize().getValue();

            // https://stackoverflow.com/questions/17261290/plural-definition-is-ignored-for-zero-quantity/17261327
            if(amount == 0) {
                binding.score.setText(getString(R.string.no_average_score));
            } else {
                binding.score.setText(getResources().getQuantityString(R.plurals.average_score, amount, String.format(Locale.getDefault(), "%.1f", score), amount));
            }
        });

        viewModel.getUser().observe(this, user -> {
            if(user != null) {
                Glide.with(binding.picture.getContext())
                        .load(viewModel.getProfilePic())
                        .into(binding.picture);

                // Hide react button if it's your own advertisement
                if (!MainRepository.getInstance().isMe(user.getId())) {
                    binding.reactButton.setVisibility(View.VISIBLE);
                }
            }
        });

        DateAdapter dateAdapter = new DateAdapter(getContext());
        binding.dateList.setAdapter(dateAdapter);
        binding.dateList.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        viewModel.getAdvertisement().observe(this, advertisement -> {
            dateAdapter.submitList(advertisement.getTimestamps());
            dateAdapter.notifyItemInserted(advertisement.getTimestamps().size() - 1);

            binding.dateList.setVisibility(advertisement.getTimestamps().isEmpty() ? View.GONE : View.VISIBLE);
            binding.detailsDateDefault.setVisibility(advertisement.getTimestamps().isEmpty() ? View.VISIBLE : View.GONE);

            try {
                List<Address> addresses = geocoder.getFromLocation(advertisement.getLocation().getLatitude(), advertisement.getLocation().getLongitude(), 1);
                binding.addressString.setText(addresses.get(0).getAddressLine(0));
            } catch(Exception e) {}
        });

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

        return binding.getRoot();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker on location and move the camera
        viewModel.getAdvertisement().observe(this, advertisement -> {
            if (advertisement != null) {
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(viewModel.getAdLocation(), 14f));
                mMap.addMarker(
                        new MarkerOptions()
                                .position(viewModel.getAdLocation())
                                .title(getString(R.string.marker_on_worklocation))
                );
            }
        });
    }

    @Override
    public void openProfile() {
        NavController navController = NavHostFragment.findNavController(AdvertisementDetailsFragment.this);

        Bundle bundle = new Bundle();
        bundle.putString(MainRepository.UID_BUNDLE, viewModel.getUser().getValue().getId());

        navController.navigate(R.id.action_advertisementDetailsFragment_to_navigation_profile, bundle);
    }

    @Override
    public void react() {
        viewModel.reactTo(bundle -> {
            NavController navController = NavHostFragment.findNavController(AdvertisementDetailsFragment.this);
            navController.navigate(R.id.action_advertisementDetailsFragment_to_chatFragment, bundle);
        });
    }

    @Override
    public void goBack() {
        NavHostFragment.findNavController(this).popBackStack();
    }
}
