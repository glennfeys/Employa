package com.employa.employa.ui.discovery;

import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.employa.employa.R;
import com.employa.employa.databinding.FragmentDiscoveryBinding;
import com.employa.employa.models.Advertisement;
import com.employa.employa.repository.MainRepository;
import com.employa.employa.ui.components.ViewType;
import com.employa.employa.ui.components.FabClickHandler;
import com.employa.employa.ui.discovery.adapter.AdvertisementAdapter;
import com.employa.employa.ui.discovery.filter.CategoryFilterDialogListener;
import com.employa.employa.ui.discovery.filter.DistanceFilterDialogListener;
import com.employa.employa.ui.discovery.filter.PeriodFilterDialogListener;
import com.employa.employa.utility.LocationPermissionHelper;
import com.employa.employa.viewmodel.AddAdvertisementViewModel;
import com.employa.employa.viewmodel.FeedViewModel;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.tylersuehr.esr.DefaultLoadingState;
import com.tylersuehr.esr.EmptyStateRecyclerView;
import com.tylersuehr.esr.TextStateDisplay;

import eightbitlab.com.blurview.BlurView;
import eightbitlab.com.blurview.RenderScriptBlur;

public class DiscoveryFragment extends Fragment implements FabClickHandler, ButtonClickListener, MaterialSearchBar.OnSearchActionListener {
    private static final float BLUR_RADIUS = 10f;
    private FeedViewModel mViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate & Stel ViewModel in
        FragmentDiscoveryBinding binding = FragmentDiscoveryBinding.inflate(inflater, container, false);
        mViewModel = ViewModelProviders.of(this).get(FeedViewModel.class);

        // Zet listener voor de searchBar
        MaterialSearchBar searchBar = binding.searchBar;
        searchBar.setOnSearchActionListener(this);
        searchBar.setSpeechMode(false);

        AdvertisementAdapter adapter = new AdvertisementAdapter(this.getContext(), ViewType.ADVERTISEMENT_NORMAL,this);
        binding.discoveryRecyclerView.setAdapter(adapter);
        mViewModel.getAdvertisements().observe(this, adapter::submitList);
        initRecyclerView(binding.discoveryRecyclerView);

        mViewModel.clear();

        // Handel de Click-gebeurtenissen af van de filterknoppen
        binding.clearFilters.setOnClickListener(v -> mViewModel.clearFilters());

        binding.filterCategories.setOnClickListener(new CategoryFilterDialogListener(v -> {
            mViewModel.setCategories(v);
            mViewModel.refresh();
        }, mViewModel.getCategories()));

        binding.filterPeriod.setOnClickListener(new PeriodFilterDialogListener(v -> {
            mViewModel.setPeriod(v);
            mViewModel.refresh();
        }, mViewModel.getPeriod()));

        binding.filterDistance.setOnClickListener(new DistanceFilterDialogListener(new LocationPermissionHelper(getContext(), getActivity()), v -> {
            mViewModel.setRadius(v);
            mViewModel.refresh();
        }, mViewModel.getRadius()));

        // Stel de bindings in
        binding.setLifecycleOwner(this);
        binding.setFabHandler(this);
        binding.setViewModel(mViewModel);

        enableBlurring(binding.discoveryRecyclerView, binding.search);

        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        mViewModel.refresh();
    }

    /**
     * Stel state displays in voor de recyclerview
     */
    private void initRecyclerView(EmptyStateRecyclerView recyclerView) {
        assert getContext() != null;

        TextStateDisplay no_results = new TextStateDisplay(getContext(), getString(R.string.title_no_results), getString(R.string.description_no_results));
        no_results.setPadding(0, 200, 0, 0);
        TextStateDisplay something_wrong = new TextStateDisplay(getContext(), getString(R.string.title_error), getString(R.string.description_error));
        something_wrong.setPadding(0, 200, 0, 0);

        recyclerView.setStateDisplay(EmptyStateRecyclerView.STATE_LOADING,
                new DefaultLoadingState(getContext(), getString(R.string.loading)));
        recyclerView.setStateDisplay(EmptyStateRecyclerView.STATE_EMPTY, no_results);
        recyclerView.setStateDisplay(EmptyStateRecyclerView.STATE_ERROR, something_wrong);
    }

    /**
     * Met deze functie passen we een blur toe gegeven een onderliggende ViewGroup
     * en de view die de blur moet krijgen.
     *
     * @param rootView Achtergrond
     * @param view     View die het blur effect moet krijgen.
     */
    private void enableBlurring(ViewGroup rootView, BlurView view) {
        view.setupWith(rootView)
                .setBlurAlgorithm(new RenderScriptBlur(getContext()))
                .setBlurRadius(BLUR_RADIUS)
                .setHasFixedTransformationMatrix(true);
    }

    /**
     * Handle "React"-button click
     *
     * @param advertisement The advertisement
     */
    @Override
    public void onReactClicked(Advertisement advertisement) {
        mViewModel.reactTo(advertisement, bundle -> {
            NavController navController = NavHostFragment.findNavController(DiscoveryFragment.this);
            navController.navigate(R.id.action_navigation_home_to_chatFragment, bundle);
        });
    }

    /**
     * Handle click on "More Info"-button
     *
     * @param advertisement The advertisement
     */
    @Override
    public void onMoreInfoClicked(Advertisement advertisement) {
        Bundle bundle = new Bundle();
        bundle.putString(AdvertisementDetailsFragment.ADV_ID_BUNDLE_NAME, advertisement.getId());

        NavHostFragment.findNavController(this).navigate(R.id.action_navigation_home_to_advertisementDetailsFragment, bundle);
    }

    /**
     * Handle click event on the Floating Action Button
     */
    @Override
    public void onFabClick() {
        ViewModelProviders.of(requireActivity()).get(AddAdvertisementViewModel.class).reset();
        NavHostFragment.findNavController(this).navigate(R.id.action_navigation_home_to_addAdvertisementFragment);
    }

    @Override
    public void goToProfile(String uid) {
        NavController navController = NavHostFragment.findNavController(this);

        Bundle bundle = new Bundle();
        bundle.putString(MainRepository.UID_BUNDLE, uid);

        navController.navigate(R.id.action_navigation_home_to_profileFragment, bundle);
    }

    /**
     * Handle a state change on the MaterialDesignSearchField
     *
     * @param enabled State of SearchField
     */
    @Override
    public void onSearchStateChanged(boolean enabled) {
        if (!enabled) {
            mViewModel.getQuery().setValue("");
            mViewModel.refresh();
        }
    }

    /**
     * Handle search confirmation
     *
     * @param text SearchQuery
     */
    @Override
    public void onSearchConfirmed(CharSequence text) {
        mViewModel.refresh();
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
    }

    @Override
    public void onButtonClicked(int buttonCode) {
    }
}
