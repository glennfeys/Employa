package com.employa.employa.ui.discovery.filter;

import android.view.View;

import androidx.lifecycle.LiveData;

import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.DatePicker;
import com.applandeo.materialcalendarview.builders.DatePickerBuilder;
import com.applandeo.materialcalendarview.listeners.OnSelectDateListener;
import com.employa.employa.utility.Consumer;

import java.util.Calendar;
import java.util.List;

public class PeriodFilterDialogListener implements View.OnClickListener, OnSelectDateListener {
    private Consumer<List<Calendar>> callback;
    private LiveData<List<Calendar>> chosenDates;

    public PeriodFilterDialogListener(Consumer<List<Calendar>> callback, LiveData<List<Calendar>> chosenDates) {
        this.callback = callback;
        this.chosenDates = chosenDates;
    }

    @Override
    public void onClick(View v) {
        Calendar now = Calendar.getInstance();
        now.add(Calendar.DATE, -1);

        DatePickerBuilder builder = new DatePickerBuilder(v.getContext(), this)
                .pickerType(CalendarView.RANGE_PICKER)
                .minimumDate(now);

        List<Calendar> value = chosenDates.getValue();
        if (value != null)
            builder.selectedDays(value);

        builder.build().show();
    }

    @Override
    public void onSelect(List<Calendar> calendar) {
        callback.accept(calendar);
    }
}
