package com.employa.employa.viewmodel;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

import com.employa.employa.models.Advertisement;
import com.employa.employa.models.Category;
import com.employa.employa.models.MyLocation;
import com.employa.employa.repository.Callback;
import com.employa.employa.repository.MainRepository;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.GeoPoint;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class AddAdvertisementViewModel extends ViewModel {
    private MutableLiveData<Category> selectedCategory = new MutableLiveData<>();
    private MutableLiveData<String> title = new MutableLiveData<>();
    private MutableLiveData<String> description = new MutableLiveData<>();
    private MutableLiveData<String> payMutableData = new MutableLiveData<>();
    private MutableLiveData<MyLocation> location = new MutableLiveData<>();
    private MutableLiveData<DatePreference> datePreference = new MutableLiveData<>();
    private MutableLiveData<List<Calendar>> dates = new MutableLiveData<>();
    private MutableLiveData<String> whenOverview = new MutableLiveData<>();
    private MutableLiveData<String> advertisementId = new MutableLiveData<>();
    private int currentState = 0;

    public AddAdvertisementViewModel() {
        reset();
    }

    public int getCurrentState() {
        return currentState;
    }

    public void setCurrentState(int state) {
        currentState = state;
    }

    public LiveData<Category> getSelectedCategory() {
        return selectedCategory;
    }

    public void setSelectedCategory(Category category) {
        selectedCategory.setValue(category);
    }

    public MutableLiveData<String> getTitleMutableData() {
        return title;
    }

    public MutableLiveData<String> getDescriptionMutableData() {
        return description;
    }

    public void setLocation(MyLocation newLocation) {
        location.setValue(newLocation);
    }

    public MutableLiveData<MyLocation> getLocation() {
        return location;
    }

    public MutableLiveData<String> getPayMutableData() {
        return payMutableData;
    }

    public LiveData<DatePreference> getDatePreference() {
        return datePreference;
    }

    public void setDatePreference(DatePreference preference) {
        datePreference.setValue(preference);
        setWhenOverview();
    }

    public void setDates(List<Calendar> list) {
        dates.setValue(list);
        setWhenOverview();
    }

    public void removeDate(Calendar x) {
        // Vermijd problemen met diff nemen in live data
        assert dates.getValue() != null;
        List<Calendar> list = new LinkedList<>(dates.getValue());
        list.remove(x);
        setDates(list);
    }

    public LiveData<List<Calendar>> getDates() {
        return dates;
    }

    public Map<String, Object> serialize() {
        Map<String, Object> x = new HashMap<>();

        LatLng ll = Objects.requireNonNull(location.getValue()).getLocation();

        // Stream requires API level >= 24, we want to target more devices, so sadly we can't use stream here
        List<Calendar> list = dates.getValue();
        assert list != null;
        List<Timestamp> timestamps = new ArrayList<>(list.size());
        if (datePreference.getValue() == DatePreference.HAVE_PREFERENCE) {
            for(Calendar d : list)
                timestamps.add(new Timestamp(d.getTime()));
        }

        x.put("category", Objects.requireNonNull(selectedCategory.getValue()).getName());
        x.put("title", Objects.requireNonNull(title.getValue()));
        x.put("description", Objects.requireNonNull(description.getValue()));
        x.put("payment", Objects.requireNonNull(payMutableData.getValue()));
        x.put("timestamps", timestamps);
        x.put("location", new GeoPoint(ll.latitude, ll.longitude));
        x.put("created", Timestamp.now());

        return x;
    }

    public boolean isDatesOK() {
        if (datePreference.getValue() == DatePreference.NO_PREFERENCE) {
            return true;
        }
        return dates.getValue() != null && dates.getValue().size() > 0;
    }

    private void setWhenOverview() {
        List<Calendar> dates = getDates().getValue();
        if (getDatePreference().getValue() != DatePreference.NO_PREFERENCE) {
            if (dates != null && dates.size() > 0) {
                StringBuilder datums = new StringBuilder();

                DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                for (Calendar date : dates) {
                    datums.append("-  ").append(formatter.format(date.getTime())).append("\n");
                }
                whenOverview.setValue(datums.toString());
            }
        }
    }

    public void reset() {
        selectedCategory.setValue(null);
        title.setValue(null);
        description.setValue(null);
        payMutableData.setValue(null);
        location.setValue(null);
        datePreference.setValue(DatePreference.NO_PREFERENCE);
        dates.setValue(new LinkedList<>());
        currentState = 0;
    }

    public LiveData<String> getWhenOverview() {
        return whenOverview;
    }

    public void fillAdvertisement(String id, Context context) {
        MainRepository repo = MainRepository.getInstance();

        repo.getAdvertisement(id, new Callback<Advertisement>() {
            @Override
            public void onSuccess(Advertisement advertisement) {
                advertisementId.setValue(advertisement.getId());
                selectedCategory.setValue(Category.valueOf(advertisement.getCategory().toUpperCase()));
                title.setValue(advertisement.getTitle());
                description.setValue(advertisement.getDescription());
                payMutableData.setValue(advertisement.getPayment());

                MyLocation mlocation = new MyLocation(advertisement.getLocation());

                Geocoder geocoder = new Geocoder(context);
                List<Address> addressList = new ArrayList<>();
                try {
                    addressList = geocoder.getFromLocation(mlocation.getLatitude(), mlocation.getLongitude(), 1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (addressList.size() > 0) {
                    mlocation.setAddress(addressList.get(0).getAddressLine(0));
                }

                location.setValue(mlocation);
                ArrayList<Calendar> datelist = new ArrayList<>(advertisement.getTimestamps().size());
                for(Timestamp t : advertisement.getTimestamps()) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(t.toDate());
                    datelist.add(calendar);
                }
                if (datelist.size() > 0) {
                    datePreference.setValue(DatePreference.HAVE_PREFERENCE);
                } else {
                    datePreference.setValue(DatePreference.NO_PREFERENCE);
                }
                setDates(datelist);
            }

            @Override
            public void onFail() {
                // redundant
            }
        });
    }

    public MutableLiveData<String> getAdvertisementId() {
        return advertisementId;
    }
}
