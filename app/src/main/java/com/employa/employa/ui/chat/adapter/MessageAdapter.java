package com.employa.employa.ui.chat.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.employa.employa.databinding.ItemMessageBinding;
import com.employa.employa.models.ChatMessage;
import com.employa.employa.repository.MainRepository;
import com.employa.employa.ui.components.IdentifyableDiffCallback;
import com.employa.employa.ui.components.ViewHolder;

import java.text.SimpleDateFormat;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ListAdapter;

public class MessageAdapter extends ListAdapter<ChatMessage, MessageAdapter.MessageViewHolder> {
    private SimpleDateFormat dateFormat;

    public MessageAdapter() {
        super(new IdentifyableDiffCallback<>());
        dateFormat = new SimpleDateFormat("dd/MM/yy HH:mm", Locale.getDefault());
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MessageViewHolder(
                ItemMessageBinding.inflate(
                        LayoutInflater.from(parent.getContext()),
                        parent,
                        false
                )
        );
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    class MessageViewHolder extends ViewHolder<ChatMessage> {
        private ItemMessageBinding binding;

        MessageViewHolder(@NonNull ItemMessageBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        @Override
        public void bind(ChatMessage message) {
            boolean isPhoto = message.getPhotoUrl() != null;
            boolean isMe = !MainRepository.getInstance().isMe(message.getRecipient());

            if (isPhoto) {
                Glide.with(binding.photoImageView.getContext())
                        .load(message.getPhotoUrl())
                        .into(binding.photoImageView);
            }

            /*
             * Opmerking: https://issuetracker.google.com/issues/37054474
             *             Quote: We explicitly did not support data binding for layout properties, though you could technically add them yourself. The problem is that these can be easily abused with people trying to animate them.
             *             Dus we moeten hier omheen werken in de adapter...
             */
            if (isMe) {
                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT
                );
                lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                binding.chatMessageContainer.setLayoutParams(lp);
            }
            binding.setDateFormat(dateFormat);
            binding.setIsPhoto(isPhoto);
            binding.setIsMe(isMe);
            binding.setMessage(message);
        }
    }
}
