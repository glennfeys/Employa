package com.employa.employa.ui.profile;

import com.employa.employa.ui.components.BackButtonListener;

public interface ProfileListener extends BackButtonListener {
    /**
     * Event when "edit" button is clicked on profile.
     */
    void editProfile();

    /**
     * Event when "react" button is clicked on profile.
     */
    void react();

    /**
     * Event when "add rating" button is clicked on profile.
     */
    void addRating();

    /**
     * Event when "ratings" button is clicked on profile.
     */
    void onClickRatings();
}
