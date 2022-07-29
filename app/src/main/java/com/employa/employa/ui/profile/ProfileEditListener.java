package com.employa.employa.ui.profile;

public interface ProfileEditListener {
    /**
     * Event when "submit" button is clicked on profile edit form.
     * @param name The name
     * @param description The description
     */
    void onSubmit(String name, String description);
}
