package mobi.androapp.com.city.api;


import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.IOException;

public class PostCommenta extends AsyncTask {

    private String URL_WORDPRESS;
    private String id;
    private String name;
    private String email;
    private String url;
    private String comment;
    private String commentParent;

    private ProgressDialog progressDialog;
    private String progressDialogTitle;
    private String progressDialogMessage;
    private Context context;
    private OnComplete onComplete;
    private Boolean showDialog = false;
    private int response;

    public PostCommenta(String id, String URL_WORDPRESS, String name, String email, String url, String comment, String commentParent) {
        this.id = id;
        this.URL_WORDPRESS = URL_WORDPRESS;
        this.name = name;
        this.email = email;
        this.url = url;
        this.comment = comment;
        this.commentParent = commentParent;
    }

    public void showDialog(Boolean showDialog, String progressDialogTitle, String progressDialogMessage, Context context) {
        this.showDialog = showDialog;
        this.progressDialogTitle = progressDialogTitle;
        this.progressDialogMessage = progressDialogMessage;
        this.context = context;
    }

    public void onPauseDialog() {
        if ((progressDialog != null) && progressDialog.isShowing())
            progressDialog.dismiss();
        progressDialog = null;
    }

    public interface OnComplete {
        public void onFinish();
        public void onError();
    }

    public void onFinish (OnComplete onComplete) {
        this.onComplete = onComplete;
    }

    @Override
    protected Object doInBackground(Object[] params) {
        String urla = URL_WORDPRESS + "comments?" +"author_name="+name+"&author_email="+email+"&author_name="+name+"&content="+comment+"&post="+id;

                Connection connection  = Jsoup.connect(URL_WORDPRESS + "comments?");
        // return api + "comments?" +"author_name="+name+"&author_email="+email+"&author_name="+name+"&content="+coment+"&post="+id;

        connection.method(Connection.Method.POST);
        connection.data("author", name);
        connection.data("author_name", name);
        connection.data("author_email", email);
        connection.data("author_url", url);
        connection.data("content", comment);
        connection.data("post", id);
        connection.data("comment_parent", commentParent);
        try {
            response = connection.execute().statusCode();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (showDialog) {
            progressDialog = new ProgressDialog(context);
            progressDialog.setTitle(progressDialogTitle);
            progressDialog.setMessage(progressDialogMessage);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
        if (showDialog) {
            if ((progressDialog != null) && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        }
        if (response != 200) {
            onComplete.onError();
        } else {
            onComplete.onFinish();
        }
    }
}
