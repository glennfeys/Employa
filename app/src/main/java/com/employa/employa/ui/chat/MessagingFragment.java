package com.employa.employa.ui.chat;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.employa.employa.R;
import com.employa.employa.databinding.FragmentMessagingBinding;
import com.employa.employa.models.Chat;
import com.employa.employa.repository.MessagingRepository;
import com.employa.employa.repository.RegisteredListener;
import com.employa.employa.ui.chat.adapter.ChatAdapter;
import com.employa.employa.viewmodel.MessagingViewModel;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.fragment.NavHostFragment;

public class MessagingFragment extends Fragment implements ChatOverviewListener {
    private RegisteredListener chatListener;
    private MessagingViewModel viewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewModel = ViewModelProviders.of(this).get(MessagingViewModel.class);
        chatListener = viewModel.fillChats();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        FragmentMessagingBinding binding = FragmentMessagingBinding.inflate(inflater, container, false);
        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(this);

        ChatAdapter adapter = new ChatAdapter(this);
        binding.chatsRecyclerView.setAdapter(adapter);
        viewModel.getChats().observe(this, adapter::submitList);

        return binding.getRoot();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        chatListener.remove();
    }

    @Override
    public void openChatConversation(Chat chat) {
        NavHostFragment.findNavController(this).navigate(
            R.id.action_navigation_messages_to_chatFragment,
            MessagingRepository.getInstance().createMessagingBundle(chat.getId(), chat.getOtherUser())
        );
    }
}
