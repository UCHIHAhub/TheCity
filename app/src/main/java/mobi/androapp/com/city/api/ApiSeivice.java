/*
 * Copyright (C) 2019  All rights reserved for FaraSource (ABBAS GHASEMI)
 * https://farasource.com
 */
package mobi.androapp.com.city.api;


import mobi.androapp.com.city.BuildConfig;
import mobi.androapp.com.city.builder.BuildApp;

public class ApiSeivice {

    private final static String api;

    private final static String apia;


    static {
        if (BuildConfig.API.endsWith("/")) {
            api = "https://thecity.com.ng/" + "wp-json/wp/v2/";
            apia = "https://thecity.com.ng/" + "api/";
        } else {
           apia = BuildConfig.API + "/api/";
           api = BuildConfig.API + "/wp-json/wp/v2/";
        }
    //    api = "https://thecity.com.ng/" + "wp-json/wp/v2/";
    //    apia = "https://thecity.com.ng/" + "api/";
    }

    public static String getCategoryPosts(String id, String page) {
        return api + "posts?" + "categories=" + id + "&per_page=" + BuildApp.limitPage + "&page=" + page;
    }
    public static String getCategoryPost(String id) {
        return api + "posts?" + "categories=" + id ;
    }
    public static String getCategoryIndex() {
        return api + "categories?per_page=100";
    }

    public static String getPageIndex() {
        return api + "pages?per_page=100";
    }

    public static String getSearchResults(String search, String page) {
        return api + "search?" + "per_page=" + BuildApp.limitPage + "&search=" + search + "&page=" + page;
    }

    public static String getPost(String post) {
        return api + "posts/" + post;
    }
    public static String getApi() {
        return apia ;
    }

    public static String getPosts(String page) {
        return api + "posts?" + "per_page=" + BuildApp.limitPage + "&page=" + page;
    }

    public static String postComments(String id, String name,String email,String coment) {
        return api + "comments?" +"author_name="+name+"&author_email="+email+"&author_name="+name+"&content="+coment+"&post="+id;
     //  return "https://public-api.wordpress.com/rest/v1.1/sites/"+api+"/posts/"+id+"/replies/new";
    }
    public static String getComments(String id, String page) {
        return api + "comments?" + "post=" + id + "&per_page=" + /*BuildApp.LimitPage*/ 100 + "&page=" + page;
    }
}