package com.employa.employa.ui.discovery;

import com.employa.employa.ui.components.BackButtonListener;

public interface AdvertisementDetailsListener extends BackButtonListener {
    /**
     * When the user wants to open the profile of this ad owner.
     */
    void openProfile();

    /**
     * When the user wants to react to this ad.
     */
    void react();
}
