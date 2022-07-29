package com.employa.employa.ui.auth;

import android.content.Intent;
import android.os.Bundle;

import com.employa.employa.R;
import com.employa.employa.ui.MainActivity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

public class AuthenticationActivity extends AppCompatActivity implements AuthStateListener {
    private boolean backEnabled;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
    }

    public void onAuthenticationSuccessful() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    public void setBackEnabled(boolean b) {
        backEnabled = b;
    }

    @Override
    public void onBackPressed() {
        if (!backEnabled) {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } else {
            ViewPager pager = findViewById(R.id.sign_up_view_pager);

            if(pager != null && pager.getCurrentItem() != 0)
                pager.setCurrentItem(pager.getCurrentItem() - 1);
            else
                super.onBackPressed();
        }
    }
}
