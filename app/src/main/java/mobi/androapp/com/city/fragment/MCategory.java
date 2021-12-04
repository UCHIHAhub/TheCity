/*
 * Copyright (C) 2019  All rights reserved for FaraSource (ABBAS GHASEMI)
 * https://farasource.com
 */
package mobi.androapp.com.city.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
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

import mobi.androapp.com.city.LuancherActivity;
import mobi.androapp.com.city.R;
import mobi.androapp.com.city.api.ApiSeivice;
import mobi.androapp.com.city.api.CheckNetworkStatus;
import mobi.androapp.com.city.api.ConnectionManager;
import mobi.androapp.com.city.builder.Application;
import mobi.androapp.com.city.builder.CategoryAdaper;
import mobi.androapp.com.city.builder.CatergoryDetails;
import mobi.androapp.com.city.builder.GenreTheCity;
import mobi.androapp.com.city.builder.SaveModel;
import mobi.androapp.com.city.builder.TinyData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class MCategory extends BaseFragment {
    private String status = "";
    private List<HashMap<String, Object>> hash;
    private ProgressBar progressBar;
    private TextView check;
    private LinearLayout net;
    private CategoryAdaper categoryAdaper;
    private boolean isLoading;
    private SwipeRefreshLayout refreshLayout;
    private int page = 1;
    GenreTheCity genreTheCity;
    private boolean  hasNexte;
    private boolean  hasNext;
    CatergoryDetails CatergoryDetails;
    private ArrayList<CatergoryDetails> allArray = new ArrayList<>();
    private  RecyclerView recyclerView;
    Context mcontext;


    @SuppressLint("InflateParams")
    @Override
    public void onCreateView(@NonNull LayoutInflater inflater) {
      //  frameLayout.addView(inflater.inflate(R.layout.rv_genremovie_item, null));
        frameLayout.addView(inflater.inflate(R.layout.categroy_list_main, null));
        genreTheCity=new GenreTheCity();
        mcontext = LuancherActivity.ncontext;


          setTitle(getResources().getString(R.string.boxs));
        progressBar = findViewById(R.id.progressBar);
        hash = new ArrayList<>();
        check = findViewById(R.id.check);
        net = findViewById(R.id.error_net);
         recyclerView = findViewById(R.id.recycler_view);



        RequestConfiguration configuration = new RequestConfiguration.Builder().build();
        MobileAds.setRequestConfiguration(configuration);
        MobileAds.initialize(getActivity(), new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {



            }
        });
//          RequestConfiguration configuration = new RequestConfiguration.Builder().setTestDeviceIds(Arrays.asList("6EDD29BEABA074DDFB1F1015A5AD78E0")).build();
//        MobileAds.setRequestConfiguration(configuration);
        String adunitI = getString(R.string.Interstitial_ad_unit_idm);



        getCategory();

        check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                net.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
                getCategory();
                loadA(getActivity(),adunitI);
            }
        });

        refreshLayout = findViewById(R.id.pull);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (isLoading) {
                    return;
                }
                loadA(getActivity(),adunitI);
                getCategory();
            }
        });
    }

    private InterstitialAd interstitialA;

    private static final String TAG = "MCategory";

    private void loadA(Activity activit, String AD_UNIT_ID ) {


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
    private void getCategory() {
        if (isLoading) {
            return;
        }
        hash.clear();

        isLoading = true;
        new ConnectionManager.Builder()
                .setURL(ApiSeivice.getCategoryIndex())
                .request(new ConnectionManager.ResultConnection() {
                    @Override
                    public void onSuccess(JSONArray jsonArray) throws JSONException {
                        if (jsonArray.length() == 0) {
                            ((ImageView) findViewById(R.id.image)).setImageResource(R.drawable.sad);
                            net.setVisibility(View.VISIBLE);
                            check.setText(getResources().getString(R.string.empty));
                            check.setOnClickListener(null);
                            check.setClickable(false);
                        } else {
                            allArray.clear();
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject object = jsonArray.getJSONObject(i);
                                HashMap<String, Object> add = new HashMap<>();
                                add.put("id", object.getString("id"));
                                add.put("slug", object.getString("slug"));
                                add.put("title", object.getString("name"));
                                add.put("count", object.getString("count"));
                                String id =object.getString("id");
                                String slug = object.getString("slug");
                                String title =object.getString("name");
                                String count =object.getString("count");
                                CatergoryDetails = new CatergoryDetails(id,slug,title,count);

                                allArray.add(CatergoryDetails);


                                hash.add(add);

                            }
                            if (allArray.isEmpty()) {
                                ((ImageView) Objects.requireNonNull(findViewById(R.id.image))).setImageResource(R.drawable.sad);
                                net.setVisibility(View.VISIBLE);
                                check.setText(getResources().getString(R.string.empty));
                                check.setOnClickListener(null);
                                check.setClickable(false);
                            } else {
                            SaveModel saveModel = new SaveModel();
                              saveModel.allArray.addAll(allArray);
                               Application.easySave.saveModel("Category", saveModel);
                               TinyData.getInstance().putStringCashe("Category");
                                categoryAdaper = new CategoryAdaper(allArray,mcontext);

                                recyclerView.setLayoutManager(new LinearLayoutManager(mcontext, LinearLayoutManager.VERTICAL, false));
                                recyclerView.setAdapter(categoryAdaper);
                                categoryAdaper.notifyDataSetChanged();
                            }
                        }
                        progressBar.setVisibility(View.GONE);
                        if (refreshLayout.isRefreshing()) {
                            refreshLayout.setRefreshing(false);
                        }
                        isLoading = false;
                    }

                    @Override
                    public void onFail(String error) {
                        if (CheckNetworkStatus.isOnline())
                            getCategory();
                        else {
                            progressBar.setVisibility(View.GONE);
                            net.setVisibility(View.VISIBLE);
                            if (refreshLayout.isRefreshing()) {
                                refreshLayout.setRefreshing(false);
                            }
                        }
                        isLoading = false;
                    }
                });
    }


//    private void getCategoryItems(String id, Category.CategoryAdaper.Holder holder) {
////            if (isLoading) {
////                return;
////            }
////            isLoading = true;
//        ArrayList<GenreThe> allTheCity = new ArrayList<>();
//        final ArrayList<GenreThe>[] allTheCi = new ArrayList[]{new ArrayList<>()};
//        new ConnectionManager.Builder()
//                .setURL(ApiSeivice.getCategoryPosts(id, String.valueOf(pagee)))
//                .request(new ConnectionManager.ResultConnection() {
//                    @Override
//                    public void onSuccess(JSONArray jsonArray) throws JSONException {
//                        for (int i = 0; i < jsonArray.length(); i++) {
//                            GenreThe TheCity = new GenreThe();
//                            JSONObject object = jsonArray.getJSONObject(i);
//
//                            HashMap<String, Object> add = new HashMap<>();
//                            add.put("id", object.getString("id"));
//                            add.put("categories", object.getJSONArray("categories").toString());
//                            add.put("url", object.getString("link"));
//                            add.put("title", object.getJSONObject("title").getString("rendered"));
//                            add.put("content", object.getJSONObject("content").getString("rendered"));
//                            String excerpt = String.valueOf(Html.fromHtml(object.getJSONObject("excerpt").getString("rendered")));
//                            add.put("excerpt", excerpt);
//                            add.put("date", object.getString("date").replace("T", " "));
//                            add.put("author", object.getString("author"));
//                            if (object.has("yoast_head_json") && object.getJSONObject("yoast_head_json").has("og_image")) {
//                                JSONArray array = object.getJSONObject("yoast_head_json").getJSONArray("og_image");
//                                add.put("url_image", array.getJSONObject(array.length() - 1).getString("url"));
//                            } else if (object.has("yoast_head")) {
//                                String head = object.getString("yoast_head");
//                                Pattern pattern = Pattern.compile("property=\"og:image\" content=\"(.*)\"");
//                                Matcher matcher = pattern.matcher(head);
//                                if (matcher.find()) {
//                                    head = matcher.group();
//                                    add.put("url_image", head.substring(29, head.length() - 1));
//                                } else {
//                                    add.put("url_image", "");
//                                }
//                            } else {
//                                add.put("url_image", "");
//                            }
//                            TheCity.setCatergories(add);
//                            allTheCity.add(TheCity);
//
//                            //  mAdaptere.getDate().add(add);
//                            //allTheCit[0] = allTheCity;
//
//                        }
//                        LinearLayoutManager genrelayour = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
//                        holder.genreMovieRecyclerView.setLayoutManager(genrelayour);
//                        holder.genreMovieRecyclerView.addItemDecoration(new SpacesItemDecoration(getActivity().getResources().getDimensionPixelSize(R.dimen.spacings)));
//                        GenrelistRecyclerViewAdapter   mAdapterd = new GenrelistRecyclerViewAdapter(getContext(),allTheCity);
//
//
//                        holder.genreMovieRecyclerView.setAdapter(mAdapterd);
//                        Toast.makeText(getContext(), String.valueOf(allTheCity.size()), Toast.LENGTH_SHORT).show();
//
//                        mAdapterd.notifyDataSetChanged();
//
//
//
//                        mAdapterd.setOnClickListener(new OnClickListener() {
//                            @Override
//                            public void onClick(View view, int position) {
////                BaseFragment baseFragment = new PostView();
////                baseFragment.addDataArguments(mAdapter.getDate().get(position));
////                startFragment(baseFragment);
//                                Intent meadintent = new Intent(getContext(),PostView.class);
//                                meadintent.putExtra("postintent", (Bundle) mAdapterd.getDate().get(position));
//                                startActivity(meadintent);
//                            }
//                        });
//                      //  Toast.makeText(getContext(), String.valueOf(allTheCit[0].size()), Toast.LENGTH_SHORT).show();
//                       isLoading = false;
//                    }
//
//                    @Override
//                    public void onFail(String error) {
//                        if (CheckNetworkStatus.isOnline())
//                            getCategoryItems(id,holder);
//                        else if (allTheCity.isEmpty()) {
//                            net.setVisibility(View.VISIBLE);
//                            progressBar.setVisibility(View.GONE);
//                        } else {
//                            Toast.makeText(getContext(), "Network not available!", Toast.LENGTH_SHORT).show();
//                            pagee--;
//                            progressBar.setVisibility(View.GONE);
//                        }
////                            if (pullRefreshLayout.isRefreshing()) {
////                                pullRefreshLayout.setRefreshing(false);
////                            }
//                        isLoading = false;
//                    }
//                });
//
//
//
//    }

}

