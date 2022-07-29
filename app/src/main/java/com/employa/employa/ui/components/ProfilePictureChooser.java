package com.employa.employa.ui.components;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.employa.employa.R;
import com.theartofdev.edmodo.cropper.CropImage;

/**
 * Mixin om profielfoto te kiezen en te behandelen.
 * We kiezen voor een mixin omdat we twee verschillende superklassen hebben die voor de rest ongerelateerd zijn aan elkaar.
 * Dit laat ook toe overal waar we willen "profile picture choosers" in te pluggen.
 */
public interface ProfilePictureChooser {
    /**
     * Kies picture
     */
    default void choosePicture() {
        Intent intent = CropImage.activity()
                .setAspectRatio(1, 1)
                .setRequestedSize(200, 200)
                .getIntent(getContext());
        startActivityForResult(intent, CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE);
    }

    /**
     * Standaard handler voor het resultaat van een picture chooser
     *
     * @param requestCode Request code
     * @param resultCode  Result code
     * @param data        Data
     */
    default void onActivityResultPictureHandler(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == Activity.RESULT_OK) {
                assert result != null;
                setProfilePicture(result.getUri());
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Toast.makeText(getContext(), getResources().getString(R.string.error_picture_chooser), Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     * Stel profile picture in naar gegeven Uri
     *
     * @param uri De uri
     */
    void setProfilePicture(Uri uri);

    /**
     * @see androidx.fragment.app.Fragment
     */
    void startActivityForResult(Intent intent, int requestCode);
    Context getContext();
    @NonNull Resources getResources();
}
