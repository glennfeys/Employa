package com.employa.employa.ui.discovery.filter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Location;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.lifecycle.LiveData;

import com.employa.employa.R;
import com.employa.employa.models.Radius;
import com.employa.employa.utility.Consumer;
import com.employa.employa.utility.LocationPermissionHelper;

public class DistanceFilterDialogListener implements View.OnClickListener {
    private LocationPermissionHelper helper;
    private LiveData<Radius> radius;
    private Consumer<Radius> callback;

    private Location location;

    /**
     * Listener to handle location filtering,
     * requests location & creates dialog to
     * get a distance for the filter
     *
     * @param helper   Helper class for handling location requests
     * @param callback Callback to handle Radius result
     * @param radius   LiveData from ViewModel about currently set Radius
     */
    public DistanceFilterDialogListener(LocationPermissionHelper helper, Consumer<Radius> callback, LiveData<Radius> radius) {
        this.helper = helper;
        this.radius = radius;
        this.callback = callback;
    }

    @Override
    public void onClick(View v) {
        helper.getLocation(position -> {
            if (position != null) {
                location = position;
                showDialog(v);
            }
        });
    }

    private void showDialog(View v) {
        Context context = v.getContext();
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        // Creating & setting dialog content
        View dialogLayout = LayoutInflater.from(context).inflate(R.layout.seekbar_dialog, null);

        SeekBar seekBar = dialogLayout.findViewById(R.id.seekBar);
        TextView textView = dialogLayout.findViewById(R.id.distance);
        textView.setText(R.string.pick_distance);

        builder.setView(dialogLayout);
        builder.setTitle(R.string.pick_distance);

        builder.setPositiveButton(context.getString(R.string.dialog_ok),
                (dialog, which) -> handleDialogDismiss(seekBar, dialog));

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                textView.setText((progress != 0) ? context.getString(R.string.distance_label, progress * 10) : context.getString(R.string.pick_distance));
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        if (radius.getValue() != null)
            seekBar.setProgress(radius.getValue().getSize() / 10);

        builder.show();
    }

    /**
     * Handle the dismiss event of our Dialog
     *
     * @param seekBar Seekbar to get current progress for callback
     * @param dialog  Dismissed dialog
     */
    private void handleDialogDismiss(SeekBar seekBar, DialogInterface dialog) {
        dialog.dismiss();
        callback.accept(seekBar.getProgress() > 0 ?new Radius(location,seekBar.getProgress() * 10):null);
    }

}
