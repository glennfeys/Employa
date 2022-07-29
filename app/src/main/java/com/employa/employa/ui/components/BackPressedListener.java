package com.employa.employa.ui.components;

/*
 * Interface om de back button op te vangen. Gebruikt in
 * AddAdvertisementFragment om de backbutton de viewpager terug te laten keren
 */
public interface BackPressedListener {
    /**
     * Goes back
     */
    void goBack();

    /**
     * If we can go back
     * @return True if we can go back, false otherwise
     */
    boolean canGoBack();
}
