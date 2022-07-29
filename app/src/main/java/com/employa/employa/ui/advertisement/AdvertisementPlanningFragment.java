package com.employa.employa.ui.advertisement;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.builders.DatePickerBuilder;
import com.applandeo.materialcalendarview.listeners.OnSelectDateListener;
import com.employa.employa.databinding.FragmentAddAdvertisementPlanningBinding;
import com.employa.employa.ui.advertisement.adapter.DateListAdapter;

import java.util.Calendar;
import java.util.List;

public class AdvertisementPlanningFragment extends AddPageFragment implements PlanningListener, DateItemListener, OnSelectDateListener {
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment AdvertisementPlanningFragment.
     */
    public static AdvertisementPlanningFragment newInstance() {
        return new AdvertisementPlanningFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FragmentAddAdvertisementPlanningBinding binding = FragmentAddAdvertisementPlanningBinding.inflate(inflater, container, false);

        DateListAdapter dateListAdapter = new DateListAdapter(this);
        binding.preferredDates.setAdapter(dateListAdapter);
        binding.setViewModel(viewModel);
        binding.setPlanningListener(this);
        binding.setLifecycleOwner(this);

        viewModel.getDates().observe(this, dateListAdapter::submitList);

        return binding.getRoot();
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
    public void delete(Calendar d) {
        viewModel.removeDate(d);
    }

    @Override
    public void onSelect(List<Calendar> calendar) {
        viewModel.setDates(calendar);
    }
}
