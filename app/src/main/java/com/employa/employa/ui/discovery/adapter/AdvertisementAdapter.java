package com.employa.employa.ui.discovery.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.employa.employa.databinding.ItemAdvertisementBinding;
import com.employa.employa.databinding.ItemAdvertisementSmallBinding;
import com.employa.employa.formatters.AdvertisementDateFormat;
import com.employa.employa.models.Advertisement;
import com.employa.employa.repository.MainRepository;
import com.employa.employa.ui.components.ViewType;
import com.employa.employa.ui.components.IdentifyableDiffCallback;
import com.employa.employa.ui.components.ViewHolder;
import com.employa.employa.ui.discovery.ButtonClickListener;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ListAdapter;

public class AdvertisementAdapter extends ListAdapter<Advertisement, ViewHolder<Advertisement>> {
    private final ViewType viewType;

    private final AdvertisementDateFormat dateFormatter;
    private final ButtonClickListener listener;

    public AdvertisementAdapter(Context context, ViewType viewType, ButtonClickListener listener) {
        super(new IdentifyableDiffCallback<>());
        this.viewType = viewType;
        this.dateFormatter = new AdvertisementDateFormat(context);
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder<Advertisement> onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(this.viewType == ViewType.ADVERTISEMENT_NORMAL)
            return new ViewHolderAdvertisement(
                ItemAdvertisementBinding.inflate(
                    LayoutInflater.from(parent.getContext()),
                    parent,
                    false
                )
            );

        return new ViewHolderAdvertisementSmall(
            ItemAdvertisementSmallBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
            )
        );
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder<Advertisement> holder, int position) {
        holder.bind(getItem(position));
    }

    class ViewHolderAdvertisement extends ViewHolder<Advertisement> {
        private ItemAdvertisementBinding binding;

        ViewHolderAdvertisement(@NonNull ItemAdvertisementBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            this.binding.setButtonHandler(AdvertisementAdapter.this.listener);
            this.binding.setDateFormatter(AdvertisementAdapter.this.dateFormatter);
        }

        @Override
        public void bind(Advertisement advertisement) {
            binding.setItem(advertisement);

            // Hide react button if it's your own advertisement
            binding.reactButton.setVisibility(MainRepository.getInstance().isMe(advertisement.getOwner().getId()) ? View.GONE : View.VISIBLE);

            ImageView imageView = binding.picture;
            Glide.with(imageView.getContext())
                .load(advertisement.getOwner().getProfilePicDownload())
                .into(imageView);
        }
    }

    class ViewHolderAdvertisementSmall extends ViewHolder<Advertisement> {
        private ItemAdvertisementSmallBinding binding;

        ViewHolderAdvertisementSmall(@NonNull ItemAdvertisementSmallBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            this.binding.setButtonHandler(AdvertisementAdapter.this.listener);
            this.binding.setDateFormatter(AdvertisementAdapter.this.dateFormatter);
        }

        @Override
        public void bind(Advertisement advertisement) {
            binding.setItem(advertisement);

            // We can do this because it won't be reused for multiple different people
            boolean isMe = MainRepository.getInstance().isMe(advertisement.getOwnerRef().getId());
            if(isMe) {
                binding.buttonDelete.setVisibility(View.VISIBLE);
                binding.buttonEdit.setVisibility(View.VISIBLE);
            }
        }
    }
}
