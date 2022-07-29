package com.employa.employa.ui.advertisement;

import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.employa.employa.databinding.FragmentAddAdvertisementDescriptionBinding;

public class AdvertisementDescriptionFragment extends AddPageFragment {
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment AdvertisementDescriptionFragment.
     */
    public static AdvertisementDescriptionFragment newInstance() {
        return new AdvertisementDescriptionFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FragmentAddAdvertisementDescriptionBinding binding = FragmentAddAdvertisementDescriptionBinding.inflate(inflater, container, false);
        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(this);
        return binding.getRoot();
    }
}
