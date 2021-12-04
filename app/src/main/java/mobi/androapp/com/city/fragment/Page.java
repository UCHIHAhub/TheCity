/*
 * Copyright (C) 2019  All rights reserved for FaraSource (ABBAS GHASEMI)
 * https://farasource.com
 */
package mobi.androapp.com.city.fragment;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.text.Html;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import mobi.androapp.com.city.R;
import mobi.androapp.com.city.adapter.OnClickListener;
import mobi.androapp.com.city.adapter.PostsAdapter;
import mobi.androapp.com.city.api.ApiSeivice;
import mobi.androapp.com.city.api.CheckNetworkStatus;
import mobi.androapp.com.city.api.ConnectionManager;
import mobi.androapp.com.city.builder.Application;
import mobi.androapp.com.city.builder.BuildApp;
import mobi.androapp.com.city.builder.SaveModel;
import mobi.androapp.com.city.builder.TinyData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static mobi.androapp.com.city.builder.BuildApp.divider;

public class Page extends BaseFragment {
    private PostsAdapter postsAdapter;
    private ProgressBar progress;
    private String status = "";
    private SwipeRefreshLayout pullRefreshLayout;
    private LinearLayout net;
    private boolean isLoading;

    @Override
    public void onCreateView(@NonNull BaseFragment baseFragment, int id) {
        super.onCreateView(baseFragment, R.layout.fragment_base_list);
        setTitle(getResources().getString(R.string.page_name));

        findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishFragment();
            }
        });

        pullRefreshLayout = findViewById(R.id.pull);
        RecyclerView recView = findViewById(R.id.recycler_view);
        TextView check = findViewById(R.id.check);
        net = findViewById(R.id.error_net);
        progress = findViewById(R.id.progressBar);
        postsAdapter = new PostsAdapter();
        postsAdapter.setDate(new ArrayList<>());
        postsAdapter.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view, int position) {
//                PostView baseFragment = new PostView();
//                baseFragment.addDataArguments(postsAdapter.getDate().get(position));
//                startFragment(baseFragment);
                Intent meadintent = new Intent(getContext(),PostView.class);
                meadintent.putExtra("postintent",postsAdapter.getDate().get(position));
                startActivity(meadintent);
            }
        });
        recView.setLayoutManager(new StaggeredGridLayoutManager(BuildApp.showType == 3 ? BuildApp.getCountPx() : 1, StaggeredGridLayoutManager.VERTICAL));
        recView.setAdapter(postsAdapter);
        if (divider) {
            DividerItemDecoration itemDecor = new DividerItemDecoration(getContext(), 1);
            recView.addItemDecoration(itemDecor);
        }

        SaveModel saveModel = Application.easySave.retrieveModel("Page", SaveModel.class);
        if (saveModel != null) {
            postsAdapter.getDate().addAll(saveModel.hashMapList);
            progress.setVisibility(View.GONE);
        } else {
            getPage();
        }

        pullRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (isLoading) {
                    return;
                }
                Application.easySave.saveModel("Page", null);
                postsAdapter.getDate().clear();
                postsAdapter.notifyDataSetChanged();
                progress.setVisibility(View.VISIBLE);
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        pullRefreshLayout.setRefreshing(false);
                    }
                }, 500);
                getPage();
            }
        });
        check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                net.setVisibility(View.GONE);
                progress.setVisibility(View.VISIBLE);
                getPage();
            }
        });
    }


    private void getPage() {
        if (isLoading) {
            return;
        }
        isLoading = true;
        new ConnectionManager.Builder()
                .setURL(ApiSeivice.getPageIndex())
                .request(new ConnectionManager.ResultConnection() {
                    @Override
                    public void onSuccess(JSONArray jsonArray) throws JSONException {
                        if (jsonArray.length() > 0) {
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject object = jsonArray.getJSONObject(i);
                                HashMap<String, Object> add = new HashMap<>();
                                add.put("id", object.getString("id"));
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
                            SaveModel saveModel = new SaveModel();
                            saveModel.hashMapList.addAll(postsAdapter.getDate());
                            Application.easySave.saveModel("Page", saveModel);
                            postsAdapter.notifyDataSetChanged();
                            TinyData.getInstance().putStringCashe("Page");
                        } else {
                            Toast.makeText(getContext(), "There is no item!", Toast.LENGTH_LONG).show();
                            finishFragment();
                        }
                        progress.setVisibility(View.GONE);
                        if (pullRefreshLayout.isRefreshing()) {
                            pullRefreshLayout.setRefreshing(false);
                        }
                        isLoading = false;
                    }

                    @Override
                    public void onFail(String error) {
                        isLoading = false;
                        if (CheckNetworkStatus.isOnline())
                            getPage();
                        else {
                            progress.setVisibility(View.GONE);
                            net.setVisibility(View.VISIBLE);
                        }
                    }
                });
    }
}