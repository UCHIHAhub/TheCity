/*
 * Copyright (C) 2019  All rights reserved for FaraSource (ABBAS GHASEMI)
 * https://farasource.com
 */
package mobi.androapp.com.city.builder;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.util.DisplayMetrics;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;

import mobi.androapp.com.city.LuancherActivity;
import mobi.androapp.com.city.R;
import mobi.androapp.com.city.customtabs.CustomTabsIntent;

import java.util.HashMap;

public class BuildApp {

    public static String authorName = "Maazi";
    public static String mail = "admin@thecity.com.ng";
    public static String phone = "+2347080809395";
    public static String telegram = "+2347080809395"; // می تونی آدرس پیامرسان یا سایت های دیگری مثل آپارات ، سایت خودتون و ... را قرار دهی.
    public static String instagram = "https://api.whatsapp.com/send?phone=+2347080809395&text=Hello Admin";// می تونی آدرس پیامرسان یا سایت های دیگری مثل آپارات ، سایت خودتون و ... را قرار دهی
    public static String fontName = "sans";
    public static String download = "https://cafebazaar.ir/app/" + Application.context.getPackageName();
    public static String siteName = "Bazaar Cafe"; // متناسب با linkDownload تغییر نماید.
    public static int showType = 2; // 1 or 2 or 3
    public static boolean showSplash = true; // true or false
    public static int splashTime = 1; // Second ,example 1 - 4
    public static boolean doubleClickForExit = true; // true or false
    public static boolean divider = false; // true or false
    public static int limitPage = 10; // 10
    public static int timeout = 12; // second
    public static int timeDistoryCashe = 5; // 5 minute
    public static boolean enablePage = true;

    ///////// Don't Change /////////
    public static void Share() {
        try {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.putExtra(Intent.EXTRA_TEXT, "Application " + LuancherActivity.activity.getResources().getString(R.string.app_name) + "  " + BuildApp.siteName + " Download:\n" + BuildApp.download);
            intent.setType("text/*");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            LuancherActivity.activity.startActivity(Intent.createChooser(intent, "Select a program to send"));
        } catch (Exception e) {
            Log.e("rr", e.toString());
        }
    }

    public static void setViewSite(Activity activity, String url) {
        Uri uri = Uri.parse(url);
        CustomTabsIntent.Builder intentBuilder = new CustomTabsIntent.Builder();
        intentBuilder.setToolbarColor(activity.getTheme().getResources().getColor( R.color.colorPrimary));
        intentBuilder.setSecondaryToolbarColor(activity.getTheme().getResources().getColor(R.color.colorPrimaryDark));
        intentBuilder.setStartAnimations(activity, R.anim.fade_in, R.anim.fade_out);
        CustomTabsIntent customTabsIntent = intentBuilder.build();
        customTabsIntent.launchUrl(activity, uri);
    }

    public static void addBookMark(SaveModel saveModel) {
        SaveModel newSaveModel = Application.easySave.retrieveModel("book_mark_info", SaveModel.class);
        if (newSaveModel != null) {
            saveModel.hashMapList.addAll(newSaveModel.hashMapList);
        }
        Application.easySave.saveModel("book_mark_info", saveModel);
    }

    public static void removeBookMark(String id) {
        SaveModel saveModel = Application.easySave.retrieveModel("book_mark_info", SaveModel.class);
        if (saveModel != null) {
            SaveModel newSaveModel = new SaveModel();
            for (HashMap<String, Object> hashMap : saveModel.hashMapList) {
                if (!hashMap.get("id").equals(id)) {
                    newSaveModel.hashMapList.add(hashMap);
                }
            }
            Application.easySave.saveModel("book_mark_info", newSaveModel.hashMapList.isEmpty() ? null : newSaveModel);
        }
    }

    public static boolean hasID(String id) {
        SaveModel saveModel = Application.easySave.retrieveModel("book_mark_info", SaveModel.class);
        if (saveModel != null) {
            for (HashMap<String, Object> hashMap : saveModel.hashMapList) {
                if (hashMap.get("id").equals(id)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static int getCountPx() {
        DisplayMetrics displayMetrics = Application.context.getResources().getDisplayMetrics();
        return (int) (displayMetrics.widthPixels / displayMetrics.density / 140);
    }

    public static void setCustomDialog(AlertDialog alertDialog) {
        alertDialog.getWindow().setBackgroundDrawableResource(R.drawable.alert_dialog_bg);
    }
}