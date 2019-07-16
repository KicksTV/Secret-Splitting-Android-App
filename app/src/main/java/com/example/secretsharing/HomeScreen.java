package com.example.secretsharing;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

public class HomeScreen extends AppCompatActivity {

    private static final String TAG = "HomeScreen";
    private SectionsPageAdapter mSectionsPageAdapter;
    private ViewPager mViewPager;


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_screen);
        Log.d(TAG, "onCreate: Starting.");

        String account = getIntent().getStringExtra("account");

        mSectionsPageAdapter = new SectionsPageAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.container);
        setupViewPager(mViewPager);


        // Navigation Tabs
        TabLayout tabNav = (TabLayout)findViewById(R.id.tabs);
        tabNav.setupWithViewPager(mViewPager);


        // Settings icon button
        ImageView image = (ImageView)findViewById(R.id.settingsIcon);
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(HomeScreen.this, settingsScreen.class); // redirecting to settings_screen.
                startActivity(intent);
            }
        });












    }

    private void setupViewPager(ViewPager viewPager) {
        SectionsPageAdapter adapter = new SectionsPageAdapter(getSupportFragmentManager());
        adapter.addFragment(new secretCodeFragment(), "Secret Code");
        adapter.addFragment(new secretImageFragment(), "Secret Image");

        viewPager.setAdapter(adapter);
    }
}
