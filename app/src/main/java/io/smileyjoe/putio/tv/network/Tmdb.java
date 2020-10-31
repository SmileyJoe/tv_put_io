package io.smileyjoe.putio.tv.network;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.JsonObject;
import com.koushikdutta.ion.Ion;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import io.smileyjoe.putio.tv.BuildConfig;
import io.smileyjoe.putio.tv.db.AppDatabase;
import io.smileyjoe.putio.tv.object.Genre;

public class Tmdb {

    private static String BASE = "https://api.themoviedb.org/3";
    private static String BASE_IMAGE = "https://image.tmdb.org/t/p/original";
    private static String SEARCH = "/search";
    private static String MOVIE = "/movie";
    private static String LIST = "/list";
    private static String GENRE = "/genre";
    private static String PARAM_API_KEY = "api_key";
    private static String PARAM_SEARCH = "query";
    private static String PARAM_YEAR = "primary_release_year";

    private static String getUrl(String... paths) {
        String url = BASE;

        for (String path : paths) {
            url += path;
        }

        url += "?" + PARAM_API_KEY + "=" + BuildConfig.TMDB_AUTH_TOKEN;

        return url;
    }

    private static String addParam(String url, String key, String value) {
        return url + "&" + key + "=" + URLEncoder.encode(value);
    }

    public static String getImageUrl(String url) {
        return BASE_IMAGE + url;
    }

    public static void searchMovie(Context context, String title, int year, Response response) {

        String url = getUrl(SEARCH, MOVIE);
        url = addParam(url, PARAM_SEARCH, title);
        url = addParam(url, PARAM_YEAR, Integer.toString(year));

        Ion.with(context)
                .load(url)
                .asJsonObject()
                .setCallback(response);
    }

    public static void get(Context context, long id, Response response){
        String url = getUrl(MOVIE, "/" + id);

        Ion.with(context)
                .load(url)
                .asJsonObject()
                .setCallback(response);
    }

    public static void updateMovieGenres(Context context){
        Ion.with(context)
                .load(getUrl(GENRE, MOVIE, LIST))
                .asJsonObject()
                .setCallback(new OnGenreResponse(context));
    }

    private static class OnGenreResponse extends Response{
        private Context mContext;

        public OnGenreResponse(Context context) {
            mContext = context;
        }

        @Override
        public void onSuccess(JsonObject result) {
            ProcessGenreResponse task = new ProcessGenreResponse(mContext, result);
            task.execute();
        }
    }

    private static class ProcessGenreResponse extends AsyncTask<Void, Void, Void>{
        private JsonObject mResult;
        private Context mContext;

        public ProcessGenreResponse(Context context, JsonObject result) {
            mContext = context;
            mResult = result;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            ArrayList<Genre> genres = Genre.fromApi(mResult.get("genres").getAsJsonArray());

            AppDatabase.getInstance(mContext).genreDao().insert(genres);

            return null;
        }
    }

}
