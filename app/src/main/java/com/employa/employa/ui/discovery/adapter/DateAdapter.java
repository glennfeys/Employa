package com.employa.employa.ui.discovery.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;

import com.employa.employa.databinding.ItemDateDetailsBinding;
import com.employa.employa.formatters.AdvertisementDateFormat;
import com.employa.employa.ui.components.ViewHolder;
import com.google.firebase.Timestamp;

public class DateAdapter extends ListAdapter<Timestamp, DateAdapter.DateViewHolder> {

    private final AdvertisementDateFormat dateFormatter;

    public DateAdapter(Context context) {
        super(DIFF_CALLBACK);
        this.dateFormatter = new AdvertisementDateFormat(context);
    }

    private static final DiffUtil.ItemCallback<Timestamp> DIFF_CALLBACK = new DiffUtil.ItemCallback<Timestamp>() {
        @Override
        public boolean areItemsTheSame(@NonNull Timestamp oldItem, @NonNull Timestamp newItem) {
            return oldItem.equals(newItem);
        }

        @Override
        public boolean areContentsTheSame(@NonNull Timestamp oldItem, @NonNull Timestamp newItem) {
            return oldItem.equals(newItem);
        }
    };

    @NonNull
    @Override
    public DateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new DateViewHolder(
            ItemDateDetailsBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
            )
        );
    }

    @Override
    public void onBindViewHolder(@NonNull DateViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    class DateViewHolder extends ViewHolder<Timestamp> {
        private ItemDateDetailsBinding binding;

        DateViewHolder(@NonNull ItemDateDetailsBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            this.binding.setDateFormatter(dateFormatter);
        }

        @Override
        public void bind(Timestamp timestamp) {
            binding.setItem(timestamp);
        }
    }
}
