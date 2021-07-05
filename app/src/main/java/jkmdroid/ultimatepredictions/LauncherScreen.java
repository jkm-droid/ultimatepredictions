package jkmdroid.ultimatepredictions;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Created by jkm-droid on 05/04/2021.
 */

public class LauncherScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.launcher_screen);

        int SPLASH_TIME_OUT = 2000;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //moving to another activity
                startActivity(new Intent(LauncherScreen.this, MainActivity.class));
                finish();

            }
        }, SPLASH_TIME_OUT);

    }
}
