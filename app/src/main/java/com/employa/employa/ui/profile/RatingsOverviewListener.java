package com.employa.employa.ui.profile;

import com.employa.employa.models.Rating;

public interface RatingsOverviewListener {
    /**
     * Event when "edit" button is clicked on ratings overview.
     * @param rating The old rating
     */
    void editRating(Rating rating);

    /**
     * Event when a name or profile picture is clicked on ratings overview.
     * @param uid The user ID
     */
    void goToProfile(String uid);
}
