package com.employa.employa.ui.chat;

import android.text.Editable;

import com.employa.employa.ui.components.BackButtonListener;

public interface ChatConversationListener extends BackButtonListener {
    /**
     * Show image picker so user can upload an image
     */
    void showImagePicker();

    /**
     * Go to profile
     */
    void goProfile();

    /**
     * Send message
     * @param msg The message
     */
    void sendMessage(Editable msg);
}
