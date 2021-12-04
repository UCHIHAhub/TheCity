package mobi.androapp.com.city.builder;


import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import mobi.androapp.com.city.R;
import mobi.androapp.com.city.api.ApiSeivice;
import mobi.androapp.com.city.api.ConnectionManager;
import mobi.androapp.com.city.fragment.GenrelistRecyclerViewAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CategoryAdaper extends RecyclerView.Adapter<CategoryAdaper.Holder> {

    private int pagee = 1;
    ArrayList<CatergoryDetails> fetchArray;
    Context mcontext;

    LinearLayoutManager genrelayour;
    public CategoryAdaper(ArrayList<CatergoryDetails> allArray, Context mcontexta) {

        fetchArray =allArray;
        mcontext = mcontexta;


    }
    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new Holder(LayoutInflater.from(mcontext).inflate(R.layout.rv_genremovie_item, null));
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, final int i) {
        holder.title.setText(fetchArray.get(i).getTitle());




        if (!fetchArray.isEmpty()){
            for (int m =0;m<fetchArray.size();m++){
                String id = fetchArray.get(i).getId();
                ArrayList<GenreThe> allTheCity = new ArrayList<>();
                //  getCategoryItems(fetchArray.get(i).getId(),holder);


                new ConnectionManager.Builder()
                        .setURL(ApiSeivice.getCategoryPost(id))
                        .request(new ConnectionManager.ResultConnection() {
                            @Override
                            public void onSuccess(JSONArray jsonArray) throws JSONException {
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    GenreThe TheCity = new GenreThe();
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
                                    TheCity.setCatergories(add);
                                    allTheCity.add(TheCity);

                                    //  mAdaptere.getDate().add(add);
                                    //allTheCit[0] = allTheCity;

                                }

                            holder.genreMovieRecyclerView.setLayoutManager(new LinearLayoutManager(mcontext.getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));
                        //   holder.genreMovieRecyclerView.addItemDecoration(new SpacesItemDecoration(8));

                                GenrelistRecyclerViewAdapter mAdapterd = new GenrelistRecyclerViewAdapter(mcontext,allTheCity);

                                holder.genreMovieRecyclerView.setAdapter(mAdapterd);


                                mAdapterd.notifyDataSetChanged();



//                                mAdapterd.setOnClickListener(new OnClickListener() {
//                                    @Override
//                                    public void onClick(View view, int position) {
////                BaseFragment baseFragment = new PostView();
////                baseFragment.addDataArguments(mAdapter.getDate().get(position));
////                startFragment(baseFragment);
//                                        Intent meadintent = new Intent(mcontext, PostView.class);
//                                        meadintent.putExtra("postintent", (Bundle) mAdapterd.getDate().get(position));
//                                        mcontext.startActivity(meadintent);
//                                    }
//                                });
                                //  Toast.makeText(mcontext, String.valueOf(allTheCit[0].size()), Toast.LENGTH_SHORT).show();
                              //  isLoading = false;
                            }

                            @Override
                            public void onFail(String error) {
                                Toast.makeText(mcontext, "Network not available!", Toast.LENGTH_SHORT).show();
                              //  pagee--;
//                                if (CheckNetworkStatus.isOnline())
//                                   // getCategoryItems(id,holder);
//                                else if (allTheCity.isEmpty()) {
//                               //     net.setVisibility(View.VISIBLE);
//                               //     progressBar.setVisibility(View.GONE);
//                                } else {
//                                    Toast.makeText(mcontext, "Network not available!", Toast.LENGTH_SHORT).show();
//                                    pagee--;
//                             //       progressBar.setVisibility(View.GONE);
//                                }
////                            if (pullRefreshLayout.isRefreshing()) {
////                                pullRefreshLayout.setRefreshing(false);
////                            }
                             //   isLoading = false;
                            }
                        });

            }
        }
//            final SaveModel saveModel = Application.easySave.retrieveModel("ListCategory_" + hash.get(i).get("id").toString(), SaveModel.class);
//            if (saveModel != null) {
//                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        mAdapter.getDate().addAll(saveModel.hashMapList);
//                        progressBar.setVisibility(View.GONE);
//                        mAdapter.notifyDataSetChanged();
//                    }
//                }, 500);
//            } else {
//                getCategoryy( mAdapter,hash.get(i).get("id").toString());
//            }


//            holder.genreMovieRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
//                @Override
//                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//                    if (dy > 0) {
//                        int visibleItemCount_posts = recyclerView.getChildCount();
//                        int totalItemCount_posts = genrelayour.getItemCount();
//                        int[] pastVisiblesItems_posts = genrelayour.findFirstVisibleItemPositions(new int[genrelayour.getSpanCount()]);
//                        if (!isLoading) {
//                            if ((visibleItemCount_posts + pastVisiblesItems_posts[0]) >= totalItemCount_posts) {
//                                if (hasNexte) {
//                                    pagee++;
//                                    progressBar.setVisibility(View.VISIBLE);
//                                    getCategory();
//                                }
//                            }
//                        }
//                    }
//                }
//            });
//            holder.title.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    ShowCategory baseFragment = new ShowCategory();
//                    baseFragment.addDataArguments(hash.get(i));
//                    Bundle bundle = new Bundle();
//                    bundle.putString("id", hash.get(i).get("id").toString());
//                    baseFragment.setArguments(bundle);
//                    startFragment(baseFragment);
//                }
//            });
//

    }

    @Override
    public int getItemCount() {
        return fetchArray.size();
    }

    class Holder extends RecyclerView.ViewHolder {
        TextView title;

        private  GenreMovieRecyclerView genreMovieRecyclerView;
        RecyclerView recycler;


        Holder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.genre_type);
            genreMovieRecyclerView = itemView.findViewById(R.id.rv_movies);
        }
    }
}
