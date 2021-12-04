/*
 * Copyright (C) 2019  All rights reserved for FaraSource (ABBAS GHASEMI)
 * https://farasource.com
 */
package mobi.androapp.com.city.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import mobi.androapp.com.city.JsontoJave;
import mobi.androapp.com.city.R;
import mobi.androapp.com.city.builder.BuildApp;

import java.util.HashMap;
import java.util.List;

public class PostsAdapter extends RecyclerView.Adapter<Holder> {
    private List<HashMap<String, Object>> date;
    private OnClickListener onClickListener;

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public List<HashMap<String, Object>> getDate() {
        return date;
    }

    public void setDate(List<HashMap<String, Object>> date) {
        this.date = date;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view;
        switch (BuildApp.showType) {
            case 1:
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_main_1, viewGroup, false);
                break;
            case 2:
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_main_2, viewGroup, false);
                break;
            default:
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_main_3, viewGroup, false);
                break;
        }
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final Holder holder, final int position) {
        final HashMap<String, Object> hash_get = date.get(position);
        String titled = (String) JsontoJave.String(hash_get.get("title").toString());
        holder.titlePost.setText(titled);

        holder.authorPost.setText(String.format("Author: %s", BuildApp.authorName));

        String url_image = hash_get.get("url_image").toString();

        if (!url_image.equals("")) {
            holder.imgPost.setVisibility(View.VISIBLE);
            Glide.with(holder.card).load(url_image).into(holder.imgPost);
        } else
            holder.imgPost.setVisibility(View.VISIBLE);
        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickListener.onClick(v, position);
            }
        });

        if (BuildApp.showType == 1) {
            String s = hash_get.get("excerpt").toString();
            holder.contextPost.setText(s);
        }
        if (BuildApp.showType != 2) {
            holder.datePost.setText(hash_get.get("date").toString());
        }
    }

    @Override
    public int getItemCount() {
        return date.size();
    }
}
