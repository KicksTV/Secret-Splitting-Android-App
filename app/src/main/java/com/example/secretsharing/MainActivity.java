package com.example.secretsharing;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.andrognito.patternlockview.PatternLockView;
import com.andrognito.patternlockview.listener.PatternLockViewListener;
import com.andrognito.patternlockview.utils.PatternLockUtils;

import java.util.List;

import io.paperdb.Paper;


public class MainActivity extends AppCompatActivity {
    String save_pattern_key = "pattern_code";
    String final_pattern = "";

    PatternLockView mPatternLockView;
    Button patternReset;
    TextView text;
    boolean ResetPattern = false;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Paper.init(this);
        //  Stores the saved pattern

        final String save_pattern = Paper.book().read(save_pattern_key);

        // If the pattern has been set
        if (save_pattern != null && !save_pattern.equals("null")) {
            setContentView(R.layout.pattern_screen);
            mPatternLockView = findViewById(R.id.pattern_lock_view);

            patternReset = findViewById(R.id.btn_patternReset);
            text = findViewById(R.id.textView);

            mPatternLockView.clearPattern();
            mPatternLockView.addPatternLockListener(new PatternLockViewListener() {
                @Override
                public void onStarted() {

                }

                @Override
                public void onProgress(List<PatternLockView.Dot> progressPattern) {

                }

                @Override
                public void onComplete(List<PatternLockView.Dot> pattern) {
                    final_pattern = PatternLockUtils.patternToString(mPatternLockView,pattern);
                    if (final_pattern.equals(save_pattern)) {
                        Toast.makeText(MainActivity.this, "Password correct!", Toast.LENGTH_SHORT).show();
                        if (ResetPattern) {
                            Paper.book().write(save_pattern_key, "null");
                            Intent intent=new Intent(MainActivity.this, MainActivity.class); // redirecting to pattern_screen.
                            startActivity(intent);
                        }else {
                            Intent intent = new Intent(MainActivity.this, Authentication.class); // redirecting to pattern_screen.
                            startActivity(intent);
                        }
                    }else {
                        Toast.makeText(MainActivity.this, "Password incorrect!", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCleared() {

                }
            });

            patternReset.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (patternReset.getText().equals("Reset Pattern")) {
                        text.setText("Enter Pattern to RESET!!!");
                        patternReset.setText("Cancel Reset Pattern");
                        ResetPattern = true;
                    }else {
                        text.setText("Enter Pattern");
                        patternReset.setText("Reset Pattern");
                        ResetPattern = false;
                    }
                }
            });


        // If pattern hasn't been set
        }else {
            setContentView(R.layout.activity_main);
            mPatternLockView = (PatternLockView)findViewById(R.id.pattern_lock_view);
            mPatternLockView.addPatternLockListener(new PatternLockViewListener() {
                @Override
                public void onStarted() {

                }

                @Override
                public void onProgress(List<PatternLockView.Dot> progressPattern) {

                }

                @Override
                public void onComplete(List<PatternLockView.Dot> pattern) {
                    final_pattern = PatternLockUtils.patternToString(mPatternLockView, pattern);
                }

                @Override
                public void onCleared() {

                }
            });


            Button btnSetup = (Button)findViewById(R.id.btnSetPattern);
            btnSetup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Paper.book().write(save_pattern_key, final_pattern);
                    Toast.makeText(MainActivity.this, "Saved pattern successful!", Toast.LENGTH_SHORT).show();
                    Intent intent=new Intent(MainActivity.this, MainActivity.class); // redirecting to pattern_screen.
                    startActivity(intent);
                }

            });
        }

    }



}
