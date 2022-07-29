package com.employa.employa.ui.advertisement;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.DatePicker;
import com.applandeo.materialcalendarview.builders.DatePickerBuilder;
import com.applandeo.materialcalendarview.listeners.OnSelectDateListener;
import com.employa.employa.R;
import com.employa.employa.databinding.FragmentEditAdvertisementBinding;
import com.employa.employa.models.Category;
import com.employa.employa.models.MyLocation;
import com.employa.employa.repository.Callback;
import com.employa.employa.repository.MainRepository;
import com.employa.employa.ui.advertisement.adapter.DateListAdapter;
import com.employa.employa.ui.profile.AdvertisementListener;
import com.employa.employa.viewmodel.AddAdvertisementViewModel;
import com.google.android.gms.tasks.Task;

import java.util.Calendar;
import java.util.List;
import java.util.Objects;

public class EditAdvertisementFragment extends Fragment implements PlanningListener, OnSelectDateListener, DateItemListener, AdvertisementListener, Callback<Task> {
    private static final int REQUEST_CODE_GET_LOCATION = 546;
    private static final String LOCATION = "location";

    private AddAdvertisementViewModel viewModel;

    public EditAdvertisementFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(this).get(AddAdvertisementViewModel.class);
        String id = requireArguments().getString(MainRepository.BUNDLE_ADVERTISEMENT_ID);

        if (viewModel.getAdvertisementId().getValue() == null)
            viewModel.fillAdvertisement(id, getContext());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FragmentEditAdvertisementBinding binding = FragmentEditAdvertisementBinding.inflate(inflater, container, false);
        binding.setViewModel(viewModel);
        ArrayAdapter<Category> categoryArrayAdapter = new ArrayAdapter<>(requireContext(), R.layout.item_spinner, Category.values());
        binding.spinner.setAdapter(categoryArrayAdapter);
        viewModel.getSelectedCategory().observe(this, category -> {
            if (category != null) {
                binding.spinner.setSelection(Objects.requireNonNull(viewModel.getSelectedCategory().getValue()).ordinal());
            }
        });
        binding.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                viewModel.setSelectedCategory(Category.values()[position]);
            }
            public void onNothingSelected(AdapterView<?> parent) {
                //
            }
        });

        binding.openMapsButton.setOnClickListener(v -> {
            assert container != null;
            Intent intent = new Intent(container.getContext(), MapActivity.class);
            startActivityForResult(intent, REQUEST_CODE_GET_LOCATION);
        });

        DateListAdapter dateListAdapter = new DateListAdapter(this);
        binding.preferredDates.setAdapter(dateListAdapter);
        binding.setPlanningListener(this);
        binding.setLifecycleOwner(this);
        binding.setActionHandler(this);

        viewModel.getDates().observe(this, dateListAdapter::submitList);
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

    @Override
    public void selectDates() {
        Calendar now = Calendar.getInstance();
        now.add(Calendar.DATE, -1);

        // Opm: deze component vertaalt reeds de UI elementen naar de ingestelde taal, geen override nodig.
        DatePickerBuilder builder = new DatePickerBuilder(getContext(), this)
                .pickerType(CalendarView.MANY_DAYS_PICKER)
                .minimumDate(now)
                .selectedDays(viewModel.getDates().getValue());

        builder.build().show();
    }

    @Override
    public void onSelect(List<Calendar> calendar) {
        viewModel.setDates(calendar);
    }

    @Override
    public void delete(Calendar d) {
        viewModel.removeDate(d);
    }

    @Override
    public void edit(String id) {
        MainRepository.getInstance().editAdvertisement(viewModel.getAdvertisementId().getValue(), viewModel.serialize(), this);
    }

    @Override
    public void onSuccess(Task task) {
        Toast.makeText(getContext(), getString(R.string.advertisement_edited) , Toast.LENGTH_SHORT).show();
        requireActivity().getSupportFragmentManager().popBackStack();
        viewModel.reset();
    }

    @Override
    public void onFail() {
        Toast.makeText(getContext(), getString(R.string.something_went_wrong), Toast.LENGTH_LONG).show();
    }
}
