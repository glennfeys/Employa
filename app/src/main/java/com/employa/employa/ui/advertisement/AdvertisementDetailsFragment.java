package com.employa.employa.ui.advertisement;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.employa.employa.databinding.FragmentAddAdvertisementDetailsBinding;
import com.employa.employa.models.MyLocation;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class AdvertisementDetailsFragment extends AddPageFragment {

    private static final String LOCATION = "location";
    private static final int REQUEST_CODE_GET_LOCATION = 101;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment AdvertisementDetailsFragment.
     */
    public static AdvertisementDetailsFragment newInstance() {
        return new AdvertisementDetailsFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FragmentAddAdvertisementDetailsBinding binding = FragmentAddAdvertisementDetailsBinding.inflate(inflater, container, false);
        binding.setViewModel(viewModel);
        binding.openMapsButton.setOnClickListener(v -> {
            Intent intent = new Intent(container.getContext(), MapActivity.class);
            startActivityForResult(intent, REQUEST_CODE_GET_LOCATION);
        });
        binding.setLifecycleOwner(this);
        return binding.getRoot();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_CODE_GET_LOCATION) {
            if (resultCode == Activity.RESULT_OK) {
                assert data != null;
                MyLocation location = (MyLocation) data.getSerializableExtra(LOCATION);
                viewModel.setLocation(location);
            }
        }
    }
}
