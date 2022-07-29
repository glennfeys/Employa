package com.employa.employa.ui.advertisement;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.employa.employa.viewmodel.AddAdvertisementViewModel;

abstract class AddPageFragment extends Fragment {

    protected AddAdvertisementViewModel viewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(requireActivity()).get(AddAdvertisementViewModel.class);
    }
}
