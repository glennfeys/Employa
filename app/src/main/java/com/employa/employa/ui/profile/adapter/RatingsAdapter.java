package com.employa.employa.ui.profile.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ListAdapter;

import com.bumptech.glide.Glide;
import com.employa.employa.R;
import com.employa.employa.databinding.ItemRatingBinding;
import com.employa.employa.formatters.AdvertisementDateFormat;
import com.employa.employa.models.Rating;
import com.employa.employa.repository.MainRepository;
import com.employa.employa.ui.components.IdentifyableDiffCallback;
import com.employa.employa.ui.components.ViewHolder;
import com.employa.employa.ui.profile.RatingsOverviewListener;

public class RatingsAdapter extends ListAdapter<Rating, RatingsAdapter.RatingViewHolder> {
    private Context context;

    private final AdvertisementDateFormat dateFormatter;
    private RatingsOverviewListener ratingsOverviewListener;

    public RatingsAdapter(Context context, RatingsOverviewListener ratingsOverviewListener) {
        super(new IdentifyableDiffCallback<>());
        this.ratingsOverviewListener = ratingsOverviewListener;
        this.context = context;
        this.dateFormatter = new AdvertisementDateFormat(context);
    }

    @NonNull
    @Override
    public RatingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RatingViewHolder(
                ItemRatingBinding.inflate(
                        LayoutInflater.from(parent.getContext()),
                        parent,
                        false
                )
        );
    }

    @Override
    public void onBindViewHolder(@NonNull RatingViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    class RatingViewHolder extends ViewHolder<Rating> {
        private ItemRatingBinding binding;

        RatingViewHolder(@NonNull ItemRatingBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            this.binding.setActionHandler(RatingsAdapter.this.ratingsOverviewListener);
            this.binding.setDateFormatter(dateFormatter);
        }

        @Override
        public void bind(Rating rating) {
            binding.setRating(rating);

            binding.score.setText(context.getString(R.string.score, rating.getScore()));

            if (!MainRepository.getInstance().isMe(rating.getUser().getId())){
                binding.editButton.setVisibility(View.GONE);
            }

            Glide.with(binding.picture.getContext())
                .load(rating.getUser().getProfilePicDownload())
                .into(binding.picture);
        }
    }
}
