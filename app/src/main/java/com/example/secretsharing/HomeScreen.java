package com.example.secretsharing;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.tabs.TabLayout;

import java.util.Random;

public class HomeScreen extends AppCompatActivity {
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_screen);

        // Navigation Tabs
        TabLayout tabNav = (TabLayout)findViewById(R.id.tabNav);

        // Settings icon button
        ImageView image = (ImageView)findViewById(R.id.settingsIcon);
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(HomeScreen.this, settingsScreen.class); // redirecting to settings_screen.
                startActivity(intent);
            }
        });


        Button reconbinButton = (Button)findViewById(R.id.recombinButton);

        // Disable recombinButton
        reconbinButton.setEnabled(false);

        Button hideButton = (Button)findViewById(R.id.hideButton);

        hideButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                EditText numberCode = (EditText)findViewById(R.id.numberCode);

                if (numberCode.getText() != null && !numberCode.equals("")) {
                    if (numberCode.getText().length() != 7) {
                        String secrectCode = numberCode.getText().toString();

                        Random r = new Random();
                        int randomN = 10000000 + (int)(r.nextFloat() * 90000000);

                        System.out.println("SecretCode: " + secrectCode);
                        System.out.println("RandomCode: " + randomN);
                        String rand = String.valueOf(randomN);

                        String result = "";
                        for (int i = 0;i <= 7;i++) {
                            char secC = secrectCode.charAt(i);
                            char ranC = rand.charAt(i);

                            int num = (Integer.valueOf(secC) < Integer.valueOf(ranC)) ? (Integer.valueOf(secC)+10) - Integer.valueOf(ranC) : (Integer.valueOf(secC) - Integer.valueOf(ranC));

                            result = result + "" + num;
                        }
                        System.out.println(result);
                    }else {
                        Toast.makeText(HomeScreen.this, "Code must be 8 numbers long!", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(HomeScreen.this, "Fields cannot be empty!", Toast.LENGTH_SHORT).show();
                }
            }
        });






    }
}
