/*
 * Copyright (C) 2019  All rights reserved for FaraSource (ABBAS GHASEMI)
 * https://farasource.com
 */
package mobi.androapp.com.city.fragment;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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

import mobi.androapp.com.city.R;
import mobi.androapp.com.city.adapter.OnClickListener;
import mobi.androapp.com.city.adapter.PostsAdapter;
import mobi.androapp.com.city.api.ApiSeivice;
import mobi.androapp.com.city.api.CheckNetworkStatus;
import mobi.androapp.com.city.api.ConnectionManager;
import mobi.androapp.com.city.builder.Application;
import mobi.androapp.com.city.builder.BuildApp;
import mobi.androapp.com.city.builder.SaveModel;
import mobi.androapp.com.city.builder.SpacesItemDecoration;
import mobi.androapp.com.city.builder.TinyData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static mobi.androapp.com.city.builder.BuildApp.divider;

public class Home extends BaseFragment {
    private PostsAdapter postsAdapter;
    private ProgressBar progress;
    private LinearLayout net;
    private SwipeRefreshLayout pullRefreshLayout;
    private int page = 1;
    private StaggeredGridLayoutManager gridLayoutManager;
    private boolean isLoading, hasNext;

    @Override
    public void onCreateView(@NonNull LayoutInflater inflater) {
        int columns = 2;
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            columns = 4;}
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.spacing);
        frameLayout.addView(inflater.inflate(R.layout.fragment_list_main, null));
        pullRefreshLayout = findViewById(R.id.pull);
        RecyclerView recView = findViewById(R.id.recycler_view);
        TextView check = findViewById(R.id.check);
        net = findViewById(R.id.error_net);
        progress = findViewById(R.id.progressBar);
        //  RequestConfiguration configuration = new RequestConfiguration.Builder().setTestDeviceIds(Arrays.asList("6EDD29BEABA074DDFB1F1015A5AD78E0")).build();
        //        MobileAds.setRequestConfiguration(configuration);
        RequestConfiguration configuration = new RequestConfiguration.Builder().build();
        MobileAds.setRequestConfiguration(configuration);
        MobileAds.initialize(getActivity(), new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {



            }
        });
//        RequestConfiguration configuration = new RequestConfiguration.Builder().build();
//        MobileAds.setRequestConfiguration(configuration);
        String adunitI = getString(R.string.Interstitial_ad_unit_idm);

        postsAdapter = new PostsAdapter();
        postsAdapter.setDate(new ArrayList<HashMap<String, Object>>());
        postsAdapter.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view, int position) {
                Intent meadintent = new Intent(getContext(),PostView.class);
                meadintent.putExtra("postintent",postsAdapter.getDate().get(position));
                startActivity(meadintent);
            }
        });
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), columns);
       // gridLayoutManager = new StaggeredGridLayoutManager(BuildApp.showType == 3 ? BuildApp.getCountPx() : 1, StaggeredGridLayoutManager.VERTICAL);
        assert recView != null;
        recView.addItemDecoration(new SpacesItemDecoration(spacingInPixels));
        recView.setLayoutManager(gridLayoutManager);
        recView.setAdapter(postsAdapter);
        if (divider) {
            DividerItemDecoration itemDecor = new DividerItemDecoration(getContext(), 1);
            recView.addItemDecoration(itemDecor);
        }
        SaveModel saveModel = Application.easySave.retrieveModel("Luancher", SaveModel.class);
        if (saveModel != null) {
            page = saveModel.page;
            hasNext = saveModel.hasNext;
            postsAdapter.setDate(saveModel.hashMapList);
            progress.setVisibility(View.GONE);
        } else {
            getPosts();
        }

        recView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) {

                        if (hasNext) {
                            if (!isLoading) {
                             //   progress.setVisibility(View.VISIBLE);
                                page++;
                                getPosts();
                            }
                        }

                }
            }
        });
        pullRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (isLoading) {
                    return;
                }
                page = 1;
                Application.easySave.saveModel("Luancher", null);
                postsAdapter.getDate().clear();
                postsAdapter.notifyDataSetChanged();
                progress.setVisibility(View.VISIBLE);
                loadAd(getActivity(),adunitI);
                getPosts();
            }
        });
        check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                net.setVisibility(View.GONE);
                progress.setVisibility(View.VISIBLE);
                loadAd(getActivity(),adunitI);
                getPosts();
            }
        });
    }

    private InterstitialAd interstitialA;

    private static final String TAG = "MCategory";

    private void loadAd(Activity activit, String AD_UNIT_ID ) {


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


                    }
                });
    }
    private void getPosts() {
        if (isLoading) {
            return;
        }
        pullRefreshLayout.setEnabled(false);
        isLoading = true;
        new ConnectionManager.Builder()
                .setURL(ApiSeivice.getPosts(String.valueOf(page)))
                .request(new ConnectionManager.ResultConnection() {
                    @Override
                    public void onSuccess(JSONArray jsonArray) throws JSONException {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject object = jsonArray.getJSONObject(i);
                            HashMap<String, Object> add = new HashMap<>();
                            add.put("id", object.getString("id"));
                            add.put("categories", object.getJSONArray("categories").toString());
                            add.put("url", object.getString("link"));
                            add.put("title", object.getJSONObject("title").getString("rendered"));
                            add.put("content", object.getJSONObject("content").getString("rendered"));
                            String excerpt = String.valueOf(Html.fromHtml(object.getJSONObject("excerpt").getString("rendered")));
                            add.put("excerpt", excerpt);
                            add.put("date", object.getString("date").replace("T", " "));
                            add.put("author", object.getString("author"));
                            if (object.has("yoast_head_json") && object.getJSONObject("yoast_head_json").has("og_image")) {
                                JSONArray array = object.getJSONObject("yoast_head_json").getJSONArray("og_image");
                                add.put("url_image", array.getJSONObject(array.length() - 1).getString("url"));
                            } else if (object.has("yoast_head")) {
                                String head = object.getString("yoast_head");
                                Pattern pattern = Pattern.compile("property=\"og:image\" content=\"(.*)\"");
                                Matcher matcher = pattern.matcher(head);
                                if (matcher.find()) {
                                    head = matcher.group();
                                    add.put("url_image", head.substring(29, head.length() - 1));
                                } else {
                                    add.put("url_image", "");
                                }
                            } else {
                                add.put("url_image", "");
                            }
                            postsAdapter.getDate().add(add);
                        }
                        hasNext = jsonArray.length() == BuildApp.limitPage;
                        SaveModel saveModel = new SaveModel();
                        saveModel.hashMapList.addAll(postsAdapter.getDate());
                        saveModel.page = page;
                        saveModel.hasNext = hasNext;
                        Application.easySave.saveModel("Luancher", saveModel);
                        postsAdapter.notifyDataSetChanged();
                        TinyData.getInstance().putStringCashe("Luancher");
                        progress.setVisibility(View.GONE);
                        if (pullRefreshLayout.isRefreshing()) {
                            pullRefreshLayout.setRefreshing(false);
                        }
                        pullRefreshLayout.setEnabled(true);
                        isLoading = false;
                    }

                    @Override
                    public void onFail(String error) {
                        if (CheckNetworkStatus.isOnline()) {
                            getPosts();
                        } else if (postsAdapter.getDate().isEmpty()) {
                            net.setVisibility(View.VISIBLE);
                            progress.setVisibility(View.GONE);
                        } else {
                            page--;
                            Toast.makeText(getContext(), "Network not available!", Toast.LENGTH_SHORT).show();
                            progress.setVisibility(View.GONE);
                        }
                        if (pullRefreshLayout.isRefreshing()) {
                            pullRefreshLayout.setRefreshing(false);
                        }
                        pullRefreshLayout.setEnabled(true);
                        isLoading = false;
                    }
                });
    }
}
