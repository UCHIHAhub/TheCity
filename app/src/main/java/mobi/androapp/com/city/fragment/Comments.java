/*
 * Copyright (C) 2019  All rights reserved for FaraSource (ABBAS GHASEMI)
 * https://farasource.com
 */
package mobi.androapp.com.city.fragment;

import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import mobi.androapp.com.city.R;
import mobi.androapp.com.city.api.ApiSeivice;
import mobi.androapp.com.city.api.ConnectionManager;
import mobi.androapp.com.city.builder.BuildApp;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Comments extends Fragment {
    private List<HashMap<String, Object>> hash;
    private boolean isLoading;
    private int page = 1;
    private boolean hasNext;
    private AdapterComments adapterComments;
    String post, authors;
    FragmentManager mfrag;
    Fragment coments;
    public void setUIArguements(final Bundle args,FragmentManager  mfrag,Fragment coments) {
        post =args.getString("post");
        authors = args.getString("author");
          mfrag =  mfrag;
        coments= coments;


    }
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_comment, container, false);

        RecyclerView recView = v.findViewById(R.id.recycler_comment);

        v.findViewById(R.id.back_comment).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            Comments.super.getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        hash = new ArrayList<>();
        adapterComments = new AdapterComments();
        recView.setLayoutManager(new LinearLayoutManager(getContext()));
        recView.setAdapter(adapterComments);
        getComments(post,authors,v);
        return v;
    }

    private void getComments(String post,String authors,View v) {
        v.findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
        if (isLoading) {
            return;
        }
        isLoading = true;
        new ConnectionManager.Builder()
                .setURL(ApiSeivice.getComments(post, String.valueOf(page)))
                .request(new ConnectionManager.ResultConnection() {
                    @Override
                    public void onSuccess(JSONArray jsonArray) throws JSONException {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject object = jsonArray.getJSONObject(i);
                            HashMap<String, Object> add = new HashMap<>();
                            add.put("name", object.getString("author_name"));
                            add.put("date", object.getString("date").replace("T", " "));
                            add.put("content", Html.fromHtml(object.getJSONObject("content").getString("rendered")));
                            boolean author = object.getString("author").equals(authors);
                            add.put("type", author ? 1 : 0);
                            hash.add(add);
                        }
                        hasNext = jsonArray.length() == BuildApp.limitPage;
                        isLoading = false;
                        adapterComments.notifyDataSetChanged();
                        v.findViewById(R.id.progressBar).setVisibility(View.GONE);
                        if (jsonArray.length() == 0) {
                            v.findViewById(R.id.no_comment).setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onFail(String error) {
                        v.findViewById(R.id.progressBar).setVisibility(View.GONE);
                        isLoading = false;
                       // v.finishFragment();
                        Toast.makeText(getContext(), "Network not available!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    class AdapterComments extends RecyclerView.Adapter<AdapterComments.contentViewHolder> {

        @NonNull
        @Override
        public contentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(viewType == 0 ?
                    R.layout.row_comment_user :
                    R.layout.row_comment_admin, parent, false);
            return new contentViewHolder(view);
        }

        @Override
        public void onBindViewHolder(contentViewHolder holder, int position) {
            final HashMap<String, Object> hash_get = hash.get(position);
            String c = hash_get.get("name").toString() + " says: \n" + hash_get.get("content").toString().trim();
            holder.msg.setText(c);
            holder.date.setText(hash_get.get("date").toString());
        }

        @Override
        public int getItemCount() {
            return hash.size();
        }

        @Override
        public int getItemViewType(int position) {
            return (int) hash.get(position).get("type");
        }

        class contentViewHolder extends RecyclerView.ViewHolder {
            TextView msg, date;

            contentViewHolder(View itemView) {
                super(itemView);
                msg = itemView.findViewById(R.id.msg);
                date = itemView.findViewById(R.id.date);
            }
        }
    }
}