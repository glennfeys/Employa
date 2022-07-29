package com.employa.employa.ui.components;

import android.app.Activity;
import android.content.Context;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.employa.employa.utility.Procedure;

public class EditorConfirmHandler implements TextView.OnEditorActionListener {
    private Procedure fun;
    private Activity activity;
    private View view;

    public EditorConfirmHandler(Procedure fun, Activity activity, View view) {
        this.fun = fun;
        this.activity = activity;
        this.view = view;
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_SEARCH) {
            fun.execute();
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            return true;
        }
        return false;
    }
}
