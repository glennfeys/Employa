package com.employa.employa.ui;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkRequest;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.employa.employa.R;
import com.employa.employa.repository.MainRepository;
import com.employa.employa.ui.offline.OfflineDialog;
import com.employa.employa.ui.components.BackPressedListener;
import com.employa.employa.utility.Helpers;
import com.employa.employa.utility.MessagingListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

import java.util.HashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity implements NavController.OnDestinationChangedListener {
    private BackPressedListener backPressedListener;
    private MessagingListener authStateListener;
    private boolean active = false;
    private OfflineDialog dialog;

    // Voor welke fragments moet de bottom nav onzichtbaar zijn?
    // Als er weggenavigeerd wordt binnen 1 bottom nav item, dan moet de bottom nav hidden zijn
    private static final Set<Integer> navBarVisibleOnFragments = new HashSet<>();
    static {
        navBarVisibleOnFragments.add(R.id.navigation_home);
        navBarVisibleOnFragments.add(R.id.navigation_messages);
        navBarVisibleOnFragments.add(R.id.navigation_profile);
        navBarVisibleOnFragments.add(R.id.navigation_settings);
    }

    @Override
    public void onStart() {
        super.onStart();
        active = true;

        if (!Helpers.isNetworkAvailable(this))
            showOffline();
    }

    @Override
    public void onStop() {
        super.onStop();
        active = false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Wanneer internet uitgaat wordt onLost opgeroepen, zelf als je niet in de app zit
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkRequest.Builder builder = new NetworkRequest.Builder();
        connectivityManager.registerNetworkCallback(builder.build(), new ConnectivityManager.NetworkCallback() {
            @Override
            public void onLost(Network network) {
                if (active) {
                    showOffline();
                }
            }

            @Override
            public void onAvailable(Network network) {
                if (active && dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        authStateListener = new MessagingListener();
        FirebaseAuth.getInstance().addAuthStateListener(authStateListener);

        setContentView(R.layout.activity_main);
        final BottomNavigationView nav = findViewById(R.id.navigation);
        final NavController navController = Navigation.findNavController(findViewById(R.id.main_content_nav_host_fragment));
        NavigationUI.setupWithNavController(nav, navController);
        navController.addOnDestinationChangedListener(this);
    }

    public void setBackPressedListener(BackPressedListener backPressedListener) {
        this.backPressedListener = backPressedListener;
    }

    @Override
    public void onBackPressed() {
        if(backPressedListener != null) {
            if(backPressedListener.canGoBack()) {
                backPressedListener.goBack();
                return;
            } else
                setBackPressedListener(null);
        }

        super.onBackPressed();
    }

    @Override
    public void onDestinationChanged(@NonNull NavController controller, @NonNull NavDestination destination, @Nullable Bundle arguments) {
        final BottomNavigationView nav = findViewById(R.id.navigation);

        boolean checker;
        if(arguments != null && arguments.containsKey(MainRepository.UID_BUNDLE)) {
            checker = MainRepository.getInstance().isMe(arguments.getString(MainRepository.UID_BUNDLE));
        } else {
            checker = navBarVisibleOnFragments.contains(destination.getId());
        }

        nav.setVisibility(checker ? View.VISIBLE : View.GONE);
    }

    private void showOffline() {
        dialog = new OfflineDialog();
        dialog.show(getSupportFragmentManager(), "Offline dialog");
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        FirebaseAuth.getInstance().removeAuthStateListener(authStateListener);
    }
}
