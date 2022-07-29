package com.employa.employa.formatters;

import android.content.Context;

import com.google.firebase.Timestamp;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.employa.employa.R;

public class AdvertisementDateFormat extends SimpleDateFormat {
    private Context context;

    public AdvertisementDateFormat(Context context) {
        super("d MMMM yyyy", Locale.getDefault());
        this.context = context;
    }

    public List<String> getDateStrings(List<Timestamp> timestamps) {
        List<String> dateStrings = new ArrayList<>(timestamps.size());
        for(Timestamp timestamp : timestamps) {
            dateStrings.add(super.format(timestamp.toDate()));
        }
        return dateStrings;
    }

    public String formatArray(List<Timestamp> timestamps) {
        // https://stackoverflow.com/questions/17261290/plural-definition-is-ignored-for-zero-quantity/17261327
        if(timestamps.size() == 0)
            return context.getString(R.string.timestamps_zero);
        return context.getResources().getQuantityString(R.plurals.timestamps, timestamps.size(), getDateStrings(timestamps).toArray());
    }

    public String timeSince(Timestamp created) {
        long delta = Timestamp.now().getSeconds() - created.getSeconds();

        if(delta < 60)         return context.getString(R.string.a_minute_ago);
        if((delta /= 60) < 60) return context.getResources().getQuantityString(R.plurals.minutes_ago, (int) delta, (int) delta);
        if((delta /= 60) < 24) return context.getResources().getQuantityString(R.plurals.hours_ago, (int) delta, (int) delta);
        if((delta /= 24) < 3)  return context.getResources().getQuantityString(R.plurals.days_ago, (int) delta, (int) delta);
        return context.getString(R.string.default_ago);
    }
}
