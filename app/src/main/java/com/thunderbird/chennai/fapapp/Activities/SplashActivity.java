package com.thunderbird.chennai.fapapp.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.crashlytics.android.Crashlytics;
import com.thunderbird.chennai.fapapp.R;

import io.fabric.sdk.android.Fabric;

public class SplashActivity extends AppCompatActivity {

    private SharedPreferences sharedpreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());

        // If the Android version is lower than Jellybean, use this call to hide
        // the status bar.
       /* if (Build.VERSION.SDK_INT < 16) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        else{
            View decorView = getWindow().getDecorView();
            // Hide the status bar.
            int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);

        }*/


        setContentView(R.layout.activity_splash);
        sharedpreferences = getApplicationContext().getSharedPreferences("myprefrence", Context.MODE_PRIVATE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(sharedpreferences.getString("u_id","").equalsIgnoreCase("")) {
                    Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(intent);
                }else{
                    Intent intent = new Intent(SplashActivity.this, PhotographerListActivity.class);
                    startActivity(intent);
                }
                finish();
                /*Intent intent = new Intent(SplashActivity.this, RegSelectEquip.class);
                startActivity(intent);
                finish();*/

            }
        },1500);

    }
}
