package mobi.androapp.com.city.fragment;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.loopj.android.http.AsyncHttpResponseHandler;
import mobi.androapp.com.city.R;
import mobi.androapp.com.city.api.ApiSeivice;
import mobi.androapp.com.city.api.NetUtil;
import mobi.androapp.com.city.api.PConnectionManager;
import mobi.androapp.com.city.builder.BuildApp;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import cz.msebera.android.httpclient.Header;
import me.shaohui.bottomdialog.BaseBottomDialog;
import stream.customalert.CustomAlertDialogue;

public class CommentBottomDialog extends BaseBottomDialog  {
    private List<HashMap<String, Object>> hash;
    private boolean isLoading;
    private int page = 1;
    private boolean hasNext;
    private AdapterComments adapterComments;
    String post, authors;
    FragmentManager mfrag;
    Fragment coments;
    String titled;
    ArrayList<String> SlineHint = new ArrayList<>();
    ArrayList<String> SlineText = new ArrayList<>();
    CustomAlertDialogue.Builder alertzz ;
    public void setUIArguements(final Bundle args, FragmentManager  mfrag, Fragment coments) {
        post =args.getString("post");
        authors = args.getString("author");
        titled = args.getString("titled");

        mfrag =  mfrag;
        coments= coments;


    }
    public void setUIhide() {
        //this.setCancelable(true);
       this.dismiss();


    }
    @Override
    public int getLayoutRes() {
        return R.layout.fragment_comment;
    }
   /* private void createAndShowInputDialog(Context mcontext) {
        new MixDialog.Builder(mcontext)
                .setTitle("Basic Dialog")
                .setMessage("Hello from MixDialog!")
                .setPositiveButtonText("PositiveButton")
                .setNegativeButtonText("NegativeButton")
                .addInputItemGroup("InputGroup")
                .addInputItem("Name")
                .addInputItem("Email", "Enter Email")
                .buildWithParent()
                .setCancelable(false)
                .setOnDialogEventListener(new MixDialog.onDialogEventListener() {
                    @Override
                    public boolean onDialogButtonClick(MixDialog.ButtonType buttonType, MixDialog dialog) {
                        boolean retVal = true;
                        if(buttonType.equals(MixDialog.ButtonType.POSITIVE)) {
                            InputItem inputItem1 = dialog.getInputItem("InputGroup", "Name");
                            InputItem inputItem2 = dialog.getInputItem("InputGroup", "Email");
                            if (inputItem1 != null){
                                if(inputItem1.getValue().isEmpty()) {
                                    inputItem1.setError("Name cannot be empty!");
                                    retVal = false;
                                }
                                else{
                                    inputItem1.clearError();
                                }
                            }
                            if (inputItem2 != null){
                                if(inputItem2.getValue().isEmpty() || inputItem2.getValue().equals("Enter Email")) {
                                    inputItem2.setError("Email must be changed!");
                                    retVal = false;
                                }
                                else{
                                    inputItem2.clearError();
                                }
                            }
                        }
                        return retVal;
                    }
                })
                .build().show();
    }*/
    @Override
    public void bindView(View v) {
        RecyclerView recView = v.findViewById(R.id.recycler_comment);
        FloatingActionButton floatingActionButton = v.findViewById(R.id.addNewFab);
        alertzz = new CustomAlertDialogue.Builder(getContext());
         AdView  mAdView = v.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        v.findViewById(R.id.back_comment).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setUIhide();
             //   Comments.super.getActivity().getSupportFragmentManager().popBackStack();
            }
        });
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
          //      createAndShowInputDialog(getActivity().getApplicationContext());

             /*   SlineHint.add("Name");
                SlineHint.add("Email");
                SlineHint.add("Comment");
                SlineText.add(null);
                SlineText.add(null);
                SlineText.add(null);





                alertzz.setBackgroundColor(R.color.colorAccent);
                alertzz.setStyle(CustomAlertDialogue.Style.INPUT)
                        .setTitle("Add Comment")
                        .setMessage(titled)
                        .setPositiveText("Submit")
                        .setPositiveColor(R.color.positive)
                        .setPositiveTypeface(Typeface.DEFAULT_BOLD)
                        .setOnInputClicked(new CustomAlertDialogue.OnInputClicked() {
                            @Override
                            public void OnClick(View view, Dialog dialog, ArrayList<String> inputList) {
                                String name = inputList.get(0).trim();
                                String email = inputList.get(1).trim();
                                String comment = inputList.get(2).trim();
                                if (!name.isEmpty() &&!email.isEmpty()&&!comment.isEmpty()){
                                    submitComment(name,email,comment,post);

                                }else{
                                    Toast.makeText(getActivity(), "Fill all details", Toast.LENGTH_SHORT).show();
                                }

                                dialog.dismiss();
                            }
                        })
                        .setNegativeText("Cancel")
                        .setNegativeColor(R.color.negative)
                        .setOnNegativeClicked(new CustomAlertDialogue.OnNegativeClicked() {
                            @Override
                            public void OnClick(View view, Dialog dialog) {

                                SlineHint.clear();
                                SlineText.clear();
                                dialog.dismiss();
                            }
                        })
                        .setLineInputHint(SlineHint)
                        .setLineInputText(SlineText)
                        .setDecorView(requireActivity().getWindow().getDecorView())
                        .build();
                alertzz.show();*/
            }
        });
        hash = new ArrayList<>();
        adapterComments = new AdapterComments();
        recView.setLayoutManager(new LinearLayoutManager(getContext()));
        recView.setAdapter(adapterComments);
        getComments(post,authors,v);
    }
    private void getComments(String post,String authors,View v) {
        v.findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
        if (isLoading) {
            return;
        }
        isLoading = true;
        new PConnectionManager.Builder()
                .setURL(ApiSeivice.getComments(post, String.valueOf(page)))
                .request(new PConnectionManager.ResultConnection() {
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
    public void submitComment(String name, String email,String content,String postId) {


        String url = "respond/submit_comment/?post_id=" + postId + "&name=" + name + "&email=" + email + "&content=" + content;

     //   String url= "comments?" +"author_name="+name+"&author_email="+email+"&author_name="+name+"&content="+content+"&post="+postId;



        NetUtil.get(url, null, new AsyncHttpResponseHandler() {


                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {


                    Toast.makeText(getActivity(),getResources().getString(R.string.comment_submit_success),Toast.LENGTH_LONG).show();
              SlineHint.clear();
                     SlineText.clear();

                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    Toast.makeText(getActivity(),getResources().getString(R.string.comment_status_close_error),Toast.LENGTH_LONG).show();

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
