package io.smileyjoe.putio.tv.tmdb;

import android.content.Context;
import android.util.Log;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.net.URLEncoder;

import io.smileyjoe.putio.tv.BuildConfig;
import io.smileyjoe.putio.tv.putio.Response;

public class Tmdb {

    private static String BASE = "https://api.themoviedb.org";
    private static String BASE_IMAGE = "https://image.tmdb.org/t/p/w500";
    private static String SEARCH = "/3/search";
    private static String MOVIE = "/movie";
    private static String PARAM_API_KEY = "api_key";
    private static String PARAM_SEARCH = "query";
    private static String PARAM_YEAR = "primary_release_year";

    private static String getUrl(String... paths){
        String url = BASE;

        for(String path:paths){
            url += path;
        }

        url += "?" + PARAM_API_KEY + "=" + BuildConfig.TMDB_AUTH_TOKEN;

        return url;
    }

    private static String addParam(String url, String key, String value){
        return url + "&" + key + "=" + URLEncoder.encode(value);
    }

    public static String getImageUrl(String url){
        return BASE_IMAGE + url;
    }

    public static void searchMovie(Context context, String title, String year, Response response){

        String url = getUrl(SEARCH, MOVIE);
        url = addParam(url, PARAM_SEARCH, title);
        url = addParam(url, PARAM_YEAR, year);

        Ion.with(context)
                .load(url)
                .asJsonObject()
                .setCallback(response);
    }

}
