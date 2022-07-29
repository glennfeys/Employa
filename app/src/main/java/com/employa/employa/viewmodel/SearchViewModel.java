package com.employa.employa.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.employa.employa.models.Radius;

import java.util.Calendar;
import java.util.List;

public class SearchViewModel extends ViewModel {
    private MutableLiveData<String> searchQuery = new MutableLiveData<>();
    private MutableLiveData<List<String>> categories = new MutableLiveData<>();
    private MutableLiveData<Radius> radius = new MutableLiveData<>();
    private MutableLiveData<List<Calendar>> period = new MutableLiveData<>();
    private MutableLiveData<Boolean> isFiltered = new MutableLiveData<>();

    public MutableLiveData<Boolean> getIsFiltered() {
        return isFiltered;
    }

    public MutableLiveData<String> getQuery() {
        return searchQuery;
    }

    public MutableLiveData<List<String>> getCategories() {
        return categories;
    }

    public void setCategories(List<String> categories) {
        this.categories.setValue(categories.isEmpty() ? null : categories);
        checkIsFiltered();
    }

    public MutableLiveData<Radius> getRadius() {
        return radius;
    }

    public void setRadius(Radius radius) {
        this.radius.setValue(radius);
        checkIsFiltered();
    }

    public MutableLiveData<List<Calendar>> getPeriod() {
        return period;
    }

    public void setPeriod(List<Calendar> period) {
        this.period.setValue(period);
        checkIsFiltered();
    }

    /**
     * Clear all search filters
     */
    public void clearFilters() {
        categories.setValue(null);
        radius.setValue(null);
        period.setValue(null);

        isFiltered.setValue(false);
    }

    /**
     * Update isFiltered value
     */
    private void checkIsFiltered() {
        isFiltered.setValue(categories.getValue() != null || period.getValue() != null || radius.getValue() != null);
    }
}
