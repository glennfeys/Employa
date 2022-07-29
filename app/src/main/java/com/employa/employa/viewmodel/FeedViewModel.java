package com.employa.employa.viewmodel;

import android.os.Bundle;

import androidx.databinding.BindingAdapter;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.employa.employa.models.Advertisement;
import com.employa.employa.repository.Callback;
import com.employa.employa.repository.MainRepository;
import com.employa.employa.repository.MessagingRepository;
import com.employa.employa.utility.Consumer;
import com.tylersuehr.esr.EmptyStateRecyclerView;

import java.util.List;

public class FeedViewModel extends SearchViewModel {
    private MutableLiveData<List<Advertisement>> advertisements = new MutableLiveData<>();
    private MutableLiveData<Byte> state = new MutableLiveData<>();

    // Consumer responsible for mutating state after loading advertisements
    private Consumer<Integer> integerConsumer = size ->
        state.setValue(size > 0 ? EmptyStateRecyclerView.STATE_OK : EmptyStateRecyclerView.STATE_EMPTY);

    /**
     * Binding to set state of EmptyStateRecyclerView with ViewModel
     *
     * @param view View
     * @param state State
     */
    @BindingAdapter("state")
    public static void setState(EmptyStateRecyclerView view, MutableLiveData<Byte> state) {
        if (state.getValue() != null) {
            view.invokeState(state.getValue());
        } else {
            view.invokeState(EmptyStateRecyclerView.STATE_LOADING);
        }
    }

    public void clear() {
        state.setValue(EmptyStateRecyclerView.STATE_LOADING);
    }

    public MutableLiveData<Byte> getState() {
        return state;
    }

    public LiveData<List<Advertisement>> getAdvertisements() {
        return advertisements;
    }

    public void reactTo(Advertisement advertisement, Callback<Bundle> callback) {
        MessagingRepository.getInstance().addChatWith(advertisement.getOwner(), callback);
    }

    /**
     * Refresh advertisements with current query
     */
    public void refresh() {
        advertisements.setValue(null);
        state.setValue(EmptyStateRecyclerView.STATE_LOADING);
        MainRepository.getInstance().getAdvertisementsByQuery(this, advertisements, integerConsumer);
    }

    /**
     * Clears all current filters & performs refresh
     */
    @Override
    public void clearFilters() {
        super.clearFilters();
        refresh();
    }
}
