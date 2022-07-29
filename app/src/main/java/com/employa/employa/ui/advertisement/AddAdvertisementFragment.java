package com.employa.employa.ui.advertisement;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.transition.Slide;

import com.employa.employa.R;
import com.employa.employa.databinding.FragmentAddAdvertisementBinding;
import com.employa.employa.ui.MainActivity;
import com.employa.employa.ui.components.NonScrollableViewPager;
import com.employa.employa.ui.components.BackPressedListener;
import com.employa.employa.utility.Helpers;
import com.employa.employa.utility.Validators;

public class AddAdvertisementFragment extends AddPageFragment implements BackPressedListener {
    private NonScrollableViewPager viewPager;

    private ImageView nextBtn;
    private ImageView prevBtn;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        Slide slide = new Slide();
        slide.setSlideEdge(Gravity.BOTTOM);
        setEnterTransition(slide);

        // Change functionality of back button, see code in MainActivity
        ((MainActivity) requireActivity()).setBackPressedListener(this);

        FragmentAddAdvertisementBinding binding = FragmentAddAdvertisementBinding.inflate(inflater, container, false);

        viewPager = binding.addAdvertisementViewPager;
        nextBtn = binding.nextButton;
        prevBtn = binding.previousButton;
        viewPager.setAdapter(new PageAdapter(getChildFragmentManager()));
        viewPager.setCurrentItem(viewModel.getCurrentState());
        updateBtnVisibilities();
        binding.nextButton.setOnClickListener(v -> {
            if (canContinue()) {
                advancePage(1);
            } else {
                if (container != null) {
                    Toast.makeText(container.getContext(), getString(R.string.make_sure_everything_is_filled_in), Toast.LENGTH_SHORT).show();
                }
            }
        });

        binding.setBackHandler(this);
        binding.setLifecycleOwner(this);
        return binding.getRoot();
    }

    private boolean canContinue() {
        switch (viewPager.getCurrentItem()) {
            case 0: return viewModel.getSelectedCategory().getValue() != null;
            case 1: return !Validators.isEmptyString(viewModel.getDescriptionMutableData()) && !Validators.isEmptyString(viewModel.getTitleMutableData());
            case 2: return !Validators.isEmptyString(viewModel.getPayMutableData()) && viewModel.getLocation().getValue() != null;
            case 3: return viewModel.isDatesOK();
            default: return true;
        }
    }

    private void updateBtnVisibilities() {
        int page = viewPager.getCurrentItem();
        viewModel.setCurrentState(page);
        prevBtn.setVisibility(page == 0 ? View.INVISIBLE : View.VISIBLE);
        nextBtn.setVisibility(page == viewPager.getAdapter().getCount() - 1 ? View.INVISIBLE : View.VISIBLE);
    }

    /**
     * Advances to current+delta page
     * @param delta Delta page nr
     */
    private void advancePage(int delta) {
        // Easiest & cleanest to modify layout here, instead of introducing a binding that gets set here...
        int newPage = viewPager.getCurrentItem() + delta;
        viewPager.setCurrentItem(newPage, true);
        updateBtnVisibilities();
    }

    @Override
    public void goBack() {
        advancePage(-1);
    }

    @Override
    public boolean canGoBack() {
        return viewPager.getCurrentItem() > 0;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Helpers.restoreDefaultKeyboardMode(this);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Helpers.setKeyboardMode(this, WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
    }

    class PageAdapter extends FragmentPagerAdapter {
        private static final int NUM_PAGES = 5;

        PageAdapter(FragmentManager m) {
            super(m, FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        }

        @Override
        @NonNull
        public Fragment getItem(int position) {
            switch(position) {
                case 0: return CategorySelectionFragment.newInstance();
                case 1: return AdvertisementDescriptionFragment.newInstance();
                case 2: return AdvertisementDetailsFragment.newInstance();
                case 3: return AdvertisementPlanningFragment.newInstance();
                case 4: return AdvertisementOverviewFragment.newInstance();
                default: throw new IllegalStateException();
            }
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }
}
