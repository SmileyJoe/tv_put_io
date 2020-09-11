package io.smileyjoe.putio.tv.putio;

import android.content.Context;
import android.util.Log;

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.util.ArrayList;

import io.smileyjoe.putio.tv.BuildConfig;

public class Putio {

    private static final long NO_PARENT = -100;
    private static final String BASE = "https://api.put.io/v2";
    private static final String FILES = "/files/list";
    private static final String DOWNLOAD_URL = "/files/{id}/url";
    private static final String RESUME_TIME = "/files/{id}/start-from";

    public static void getFiles(Context context, Response response){
        getFiles(context, NO_PARENT, response);
    }

    public static void getFiles(Context context, long parentId, Response response){
        String url = BASE + FILES;

        if(parentId != NO_PARENT){
            url += "?parent_id=" + parentId + "&stream_url=true";
        }

        execute(context, url, response);
    }

    public static void getResumeTime(Context context, long id, Response response){
        String url = BASE + RESUME_TIME;

        url = url.replace("{id}", Long.toString(id));

        execute(context, url, response);
    }

    public static void getDownloadUrl(Context context, long id, Response response){
        String url = BASE + DOWNLOAD_URL;

        url = url.replace("{id}", Long.toString(id));

        execute(context, url, response);
    }

    private static void execute(Context context, String url, Response response){
        Ion.with(context)
                .load(url)
                .setHeader("client_id", BuildConfig.PUTIO_CLIENT_ID)
                .setHeader("client_secret", BuildConfig.PUTIO_CLIENT_SECRET)
                .setHeader("Authorization", "Bearer " + BuildConfig.PUTIO_AUTH_TOKEN)
                .asJsonObject()
                .setCallback(response);
    }

}
