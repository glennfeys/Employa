package com.employa.employa.ui.discovery;

import com.employa.employa.models.Advertisement;

public interface ButtonClickListener {
    /**
     * Event when "react" button is clicked on advertisement.
     * @param advertisement The advertisement
     */
    default void onReactClicked(Advertisement advertisement) {}

    /**
     * Event when "more info" button is clicked on advertisement.
     * @param advertisement The advertisement
     */
    void onMoreInfoClicked(Advertisement advertisement);

    /**
     * Event when "edit" button is clicked on advertisement.
     * @param advertisement The advertisement
     */
    default void onEditClicked(Advertisement advertisement) {}

    /**
     * Event when "delete" button is clicked on advertisement.
     * @param advertisement The advertisement
     */
    default void onDeleteClicked(Advertisement advertisement) {}

    /**
     * When the user wants to open the profile of this ad owner.
     * @param uid The user ID
     */
    default void goToProfile(String uid) {}
}
