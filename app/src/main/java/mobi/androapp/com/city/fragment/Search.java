/*
 * Copyright (C) 2019  All rights reserved for FaraSource (ABBAS GHASEMI)
 * https://farasource.com
 */
package mobi.androapp.com.city.fragment;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import mobi.androapp.com.city.R;
import mobi.androapp.com.city.api.ApiSeivice;
import mobi.androapp.com.city.api.CheckNetworkStatus;
import mobi.androapp.com.city.api.ConnectionManager;
import mobi.androapp.com.city.api.ResultListener;
import mobi.androapp.com.city.builder.BuildApp;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static mobi.androapp.com.city.builder.BuildApp.divider;

public class Search extends BaseFragment implements ResultListener, ActivityResultCallback<ActivityResult> {

    public static int id;
    private ProgressBar progress;
    private EditText test;
    private SearchAdapter searchAdapter;
    private String searchText;
    private ImageView button;
    private boolean isLoading = true, hasNext;
    private LinearLayout error_net;
    private int page = 1;
    private List<HashMap<String, Object>> date = new ArrayList<>();
    private StaggeredGridLayoutManager gridLayoutManager;
    ActivityResultLauncher<Intent> resultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), this);

    @Override
    public void onCreateView(@NonNull LayoutInflater layoutInflater) {
        RelativeLayout view = (RelativeLayout) layoutInflater.inflate(R.layout.fragment_list_main, null);
        frameLayout.addView(view);
        progress = view.findViewById(R.id.progressBar);
        progress.setVisibility(View.GONE);
        error_net = view.findViewById(R.id.error_net);
//        ImageView image = findViewById(R.id.image);
//        TextView txt_error = findViewById(R.id.check);
        view.removeView(findViewById(R.id.title));
        layoutInflater.inflate(R.layout.fragment_search, view);
        test = view.findViewById(R.id.edit_text);
        SwipeRefreshLayout refreshLayout = view.findViewById(R.id.pull);
        refreshLayout.setEnabled(false);
        refreshLayout.setRefreshing(false);
        test.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (test.getText().toString().trim().isEmpty()) {
                    test.setText("");
                    return false;
                }
                search();
                try {
                    InputMethodManager imm = (InputMethodManager) test.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    assert imm != null;
                    imm.hideSoftInputFromWindow(test.getWindowToken(), 0);
                } catch (Exception e) {
                    //
                }
                return true;
            }
        });
        test.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (test.getText().toString().trim().isEmpty()) {
                    if (s.length() > 0) {
                        test.setText("");
                    }
                    button.setImageResource(R.drawable.ic_mic);
                    button.setColorFilter(getContext().getTheme().getResources().getColor(R.color.colorAccent));
                } else {
                    button.setImageResource(R.drawable.ic_close);
                    button.setColorFilter(getContext().getTheme().getResources().getColor(R.color.textColor));
                    searchText = String.valueOf(s);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        searchAdapter = new SearchAdapter();
        RecyclerView recView = findViewById(R.id.recycler_view);
        gridLayoutManager = new StaggeredGridLayoutManager(BuildApp.showType == 3 ? BuildApp.getCountPx() : 1, StaggeredGridLayoutManager.VERTICAL);
        recView.setLayoutManager(gridLayoutManager);
        recView.setAdapter(searchAdapter);
        recView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) {
                    int visibleItemCount_posts = gridLayoutManager.getChildCount();
                    int totalItemCount_posts = gridLayoutManager.getItemCount();
                    int[] pastVisiblesItems_posts = gridLayoutManager.findFirstVisibleItemPositions(new int[gridLayoutManager.getSpanCount()]);
                    if ((visibleItemCount_posts + pastVisiblesItems_posts[0]) >= totalItemCount_posts) {
                        if (hasNext) {
                            if (!isLoading) {
                                isLoading = true;
                                page++;
                                progress.setVisibility(View.VISIBLE);
                                request(searchText, String.valueOf(page), Search.this, id);
                            }
                        }
                    }
                }
            }
        });

        if (divider) {
            DividerItemDecoration itemDecor = new DividerItemDecoration(getContext(), 1);
            recView.addItemDecoration(itemDecor);
        }
        button = view.findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (test.getText().toString().isEmpty()) {
                    askSpeechInput();
                } else {
                    test.setText("");
                }
            }
        });
    }

    private void search() {
        id++;
        date.clear();
        page = 1;
        isLoading = true;
        error_net.setVisibility(View.GONE);
        progress.setVisibility(View.VISIBLE);
        request(searchText, String.valueOf(page), Search.this, id);
    }


    public void request(final String q, final String page, final ResultListener resultListener, final int id) {
        new ConnectionManager.Builder()
                .setURL(ApiSeivice.getSearchResults(q, page))
                .request(new ConnectionManager.ResultConnection() {
                    @Override
                    public void onSuccess(JSONArray jsonArray) throws JSONException {
                        if (jsonArray.length() > 0)
                            resultListener.onResult(true, "ok", jsonArray, id);
                        else
                            resultListener.onResult(false, "not found", null, id);
                    }

                    @Override
                    public void onFail(String error) {
                        if (!CheckNetworkStatus.isOnline()) {
                            resultListener.onResult(false, "Error Network", null, id);
                        } else {
                            if (id == Search.id)
                                request(q, page, resultListener, id);
                        }
                    }
                });
    }

    @Override
    public void onResult(boolean isSuccess, String msg, JSONArray jsonArray, int id) {
        if (id == Search.id) {
            if (isSuccess) {
                hasNext = jsonArray.length() == BuildApp.limitPage;
                for (int i = 0; i < jsonArray.length(); i++) {
                    try {
                        JSONObject object = jsonArray.getJSONObject(i);
                        HashMap<String, Object> add = new HashMap<>();
                        add.put("id", object.getString("id"));
                        add.put("url", object.getString("url"));
                        add.put("title", object.getString("title"));
                        date.add(add);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                searchAdapter.notifyDataSetChanged();
                if (jsonArray.length() == 0) {
                    ((ImageView) findViewById(R.id.image)).setImageResource(R.drawable.sad);
                    ((TextView) findViewById(R.id.check)).setText(getResources().getString(R.string.not_found));
                    error_net.setVisibility(View.VISIBLE);
                    Toast.makeText(getContext(), "Nothing found.", Toast.LENGTH_SHORT).show();
                }
            } else {
                page--;
                switch (msg) {
                    case "Error Network":
                        ((ImageView) findViewById(R.id.image)).setImageResource(R.drawable.without_internet);
                        ((TextView) findViewById(R.id.check)).setText("Network not available");
                        error_net.setVisibility(View.VISIBLE);
                        Toast.makeText(getContext(), "Network not available!", Toast.LENGTH_SHORT).show();
                        break;
                    case "not found":
                        ((ImageView) findViewById(R.id.image)).setImageResource(R.drawable.sad);
                        ((TextView) findViewById(R.id.check)).setText("Unspecified error");
                        error_net.setVisibility(View.VISIBLE);
                        Toast.makeText(getContext(), "Unspecified error", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
            isLoading = false;
            progress.setVisibility(View.GONE);
        }
    }

    private void askSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "what are you looking for?");
        try {
            resultLauncher.launch(intent);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getContext(), "Sorry, not supported on your device.", Toast.LENGTH_LONG).show();
        }
    }


    @Override
    public void onActivityResult(ActivityResult activityResult) {
        if (activityResult.getResultCode() == Activity.RESULT_OK) {
            Intent data = activityResult.getData();
            ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (result != null && result.size() > 0 && !result.get(0).equals("")) {
                test.setText(result.get(0));
                search();
            }
        }
    }

    public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.Holder> {

        private boolean isLoading;

        @NonNull
        @Override
        public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new Holder(LayoutInflater.from(getContext()).inflate(R.layout.row_category, null));
        }

        @Override
        public void onBindViewHolder(@NonNull Holder holder, int position) {
            final HashMap<String, Object> hash_get = date.get(position);
            holder.title.setText(hash_get.get("title").toString());
            holder.title.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isLoading) {
                        return;
                    }
                    isLoading = true;
                    new ConnectionManager.Builder()
                            .setURL(ApiSeivice.getPost(date.get(position).get("id").toString()))
                            .request(new ConnectionManager.ResultConnection() {
                                @Override
                                public void onSuccess(JSONArray jsonArray) throws JSONException {
                                  //  PostView baseFragment = new PostView();
                                    JSONObject object = jsonArray.getJSONObject(0);
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
                                 //   baseFragment.addDataArguments(add);
                                    //startFragment(baseFragment);
                                    isLoading = false;
                                    Intent meadintent = new Intent(getContext(),PostView.class);
                                    meadintent.putExtra("postintent",add);
                                    startActivity(meadintent);

                                }

                                @Override
                                public void onFail(String error) {
                                    isLoading = false;
                                    Toast.makeText(getContext(), "Could not connect to server.", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            });
        }

        @Override
        public int getItemCount() {
            return date.size();
        }

        public class Holder extends RecyclerView.ViewHolder {
            TextView title;

            Holder(@NonNull View itemView) {
                super(itemView);
                title = itemView.findViewById(R.id.title);
            }
        }
    }
}
