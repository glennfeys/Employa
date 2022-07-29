package com.employa.employa.ui.advertisement;

import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.employa.employa.R;
import com.employa.employa.databinding.FragmentAddAdvertisementOverviewBinding;
import com.employa.employa.repository.Callback;
import com.employa.employa.repository.MainRepository;

import java.util.Objects;
import com.google.android.gms.tasks.Task;

public class AdvertisementOverviewFragment extends AddPageFragment implements OverviewListener, Callback<Task> {

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment AdvertisementOverviewFragment.
     */
    public static AdvertisementOverviewFragment newInstance() {
        return new AdvertisementOverviewFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FragmentAddAdvertisementOverviewBinding binding = FragmentAddAdvertisementOverviewBinding.inflate(inflater, container, false);
        binding.setViewModel(viewModel);
        binding.setOverviewListener(this);
        binding.setLifecycleOwner(this);
        return binding.getRoot();
    }

    @Override
    public void save() {
        MainRepository.getInstance().addAdvertisement(viewModel.serialize(), this);
    }

    @Override
    public void onSuccess(Task task) {
        Toast.makeText(getContext(), getString(R.string.advertisement_placed), Toast.LENGTH_SHORT).show();
        Objects.requireNonNull(getActivity()).getSupportFragmentManager().popBackStack();
        viewModel.reset();
    }

    @Override
    public void onFail() {
        Toast.makeText(getContext(), getString(R.string.something_went_wrong), Toast.LENGTH_LONG).show();
    }
}
