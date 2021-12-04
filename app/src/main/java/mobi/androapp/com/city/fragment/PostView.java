/*
 * Copyright (C) 2019  All rights reserved for FaraSource (ABBAS GHASEMI)
 * https://farasource.com
 */
package mobi.androapp.com.city.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import mobi.androapp.com.city.JsontoJave;
import mobi.androapp.com.city.PhotoEditActivity;
import mobi.androapp.com.city.R;
import mobi.androapp.com.city.api.CheckNetworkStatus;
import mobi.androapp.com.city.builder.BuildApp;
import mobi.androapp.com.city.builder.SaveModel;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class PostView extends AppCompatActivity {
    private ImageView image;
    private TextView titleToolbar, author, date;
    private WebView contextWeb, web;
    private NestedScrollView nestedScrollView;
    private RelativeLayout toolbar;
    private boolean isMark, showWeb;
    private View view;
    private ProgressBar progressBar;
    private int tolbarColor;
    private HashMap<String, Object> intentdate;
    public static PostView activity;
    Activity active;
    BottomNavigationView bottomBar;
    BaseFragment[] baseFragments = new BaseFragment[1];
    FinishListener finishListener;
    LinearLayout bottomBarPost;
    ImageView share, screen, comments, fav, iweb,imageback;
    String titled;
  private InterstitialAd minterstitialAd;
    private ViewPager mViewPager;
    private InterstitialAd mInterstitialAd;
      private static final String TAG = "PostView";

    private InterstitialAd interstitialAd;

    public void loadAd(Activity activit,String AD_UNIT_ID ) {

        AdRequest adRequest = new AdRequest.Builder().build();
        InterstitialAd.load(
                this,
                AD_UNIT_ID,
                adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        PostView.this.interstitialAd = interstitialAd;
                        PostView.this.interstitialAd.show(activit);
                        Log.i(TAG, "onAdLoaded");
                     //   Toast.makeText(PostView.this, "onAdLoaded()", Toast.LENGTH_SHORT).show();
                        interstitialAd.setFullScreenContentCallback(
                                new FullScreenContentCallback() {
                                    @Override
                                    public void onAdDismissedFullScreenContent() {
                                        // Called when fullscreen content is dismissed.
                                        // Make sure to set your reference to null so you don't
                                        // show it a second time.
                                        PostView.this.interstitialAd = null;
                                        Log.d("TAG", "The ad was dismissed.");
                                    }

                                    @Override
                                    public void onAdFailedToShowFullScreenContent(AdError adError) {
                                        // Called when fullscreen content failed to show.
                                        // Make sure to set your reference to null so you don't
    // show it a second time.
    PostView.this.interstitialAd = null;
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
        interstitialAd = null;

        String error =
        String.format(
        "domain: %s, code: %d, message: %s",
        loadAdError.getDomain(), loadAdError.getCode(), loadAdError.getMessage());
    //    Toast.makeText(
     //   PostView.this, "onAdFailedToLoad() with error: " + error, Toast.LENGTH_SHORT)
     //   .show();
        }
        });
        }

    private void setupInterstitialAd(Context activit){
        AdRequest adRequest = new AdRequest.Builder().build();

        InterstitialAd.load(activit,"ca-app-pub-3054145908520639/4199269389", adRequest,
                new InterstitialAdLoadCallback() {

                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        mInterstitialAd = interstitialAd;
                        Log.i(TAG, "onAdLoaded");

                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error
                        Log.i(TAG, loadAdError.getMessage());
                        mInterstitialAd = null;
                    }
                });

    }
    String devciceid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.fragment_show_post);
            activity = this;
            active  = this;
        init();
//  RequestConfiguration configuration = new RequestConfiguration.Builder().setTestDeviceIds(Arrays.asList("6EDD29BEABA074DDFB1F1015A5AD78E0")).build();
//        MobileAds.setRequestConfiguration(configuration);
        RequestConfiguration configuration = new RequestConfiguration.Builder().build();
        MobileAds.setRequestConfiguration(configuration);

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {



            }
        });
        String adunitId = getString(R.string.Interstitial_ad_unit_idm);
        //"ca-app-pub-1067848872112069/3866029211";
        loadAd(active,adunitId);

      //  String adunitId = getResources().getString(R.string.Interstitial_ad_unit_id);

        intentdate = (HashMap<String, Object>) getIntent().getSerializableExtra("postintent");
        if (BuildApp.hasID(Objects.requireNonNull(intentdate.get("id")).toString())){
            isMark = true;
            (fav).setImageResource(R.drawable.ic_turned);
        }else{
            isMark = false;
            (fav).setImageResource(R.drawable.ic_turned_not);
        }
        assert intentdate != null;
        if (intentdate.get("url_image").toString().isEmpty()) {
            image.setVisibility(View.GONE);
            view.setVisibility(View.VISIBLE);
        } else {
            Glide.with(this).load(intentdate.get("url_image").toString()).into(image);
            titleToolbar.setVisibility(View.GONE);
            toolbar.setBackgroundColor(0);
            startScroll();
        }


        button();

        show();

      //  mViewPager.setAdapter(fragmentPager);

    }
    

    private void startScroll() {
        nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView nestedScrollView, int i, int i1, int i2, int i3) {
                if (showWeb) {
                    return;
                }
                float y = image.getHeight();
                if (i1 <= y) {
                    if (titleToolbar.getVisibility() == View.VISIBLE) {
                        titleToolbar.setVisibility(View.GONE);
                    }
                    int x = (int) (i1 * 255 / y);
                    toolbar.setBackgroundColor(Color.argb(x, Color.red(tolbarColor), Color.green(tolbarColor), Color.blue(tolbarColor)));
                } else if (titleToolbar.getVisibility() == View.GONE) {
                    toolbar.setBackgroundColor(tolbarColor);
                    titleToolbar.setVisibility(View.VISIBLE);
                    requestTitle();
                }

            }
        });
    }

    private void requestTitle() {
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                titleToolbar.requestFocus();
            }
        }, 100);
    }

    private void button() {
        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // finishFragment();
                activity.finish();
            }
        });
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void show() {
         titled = (String) JsontoJave.String(intentdate.get("title").toString());

        titleToolbar.setText(titled);

        String authora =  intentdate.get("content").toString().trim();
        String authore =  intentdate.get("content").toString().trim();
        int newline = authora.indexOf("\n");

        authora = authora.substring(0,newline).trim();
        int fireword = authora.indexOf(" ",1);
        String authoraa = authora.replace("</p>","").replace("<p>","").trim();
        String firedword = authoraa.substring(0,fireword);
       if (firedword.contains("by")||firedword.contains("By")||firedword.contains("BY")||firedword.contains("bY")){
            author.setText(String.format("Author:%s", authora.replace("bY","").replace("By","").replace("BY","").replace("by","").replace("</p>","").replace("<p>","")));
            authore = authore.substring(newline);  }else{
            author.setText(String.format("Author:%s", BuildApp.authorName));
        }

        date.setText(String.format("%s", intentdate.get("date").toString()));
        contextWeb.getSettings().setJavaScriptEnabled(true);
        contextWeb.getSettings().setJavaScriptCanOpenWindowsAutomatically(false);
        web.getSettings().setJavaScriptEnabled(true);
        web.getSettings().setJavaScriptCanOpenWindowsAutomatically(false);
        web.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                BuildApp.setViewSite(activity, url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (CheckNetworkStatus.isOnline()) {
                    progressBar.setVisibility(View.GONE);
                } else {
                    new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            web.reload();
                        }
                    }, 1000);
                }
            }


        });
        String finalAuthore = authore;
        contextWeb.setWebViewClient(new WebViewClient() {

            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                BuildApp.setViewSite(activity, url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                if (!url.equals("about:blank")) {
                    view.goBack();
                    contextWeb.loadDataWithBaseURL(
                            "",
                            "<html>" +
                                    "<head>" +
                                    "<style type=\"text/css\">" +
                                    "@font-face {" +
                                    "font-family: sans;src: url(\"file:///android_asset/fonts/" + BuildApp.fontName + ".ttf\")}body {font-family: sans !important;font-size: light;text-align: justify;}*{max-width:100%}" +
                                    "</style>" +
                                    "</head>" +

                                    JsontoJave.String(finalAuthore) +
                                    "</body></html>",
                            "text/html",
                            "UTF-8",
                            "");

                }
            }
        });
        contextWeb.loadDataWithBaseURL(
                "",
                "<html>" +
                        "<head>" +
                        "<style type=\"text/css\">" +
                        "@font-face {" +
                        "font-family: sans;src: url(\"file:///android_asset/fonts/" + BuildApp.fontName + ".ttf\")}body {font-family: sans !important;font-size: light;text-align: justify;}*{max-width:100%}" +
                        "</style>" +
                        "</head>" +

                        authore +
                        "</body></html>",
                "text/html",
                "UTF-8",
                "");
    }

    private void init() {
        fav = findViewById(R.id.fav);
        progressBar = findViewById(R.id.progressBar);
        view = findViewById(R.id.fake);
        toolbar = findViewById(R.id.toolbar);
        tolbarColor = ((ColorDrawable) toolbar.getBackground()).getColor();
        nestedScrollView = findViewById(R.id.nestedScrollView);
        titleToolbar = findViewById(R.id.title_toolbar);
        image = findViewById(R.id.image);
        author = findViewById(R.id.author);
        date = findViewById(R.id.date);
        web = findViewById(R.id.web);
        contextWeb = findViewById(R.id.contextWeb);

        mViewPager = findViewById(R.id.container);
        Intent intent = new Intent(Intent.ACTION_SEND);


        bottomBarPost = findViewById(R.id.pbottomBarPost);

        share = findViewById(R.id.share);
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent.putExtra(Intent.EXTRA_TEXT, intentdate.get("title").toString() + ":\n" + intentdate.get("url").toString());
                intent.setType("text/*");
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivity(Intent.createChooser(intent, "Subscription ..."));
            }
        });
        screen = findViewById(R.id.screen);
        screen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PhotoEditActivity.loadBitmapFromView(findViewById(R.id.relative));
                startActivity(new Intent(activity, PhotoEditActivity.class));

            }
        });
        comments = findViewById(R.id.comments);
        comments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommentBottomDialog baseFragment = new CommentBottomDialog();
                Bundle bundle = new Bundle();
                bundle.putString("post", intentdate.get("id").toString());
                bundle.putString("author", intentdate.get("author").toString());
                bundle.putString("titled", titled);
                FragmentManager  mfrag = getSupportFragmentManager();
                baseFragment.setUIArguements(bundle,mfrag,baseFragment);
                baseFragment.setArguments(bundle);
                baseFragment.show(getSupportFragmentManager());


            }
        });

        fav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isMark) {
                    isMark = false;
                    (fav).setImageResource(R.drawable.ic_turned_not);
                    BuildApp.removeBookMark(intentdate.get("id").toString());
                    Toast.makeText(activity, "Post unliked", Toast.LENGTH_SHORT).show();

                }else{
                    isMark = true;
                    SaveModel saveModel = new SaveModel();
                    saveModel.hashMapList.add(intentdate);
                    (fav).setImageResource(R.drawable.ic_turned);
                    Toast.makeText(activity, "Post liked", Toast.LENGTH_SHORT).show();
                    BuildApp.addBookMark(saveModel);
                }

            }
        });
        iweb = findViewById(R.id.iweb);
        iweb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showWeb = !showWeb;
                if (showWeb) {
                    (iweb).setColorFilter(activity.getTheme().getResources().getColor( R.color.colorAccent));
                    if (web.getUrl() == null) {
                        progressBar.setVisibility(View.VISIBLE);
                        web.loadUrl(intentdate.get("url").toString());
                    }
                    web.setVisibility(View.VISIBLE);
                    contextWeb.setVisibility(View.GONE);
                    if (!intentdate.get("url_image").toString().isEmpty()) {
                        image.setVisibility(View.GONE);
                        // this.view.setVisibility(View.VISIBLE);
                        titleToolbar.setVisibility(View.VISIBLE);
                        toolbar.setBackgroundColor(activity.getTheme().getResources().getColor( R.color.colorPrimary));
                        nestedScrollView.setScrollY(0);
                        requestTitle();
                    }
                } else {
                    (iweb).setColorFilter(getResources().getColor(R.color.inActiveTabColor));
                    web.setVisibility(View.GONE);
                    contextWeb.setVisibility(View.VISIBLE);
                    if (!intentdate.get("url_image").toString().isEmpty()) {
                        image.setVisibility(View.VISIBLE);
                        // this.view.setVisibility(View.GONE);
                        titleToolbar.setVisibility(View.GONE);
                        toolbar.setBackgroundColor(0);
                        nestedScrollView.setScrollY(0);
                    }
                }
            }
        });
    }

//    @Override
//    public void onClickBottomBarPost(View view) {
//
//        switch (view.getId()) {
//
//        }
//    }

//    @Override
//    public void onResumeFragment() {
//        showBottomBarPost(true, true, isMark = BuildApp.hasID(intentdate.get("id").toString()));
//        setButtonBarPostStatus(showWeb, isMark);
//    }


}
