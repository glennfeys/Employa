package com.employa.employa.ui.chat;

import com.employa.employa.models.Chat;

public interface ChatOverviewListener {
    /**
     * Opens a chat conversation
     * @param chat The chat
     */
    void openChatConversation(Chat chat);
}
