package com.employa.employa.ui.advertisement.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.employa.employa.databinding.ItemDateBinding;
import com.employa.employa.ui.advertisement.DateItemListener;
import com.employa.employa.ui.components.ViewHolder;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;

public class DateListAdapter extends ListAdapter<Calendar, DateListAdapter.DateListViewHolder> {
    private final SimpleDateFormat formatter;
    private final DateItemListener diListener;

    public DateListAdapter(DateItemListener diListener) {
        super(DIFF_CALLBACK);
        this.formatter = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        this.diListener = diListener;
    }

    // Opm: gebrek aan Id wegens niet uit DB of iets dergelijks
    private static final DiffUtil.ItemCallback<Calendar> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<Calendar>() {
                @Override
                public boolean areItemsTheSame(
                        @NonNull Calendar oldItem, @NonNull Calendar newItem) {
                    return oldItem.equals(newItem);
                }
                @Override
                public boolean areContentsTheSame(
                        @NonNull Calendar oldItem,  @NonNull Calendar newItem) {
                    return oldItem.equals(newItem);
                }
            };

    @NonNull
    @Override
    public DateListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new DateListViewHolder(
                ItemDateBinding.inflate(
                        LayoutInflater.from(parent.getContext()),
                        parent,
                        false
                )
        );
    }

    @Override
    public void onBindViewHolder(@NonNull DateListViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    class DateListViewHolder extends ViewHolder<Calendar> {
        private ItemDateBinding binding;

        DateListViewHolder(@NonNull ItemDateBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            binding.setFormatter(formatter);
            binding.setDateItemListener(diListener);
        }

        @Override
        public void bind(Calendar item) {
            binding.setItem(item);
        }
    }
}