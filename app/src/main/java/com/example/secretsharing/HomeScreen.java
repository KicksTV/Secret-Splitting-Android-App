package com.example.secretsharing;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class HomeScreen extends AppCompatActivity {

    private static final String TAG = "HomeScreen";
    private SectionsPageAdapter mSectionsPageAdapter;
    private ViewPager mViewPager;
    private TextView username;
    private TabLayout tabNav;
    private ImageView settingIcon;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_screen);
        Log.d(TAG, "onCreate: Starting.");


        // set username
        setUserName();


        // Setting up tab navigation
        mViewPager = (ViewPager) findViewById(R.id.container);
        setupViewPager(mViewPager);
        mSectionsPageAdapter = new SectionsPageAdapter(getSupportFragmentManager());
        // Navigation Tabs
        tabNav = (TabLayout)findViewById(R.id.tabs);
        tabNav.setupWithViewPager(mViewPager);


        // Settings icon button
        settingIcon = (ImageView)findViewById(R.id.settingsIcon);
        settingIcon.setOnClickListener(new View.OnClickListener() {
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

    private void setUserName() {
        username = findViewById(R.id.txt_username);

        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null) {
            // already signed in
            FirebaseUser user = mAuth.getCurrentUser();

            username.setText(user.getDisplayName());
        }
    }
}
