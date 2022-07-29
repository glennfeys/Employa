package com.employa.employa.ui.advertisement;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.employa.employa.R;
import com.employa.employa.databinding.FragmentAddAdvertisementCategorySelectionBinding;
import com.employa.employa.databinding.ItemAdvertisementCategoryBinding;
import com.employa.employa.models.Category;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class CategorySelectionFragment extends AddPageFragment {
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment CategorySelectionFragment.
     */
    public static CategorySelectionFragment newInstance() {
        return new CategorySelectionFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FragmentAddAdvertisementCategorySelectionBinding binding = FragmentAddAdvertisementCategorySelectionBinding.inflate(inflater, container, false);
        binding.setViewmodel(viewModel);
        binding.categoryGridView.setAdapter(new CategoryAdapter());
        binding.categoryGridView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        binding.setLifecycleOwner(this);
        return binding.getRoot();
    }

    class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {
        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(
                    ItemAdvertisementCategoryBinding.inflate(
                            LayoutInflater.from(parent.getContext()),
                            parent,
                            false
                    )
            );
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.bind(Category.values()[position]);
        }

        @Override
        public int getItemCount() {
            return Category.values().length;
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            private ItemAdvertisementCategoryBinding binding;

            ViewHolder(@NonNull ItemAdvertisementCategoryBinding binding) {
                super(binding.getRoot());
                this.binding = binding;
            }

            void bind(Category item) {
                binding.setItem(item);
                binding.setViewModel(viewModel);
                binding.setLifecycleOwner(CategorySelectionFragment.this);
                // get icon from category
                Drawable icon = ContextCompat.getDrawable(requireContext(), item.getIcon());
                binding.setIcon(icon);
                // change color of icon for the selected icon
                Drawable select_icon = icon.getConstantState().newDrawable().mutate();
                select_icon.setColorFilter(ContextCompat.getColor(requireContext(), R.color.white), PorterDuff.Mode.SRC_ATOP);
                binding.setIconSelected(select_icon);
            }
        }
    }
}
