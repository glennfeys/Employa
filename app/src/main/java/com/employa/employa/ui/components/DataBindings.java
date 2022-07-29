package com.employa.employa.ui.components;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.core.content.ContextCompat;
import androidx.databinding.BindingAdapter;
import androidx.databinding.InverseBindingAdapter;
import androidx.databinding.InverseBindingListener;
import androidx.lifecycle.MutableLiveData;

import com.employa.employa.R;
import com.mancj.materialsearchbar.MaterialSearchBar;

public class DataBindings {
    @BindingAdapter({"errorMessage"})
    public static void errorMessage(EditText textView, Integer error) {
        if (error != null) {
            textView.setError(textView.getContext().getResources().getString(error));
        } else {
            textView.setError(null);
        }
    }

    /**
     * Databinding for setting search queries
     *
     * @param view View
     * @param query Query
     */
    @BindingAdapter("query")
    public static void setQuery(MaterialSearchBar view, String query) {
        if (!view.getText().equals(query)) {
            view.setText(query);
        }
    }

    /**
     * Inverse Databinding for search queries
     *
     * @param view View
     */
    @InverseBindingAdapter(attribute = "query")
    public static String getQuery(MaterialSearchBar view) {
        return view.getText();
    }

    @BindingAdapter("queryAttrChanged")
    public static void setListeners(MaterialSearchBar view, final InverseBindingListener attrChange) {
        // Set a listener for click, focus, touch, etc.
        view.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                attrChange.onChange();
            }
        });
    }

    /**
     * Databinding for setting button color based on filter
     *
     * @param view    View
     * @param enabled If button is enabled
     */
    @BindingAdapter("enabledButton")
    public static void setEnabled(Button view, MutableLiveData<?> enabled) {
        Context context = view.getContext();
        if (enabled.getValue() != null) {
            view.setBackground(context.getDrawable(R.drawable.button));
            view.setTextColor(ContextCompat.getColor(context, R.color.white));
        } else {
            view.setBackground(context.getDrawable(R.drawable.button_bordered));
            view.setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryDark));
        }
    }

    /**
     * Databinding for toggling visibility based on Boolean MutableData
     *
     * @param view    View
     * @param enabled If enabled
     */
    @BindingAdapter("visibility")
    public static void setVisibility(View view, MutableLiveData<Boolean> enabled) {
        setVisibility(view, enabled.getValue() != null && enabled.getValue());
    }

    /**
     * Databinding for toggling visibility based on boolean
     *
     * @param view    View
     * @param enabled If enabled
     */
    @BindingAdapter("visibility")
    public static void setVisibility(View view, boolean enabled) {
        if (enabled) {
            view.setVisibility(View.VISIBLE);
        } else {
            view.setVisibility(View.GONE);
        }
    }
}
