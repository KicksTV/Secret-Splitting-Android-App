package com.example.secretsharing;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.tabs.TabLayout;

public class HomeScreen extends AppCompatActivity {
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_screen);


        TabLayout tabNav = (TabLayout)findViewById(R.id.tabNav);




        ImageView image = (ImageView)findViewById(R.id.settingsIcon);
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(HomeScreen.this, settingsScreen.class); // redirecting to settings_screen.
                startActivity(intent);
            }
        });
    }
}
