package com.employa.employa.ui.components;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;

import com.google.android.gms.maps.SupportMapFragment;

public class ScrollCompatibleSupportMapFragment extends SupportMapFragment {
    private View view;

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        view = super.onCreateView(layoutInflater, viewGroup, bundle);
        TouchableWrapper tw = new TouchableWrapper(getContext());
        tw.addView(view);
        return tw;
    }

    @Nullable
    @Override
    public View getView() {
        return view;
    }

    class TouchableWrapper extends FrameLayout {
        public TouchableWrapper(Context ctx) {
            super(ctx);
        }

        @Override
        public boolean dispatchTouchEvent(MotionEvent ev) {
            getParent().requestDisallowInterceptTouchEvent(true);
            return super.dispatchTouchEvent(ev);
        }
    }
}
