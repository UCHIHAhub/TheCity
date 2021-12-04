/*
 * Copyright (C) 2019  All rights reserved for FaraSource (ABBAS GHASEMI)
 * https://farasource.com
 */
package mobi.androapp.com.city.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import mobi.androapp.com.city.R;
import mobi.androapp.com.city.builder.BuildApp;
import mobi.androapp.com.city.builder.TinyData;

import java.util.Arrays;

public class Menu extends BaseFragment {
    private BottomSheetDialog bottomSheetDialog;
    private TextView nightType;
    private AdView mAdView;
    private InterstitialAd mInterstitialAd;
    private static final String TAG = "Menu";

    private InterstitialAd interstitialA;

    public void loadAd(Activity activit, String AD_UNIT_ID ) {

        AdRequest adRequest = new AdRequest.Builder().build();
        InterstitialAd.load(
                getActivity(),
                AD_UNIT_ID,
                adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        interstitialA = interstitialAd;
                        interstitialA.show(activit);
                        Log.i(TAG, "onAdLoaded");
               //         Toast.makeText(getActivity(), "onAdLoaded()", Toast.LENGTH_SHORT).show();
                        interstitialAd.setFullScreenContentCallback(
                                new FullScreenContentCallback() {
                                    @Override
                                    public void onAdDismissedFullScreenContent() {
                                        // Called when fullscreen content is dismissed.
                                        // Make sure to set your reference to null so you don't
                                        // show it a second time.
                                        interstitialA = null;
                                        Log.d("TAG", "The ad was dismissed.");
                                    }

                                    @Override
                                    public void onAdFailedToShowFullScreenContent(AdError adError) {
                                        // Called when fullscreen content failed to show.
                                        // Make sure to set your reference to null so you don't
                                        // show it a second time.
                                        interstitialA = null;
                                        Log.d("TAG", "The ad failed to show.");
                                    }

                                    @Override
                                    public void onAdShowedFullScreenContent() {
                                        // Called when fullscreen content is shown.
                                        Log.d("TAG", "The ad was shown.");
                                    }
                                });
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error
                        Log.i(TAG, loadAdError.getMessage());
                        interstitialA = null;

                        @SuppressLint("DefaultLocale") String error =
                                String.format(
                                        "domain: %s, code: %d, message: %s",
                                        loadAdError.getDomain(), loadAdError.getCode(), loadAdError.getMessage());
                    //    Toast.makeText(
                   //            getActivity(), "onAdFailedToLoad() with error: " + error, Toast.LENGTH_SHORT)
                   //            .show();
                    }
                });
    }
    @Override
    public void onCreateView(@NonNull LayoutInflater inflater) {
        frameLayout.addView(inflater.inflate(R.layout.fragment_menu, null));
        if (!BuildApp.enablePage) {
            findViewById(R.id.pages).setVisibility(View.GONE);
            findViewById(R.id.pages_divider).setVisibility(View.GONE);
        }
        findViewById(R.id.pages).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startFragment(new Page());
            }
        });
        findViewById(R.id.share).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BuildApp.Share();
            }
        });

        if (TextUtils.isEmpty(BuildApp.phone)) {
            findViewById(R.id.call).setVisibility(View.GONE);
            findViewById(R.id.call_divider).setVisibility(View.GONE);
        }
  //RequestConfiguration configuration = new RequestConfiguration.Builder().build();
        RequestConfiguration configuration = new RequestConfiguration.Builder().setTestDeviceIds(Arrays.asList("6EDD29BEABA074DDFB1F1015A5AD78E0")).build();

        MobileAds.setRequestConfiguration(configuration);
    //    RequestConfiguration configuration = new RequestConfiguration.Builder().build();
      //  MobileAds.setRequestConfiguration(configuration);
        MobileAds.initialize(getActivity(), new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {



            }
        });
//        RequestConfiguration configuration = new RequestConfiguration.Builder().build();
//        MobileAds.setRequestConfiguration(configuration);
        String adunitI = getString(R.string.Interstitial_ad_unit_idm);
        loadAd(getActivity(),adunitI);


        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        findViewById(R.id.call).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:" + BuildApp.phone));
                    startActivity(intent);
                } catch (Exception e) {
                    Toast.makeText(getContext(), "No program can do that.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        findViewById(R.id.email).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("message/rfc822");
                i.putExtra(Intent.EXTRA_EMAIL, new String[]{BuildApp.mail});
                i.putExtra(Intent.EXTRA_SUBJECT,  getResources().getString(R.string.app_name)+ "Enquiry");
                i.putExtra(Intent.EXTRA_TEXT, "");
                try {
                    startActivity(i);
                } catch (ActivityNotFoundException ex) {
                    Toast.makeText(getContext(), "No program can do that.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        findViewById(R.id.telegram).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(BuildApp.telegram));
                    startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(getContext(), "No program can do that..", Toast.LENGTH_SHORT).show();
                }
            }
        });
        findViewById(R.id.instagram).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(BuildApp.instagram));
                    startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(getContext(), "No program can do that..", Toast.LENGTH_SHORT).show();
                }
            }
        });
        findViewById(R.id.service).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BuildApp.setCustomDialog(new AlertDialog.Builder(getContext())
                        .setTitle(getString(R.string.app_name))
                        .setMessage(getString(R.string.msg_service))
                        .setNegativeButton("Cancellation", null)
                        .show());
            }
        });
        findViewById(R.id.about).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startFragment(new AboutUs());
            }
        });


        nightType = findViewById(R.id.night_type);
        String nightMode = TinyData.getInstance().getString("nightMode", "system");
        if (nightMode.equals("system")) {
            nightType.setText("Default system");
        } else if (nightMode.equals("off")) {
            nightType.setText("Inactive");
        }
        findViewById(R.id.l_theme).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bottomSheetDialog != null) {
                    return;
                }
                bottomSheetDialog = new BottomSheetDialog(getContext(), R.style.Theme_Design_BottomSheetDialog);
                bottomSheetDialog.setContentView(getLayoutInflater().inflate(R.layout.dialog_select_night_mode, null));
                bottomSheetDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        bottomSheetDialog = null;
                    }
                });
                bottomSheetDialog.getWindow().findViewById(R.id.design_bottom_sheet).setBackgroundColor(0);
                bottomSheetDialog.show();
                bottomSheetDialog.findViewById(R.id.night_system).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        bottomSheetDialog.dismiss();
                        bottomSheetDialog = null;
                        String nightMode = TinyData.getInstance().getString("nightMode", "system");
                        if (!nightMode.equals("system")) {
                            TinyData.getInstance().putString("nightMode", "system");
                            nightType.setText("Default system");
                            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                        }
                    }
                });
                bottomSheetDialog.findViewById(R.id.night_off).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        bottomSheetDialog.dismiss();
                        bottomSheetDialog = null;
                        String nightMode = TinyData.getInstance().getString("nightMode", "system");
                        if (!nightMode.equals("off")) {
                            TinyData.getInstance().putString("nightMode", "off");
                            nightType.setText("Inactive");
                            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                        }
                    }
                });
                bottomSheetDialog.findViewById(R.id.night_on).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        bottomSheetDialog.dismiss();
                        bottomSheetDialog = null;
                        String nightMode = TinyData.getInstance().getString("nightMode", "system");
                        if (!nightMode.equals("on")) {
                            TinyData.getInstance().putString("nightMode", "on");
                            nightType.setText("active");
                            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                        }
                    }
                });
            }
        });
    }

}
