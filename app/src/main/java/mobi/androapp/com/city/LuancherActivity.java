/*
 * Copyright (C) 2019  All rights reserved for FaraSource (ABBAS GHASEMI)
 * https://farasource.com
 */
package mobi.androapp.com.city;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import mobi.androapp.com.city.adapter.OnClickListener;
import mobi.androapp.com.city.builder.TinyData;
import mobi.androapp.com.city.fragment.BaseFragment;
import mobi.androapp.com.city.fragment.BookMark;
import mobi.androapp.com.city.fragment.FinishListener;
import mobi.androapp.com.city.fragment.Home;
import mobi.androapp.com.city.fragment.MCategory;
import mobi.androapp.com.city.fragment.Menu;
import mobi.androapp.com.city.fragment.Search;

import java.util.Arrays;
import java.util.Date;

import eu.dkaratzas.android.inapp.update.Constants;
import eu.dkaratzas.android.inapp.update.InAppUpdateManager;
import eu.dkaratzas.android.inapp.update.InAppUpdateStatus;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;

import static mobi.androapp.com.city.builder.BuildApp.doubleClickForExit;

public class LuancherActivity extends AppCompatActivity implements InAppUpdateManager.InAppUpdateHandler{
    @SuppressLint("StaticFieldLeak")
    public static LuancherActivity activity;
    ViewPager2 viewPager;
    long preTime;
    BottomNavigationView bottomBar;
    LinearLayout bottomBarPost;
    ImageView share, screen, comments, fav, web,search;
    OnClickListener onClickListener;
    BaseFragment[] baseFragments = new BaseFragment[5];
    FinishListener finishListener;
    RelativeLayout searchtool;
    int id = 0;
    private InAppUpdateManager inAppUpdateManager;
    private static final int REQ_CODE_VERSION_UPDATE = 530;
    private static final String TAG = "LuancherActivity";
    Context mcontext;

    @Override
    public void onInAppUpdateError(int code, Throwable error) {
        /*
         * Called when some error occurred. See Constants class for more details
         */
        Log.d(TAG, "code: " + code, error);
    }

    @Override
    public void onInAppUpdateStatus(InAppUpdateStatus status) {

        /*
         * Called when the update status change occurred.
         */

//        progressBar.setVisibility(status.isDownloading() ? View.VISIBLE : View.GONE);
//
//        tvVersionCode.setText(String.format("Available version code: %d", status.availableVersionCode()));
//        tvUpdateAvailable.setText(String.format("Update available: %s", String.valueOf(status.isUpdateAvailable())));

        if (status.isDownloaded()) {
            View rootView = getWindow().getDecorView().findViewById(android.R.id.content);
            Snackbar snackbar = Snackbar.make(rootView,
                    "An update has just been downloaded.",Snackbar.LENGTH_INDEFINITE);
            snackbar.setAction("RESTART",view -> {
                inAppUpdateManager.completeUpdate();
            });
            snackbar.show();
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
         if (requestCode == REQ_CODE_VERSION_UPDATE) {
            if (resultCode == Activity.RESULT_CANCELED) {
                // If the update is cancelled by the user,
                // you can request to start the update again.
                inAppUpdateManager.checkForAppUpdate();

                Log.d(TAG, "Update flow failed! Result code: " + resultCode);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    public static long weeksBetwen(int week) {
        int minutesinweek = 10080*60*1000*week;

        return new Date().getTime() - minutesinweek; }int weeks;
    public static  Context ncontext;
    PrefManager prefManager ;
    @Override
    protected void onCreate(Bundle superSave) {
        super.onCreate(superSave);
        activity = this;
        mcontext = this;
        ncontext = this;
        DatabaseReference mySupport = mDatabase.child("TheCity");
        DatabaseReference myweeks = mDatabase.child("weeks");
        //check if user has support database
        // Checking for first time launch - before calling setContentView()
        prefManager = new PrefManager(this);
        if (!prefManager.isFirstTimeLaunch()) {
            TinyData.getInstance().putString("nightMode", "system");

            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);

        }
       myweeks.addValueEventListener(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull  DataSnapshot snapshot) {
               long mead = (long)snapshot.getValue();
            weeks = (int) mead;

               mySupport.addValueEventListener(new ValueEventListener() {
                   @Override
                   public void onDataChange(@NonNull  DataSnapshot snapshot1) {
                       if(snapshot1.exists()){
                           long value = (long) snapshot1.getValue();
                           Date date1 = new Date(value);
                           if(date1.getTime()<weeksBetwen(weeks)){
                               finish();
                           } else {

                           }
                       }
                   }

                   @Override
                   public void onCancelled(@NonNull  DatabaseError error) {

                   }
               });
           }

           @Override
           public void onCancelled(@NonNull  DatabaseError error) {

           }
       });
        mySupport.keepSynced(true);
        init();
        inAppUpdateManager = InAppUpdateManager.Builder(this, REQ_CODE_VERSION_UPDATE)
                .resumeUpdates(true)
                .handler(this)
                .mode(Constants.UpdateMode.FLEXIBLE)
                .useCustomNotification(true);
        inAppUpdateManager.checkForAppUpdate();

    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected( MenuItem item) {
        if (item.getItemId() == R.id.action_voice_search) {
            id = 2;
        } else {
            throw new RuntimeException("bottomBar:: tabId not found!.");
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("NonConstantResourceId")
    private void init() {
        setContentView(R.layout.activity_luancher);
        searchtool = findViewById(R.id.rtoolbar);
        viewPager = findViewById(R.id.contentContainer);
        viewPager.setOffscreenPageLimit(4);
        viewPager.setUserInputEnabled(false);
        viewPager.setAdapter(new FragmentStateAdapter(this) {

            @NonNull
            @Override
            public Fragment createFragment(int position) {
                switch (position) {
                    case 0:
                        baseFragments[0] = new Home();
                        return baseFragments[0];
                    case 1:

                        baseFragments[1] = new MCategory();
                        return baseFragments[1];
                    case 2:
                        baseFragments[2] = new Search();
                        return baseFragments[2];
                    case 3:
                        baseFragments[3] = new BookMark();
                        return baseFragments[3];
                    default:
                        baseFragments[4] = new Menu();
                        return baseFragments[4];
                }
            }

            @Override
            public int getItemCount() {
                return 5;
            }
        });
        bottomBarPost = findViewById(R.id.bottomBarPost);
        share = findViewById(R.id.share);
        search = findViewById(R.id.search);
        search.setOnClickListener(v -> {
            id=2;
            showsearchbar(false);
            viewPager.setCurrentItem(id);
            if (baseFragments[id] != null)
                finishListener = baseFragments[id].getFinishFragment();
        });
        share.setOnClickListener(new BottomBarPost());
        screen = findViewById(R.id.screen);
        screen.setOnClickListener(new BottomBarPost());
        comments = findViewById(R.id.comments);
        comments.setOnClickListener(new BottomBarPost());
        fav = findViewById(R.id.fav);
        fav.setOnClickListener(new BottomBarPost());
        web = findViewById(R.id.web);
        web.setOnClickListener(new BottomBarPost());
        bottomBar = findViewById(R.id.bottomBar);
        bottomBar.setOnItemSelectedListener(item -> {

            switch (item.getItemId()) {
                case R.id.tab_home:
                    id = 0;
                    showsearchbar(true);
                    break;
                case R.id.tab_categories:
                    id = 1;
                    showsearchbar(false);
                    break;
                case R.id.tab_favorites:
                    id = 3;
                    showsearchbar(false);
                    break;
                case R.id.tab_menu:
                    id = 4;
                    showsearchbar(false);
                    break;
                default:
                    throw new RuntimeException("bottomBar:: tabId not found!.");
            }
            viewPager.setCurrentItem(id);
            if (baseFragments[id] != null)
                finishListener = baseFragments[id].getFinishFragment();
            return true;
        });
        if (baseFragments[id] != null){
            finishListener = baseFragments[id].getFinishFragment();

    }
        bottomBar.setOnItemReselectedListener(item -> {
            if (finishListener != null) {
                finishListener.finishFragment();
            }
        });
    }

    public void showBottomBarPost(boolean show, boolean showComment, boolean isMark, OnClickListener onClickListener) {
        if (showComment) {
            comments.setVisibility(View.VISIBLE);
        } else {
            comments.setVisibility(View.GONE);
        }
        if (isMark) {
            fav.setImageResource(R.drawable.ic_turned);
        } else {
            fav.setImageResource(R.drawable.ic_turned_not);
        }
        showBottomBarPost(show);
        this.onClickListener = onClickListener;
    }

    public void showBottomBarPost(boolean show) {
        if (show) {
            bottomBar.setVisibility(View.INVISIBLE);
            this.bottomBarPost.setVisibility(View.VISIBLE);
            web.setColorFilter(getResources().getColor(R.color.inActiveTabColor));
        } else {
            bottomBar.setVisibility(View.VISIBLE);
            this.bottomBarPost.setVisibility(View.GONE);
        }
    }

    public void setStatus(boolean showWeb, boolean isMark) {
        if (showWeb) {
            web.setColorFilter(getTheme().getResources().getColor(R.color.colorAccent));
        }
        if (isMark) {
            fav.setImageResource(R.drawable.ic_turned);
        }
    }
    public void showsearchbar(boolean showWeb) {
        if (showWeb) {
            searchtool.setVisibility(View.VISIBLE);
        }else{
            searchtool.setVisibility(View.GONE);
        }
    }

    @Override
    public void onBackPressed() {
        if(finishListener!=null){
        if (finishListener.finishFragment()) {
            return;
        }}
        if (doubleClickForExit) {
            long currentTime = new Date().getTime();
            if ((currentTime - preTime) > 2000) {
                Toast.makeText(LuancherActivity.this, "Click again to exit ...", Toast.LENGTH_SHORT).show();
                preTime = currentTime;
            } else {
                LuancherActivity.this.finish();
            }
        } else {
            new AlertDialog.Builder(this)
                    .setTitle("Exit the app")
                    .setMessage("Do you want to log out?")
                    .setPositiveButton("No", null)
                    .setNeutralButton("Yes", (dialog, which) -> LuancherActivity.this.finish())
                    .show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public class BottomBarPost implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            if (onClickListener == null) {
                return;
            }
            onClickListener.onClick(v, 0);
        }
    }

}