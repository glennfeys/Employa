package com.employa.employa.ui.discovery.filter;


import android.app.AlertDialog;
import android.content.Context;
import android.view.View;

import androidx.lifecycle.LiveData;

import com.employa.employa.R;
import com.employa.employa.models.Category;
import com.employa.employa.utility.Consumer;

import java.util.ArrayList;
import java.util.List;

public class CategoryFilterDialogListener implements View.OnClickListener {
    private Consumer<List<String>> callback;
    private LiveData<List<String>> chosenCategories;

    private static final String[] CATEGORY_NAMES = getCategoryNameList();

    public CategoryFilterDialogListener(Consumer<List<String>> callback, LiveData<List<String>> chosenCategories) {
        this.callback = callback;
        this.chosenCategories = chosenCategories;
    }

    @Override
    public void onClick(View v) {
        // setup the alert builder
        Context context = v.getContext();
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setTitle(context.getString(R.string.pick_category));

        // add a checkbox list
        boolean[] checkedItems = new boolean[Category.values().length];

        // Fill checkmarks based on viewModel
        List<String> chosen = chosenCategories.getValue();
        if(chosen != null) {
            for(int i = 0; i < CATEGORY_NAMES.length; i++){
                if(chosen.contains(CATEGORY_NAMES[i]))
                    checkedItems[i] = true;
            }
        }

        builder.setMultiChoiceItems(CATEGORY_NAMES, checkedItems, (dialog, which, isChecked) -> {});

        // add OK and Cancel buttons
        builder.setPositiveButton(context.getText(R.string.filter), (dialog, which) ->
            callback.accept(getCategoryFilterList(checkedItems)));

        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * Convert boolean array to list of filters
     * @param checkedItems Checked array
     * @return Filter list
     */
    private List<String> getCategoryFilterList(boolean[] checkedItems) {
        List<String> filters = new ArrayList<>();
        for(int i = 0; i < CATEGORY_NAMES.length; i++){
            if(checkedItems[i])
                filters.add(CATEGORY_NAMES[i]);
        }
        return filters;
    }

    /**
     * Get a list of all category names in the Category Enum
     * @return List of Category names
     */
    private static String[] getCategoryNameList() {
        Category[] categories = Category.values();
        String[] strs = new String[Category.values().length];

        for(int i = 0; i < categories.length; ++i)
            strs[i] = categories[i].getName();

        return strs;
    }
}
