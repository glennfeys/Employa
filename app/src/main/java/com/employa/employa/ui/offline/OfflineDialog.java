package com.employa.employa.ui.offline;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.fragment.app.DialogFragment;

import com.employa.employa.R;
import com.employa.employa.databinding.FragmentOfflineBinding;
import com.employa.employa.utility.Helpers;

import java.util.Objects;

public class OfflineDialog extends AppCompatDialogFragment implements View.OnClickListener {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCancelable(false);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.AppThemeFull);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FragmentOfflineBinding binding = FragmentOfflineBinding.inflate(inflater, container, false);
        binding.buttonOffline.setOnClickListener(this);
        return binding.getRoot();
    }

    @Override
    public void onClick(View v) {
        String msg;
        if (Helpers.isNetworkAvailable(Objects.requireNonNull(getActivity()))) {
            dismiss();
            msg = getResources().getString(R.string.connected);
        } else {
            msg = getResources().getString(R.string.not_connected);
        }

        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
    }
}
