/*
 * Copyright (C) 2019  All rights reserved for FaraSource (ABBAS GHASEMI)
 * https://farasource.com
  */
package mobi.androapp.com.city.fragment;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import mobi.androapp.com.city.R;
import mobi.androapp.com.city.adapter.OnClickListener;
import mobi.androapp.com.city.adapter.PostsAdapter;
import mobi.androapp.com.city.builder.Application;
import mobi.androapp.com.city.builder.BuildApp;
import mobi.androapp.com.city.builder.SaveModel;

import java.util.ArrayList;
import java.util.HashMap;

import static mobi.androapp.com.city.builder.BuildApp.divider;

public class BookMark extends BaseFragment {

    private PostsAdapter postsAdapter;
    private LinearLayout errorNet;

    @Override
    public void onCreateView(@NonNull LayoutInflater inflater) {
        frameLayout.addView(inflater.inflate(R.layout.fragment_list_main, null));
        setTitle(getResources().getString(R.string.mark_name));
        findViewById(R.id.progressBar).setVisibility(View.GONE);
        SwipeRefreshLayout pullRefreshLayout = findViewById(R.id.pull);
        errorNet = findViewById(R.id.error_net);
        ((ImageView)findViewById(R.id.image)).setImageResource(R.drawable.sad);
        ((TextView)findViewById(R.id.check)).setText(getResources().getString(R.string.empty));
        pullRefreshLayout.setEnabled(false);
        pullRefreshLayout.setRefreshing(false);
        postsAdapter = new PostsAdapter();
        RecyclerView recView = findViewById(R.id.recycler_view);
        recView.setLayoutManager(new StaggeredGridLayoutManager(BuildApp.showType == 3 ? BuildApp.getCountPx() : 1, StaggeredGridLayoutManager.VERTICAL));
        postsAdapter.setDate(new ArrayList<HashMap<String, Object>>());
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
        recView.setAdapter(postsAdapter);
        if (divider) {
            DividerItemDecoration itemDecor = new DividerItemDecoration(getContext(), 1);
            recView.addItemDecoration(itemDecor);
        }
    }

    @Override
    public void onResumeFragment() {
        postsAdapter.getDate().clear();
        SaveModel saveModel = Application.easySave.retrieveModel("book_mark_info", SaveModel.class);
        if (saveModel != null) {
            errorNet.setVisibility(View.GONE);
            postsAdapter.getDate().addAll(saveModel.hashMapList);
        }else {
            errorNet.setVisibility(View.VISIBLE);
        }
        postsAdapter.notifyDataSetChanged();
    }
}
