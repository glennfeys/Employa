package com.employa.employa.ui.chat.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.employa.employa.databinding.ItemChatBinding;
import com.employa.employa.models.Chat;
import com.employa.employa.ui.chat.ChatOverviewListener;
import com.employa.employa.ui.components.IdentifyableDiffCallback;
import com.employa.employa.ui.components.ViewHolder;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ListAdapter;

public class ChatAdapter extends ListAdapter<Chat, ChatAdapter.ChatViewHolder> {
    private ChatOverviewListener listener;

    public ChatAdapter(ChatOverviewListener listener) {
        super(new IdentifyableDiffCallback<>());
        this.listener = listener;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ChatViewHolder(
                ItemChatBinding.inflate(
                        LayoutInflater.from(parent.getContext()),
                        parent,
                        false
                )
        );
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    class ChatViewHolder extends ViewHolder<Chat> {
        private ItemChatBinding binding;

        ChatViewHolder(@NonNull ItemChatBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        @Override
        public void bind(Chat chat) {
            Glide.with(binding.photoImageView.getContext())
                    .load(chat.getOtherUser().getProfilePicDownload())
                    .into(binding.photoImageView);

            binding.setChat(chat);
            binding.setListener(ChatAdapter.this.listener);
        }
    }
}
