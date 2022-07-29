package com.employa.employa.ui.components;

import com.employa.employa.models.IdentifyableModel;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

public class IdentifyableDiffCallback<T extends IdentifyableModel> extends DiffUtil.ItemCallback<T> {
    @Override
    public boolean areItemsTheSame(@NonNull T oldItem, @NonNull T newItem) {
        return oldItem.getId().equals(newItem.getId());
    }

    @Override
    public boolean areContentsTheSame(@NonNull T oldItem, @NonNull T newItem) {
        return oldItem.equals(newItem);
    }
}
