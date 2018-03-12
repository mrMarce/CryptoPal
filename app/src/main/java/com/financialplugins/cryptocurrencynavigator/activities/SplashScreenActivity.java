package com.financialplugins.cryptocurrencynavigator.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.ads.MobileAds;

import com.financialplugins.cryptocurrencynavigator.R;

public class SplashScreenActivity extends AppCompatActivity {
    private static final String TAG = "SplashScreenActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_splash_screen);
        MobileAds.initialize(this, getString(R.string.admob_app_id));
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();

    }
}
