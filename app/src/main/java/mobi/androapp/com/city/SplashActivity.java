/*
 * Copyright (C) 2019  All rights reserved for FaraSource (ABBAS GHASEMI)
 * https://farasource.com
 */
package mobi.androapp.com.city;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import mobi.androapp.com.city.api.CheckNetworkStatus;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;

import static mobi.androapp.com.city.builder.BuildApp.showSplash;
import static mobi.androapp.com.city.builder.BuildApp.splashTime;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle superSave) {
        super.onCreate(superSave);

        if (!showSplash) {
            main();
            return;
        }

        setContentView(R.layout.activity_splash);
        if (!CheckNetworkStatus.isOnline())
            Toast.makeText(this, "Network not available!", Toast.LENGTH_SHORT).show();
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                main();
            }
        }, splashTime * 1000);
    }

    private void main() {
        if (isFinishing()) {
            return;
        }
        startActivity(new Intent(SplashActivity.this, mobi.androapp.com.city.LuancherActivity.class));
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        finish();
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}