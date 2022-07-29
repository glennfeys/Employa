package com.employa.employa.ui.auth;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

public abstract class AuthFragmentBase extends Fragment {
    protected AuthStateListener mListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof AuthStateListener) {
            mListener = (AuthStateListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement AuthStateListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    protected void notifyListener() {
        if (mListener != null)
            mListener.onAuthenticationSuccessful();
    }

}
