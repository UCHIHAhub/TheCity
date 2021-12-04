package mobi.androapp.com.city.fragment;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import mobi.androapp.com.city.JsontoJave;
import mobi.androapp.com.city.R;
import mobi.androapp.com.city.adapter.OnClickListener;
import mobi.androapp.com.city.builder.BuildApp;
import mobi.androapp.com.city.builder.GenreThe;

import java.util.ArrayList;
import java.util.HashMap;

public class GenrelistRecyclerViewAdapter extends RecyclerView.Adapter<GenrelistRecyclerViewAdapter.MovieViewHolder> {
    private HashMap<String, Object> date;
    private OnClickListener onClickListener;
    ArrayList<GenreThe> catergories;
    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public HashMap<String, Object> getDate() {
        return date;
    }

    public void setDate(HashMap<String, Object> date) {
        this.date = date;
    }

    Context mcontext;

    public GenrelistRecyclerViewAdapter(Context mcontext, ArrayList<GenreThe> catergories) {

        this.catergories=catergories;
        this.mcontext = mcontext;


    }



    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(mcontext).inflate(R.layout.row_main_22, viewGroup, false);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieViewHolder holder, int position) {

        final HashMap<String, Object> hash_get = catergories.get(position).getCatergories();
        this.date = hash_get;
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
                Intent meadintent = new Intent(mcontext, PostView.class);
                meadintent.putExtra("postintent",  hash_get);
                mcontext.startActivity(meadintent);

               // onClickListener.onClick(v, position);
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
        if (catergories == null) return 0;

       else return catergories.size();
    }



    public class MovieViewHolder extends RecyclerView.ViewHolder  {
        ImageView imgPost;
        View card;
        TextView titlePost, contextPost, authorPost, datePost;

        MovieViewHolder(View itemView) {
            super(itemView);
            imgPost = itemView.findViewById(R.id.imgPost);
            titlePost = itemView.findViewById(R.id.titlePost);
            authorPost = itemView.findViewById(R.id.writerPost);
            if (BuildApp.showType == 1) {
                contextPost = itemView.findViewById(R.id.contextPost);
            }
            if (BuildApp.showType != 2) {
                datePost = itemView.findViewById(R.id.timePost);
                card = itemView.findViewById(R.id.card_view);
            } else {
                card = itemView;
            }

        }


    }
}
