package io.smileyjoe.putio.tv.network;

import android.content.Context;

import com.google.gson.JsonObject;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.builder.Builders;

import java.util.concurrent.ExecutionException;

import io.smileyjoe.putio.tv.Application;
import io.smileyjoe.putio.tv.BuildConfig;

public class Putio {

    public static final long NO_PARENT = -100;
    private static final String BASE = "https://api.put.io/v2";
    private static final String FILES = "/files/list";
    private static final String DOWNLOAD_URL = "/files/{id}/url";
    private static final String RESUME_TIME = "/files/{id}/start-from";
    private static final String CONVERT = "/files/{id}/mp4";
    private static final String AUTH_GET_CODE = "/oauth2/oob/code";
    private static final String AUTH_GET_TOKEN = "/oauth2/oob/code/{code}";
    private static final String SUBTITLES_AVAILABLE = "/files/{id}/subtitles";
    private static final String SUBTITLES = SUBTITLES_AVAILABLE + "/{key}";

    public static void getAuthCode(Context context, Response response) {
        String url = BASE + AUTH_GET_CODE + "?app_id=" + BuildConfig.PUTIO_CLIENT_ID;

        Ion.with(context)
                .load(url)
                .asJsonObject()
                .withResponse()
                .setCallback(response);
    }

    public static void getAuthToken(Context context, String code, Response response) {
        String url = BASE + AUTH_GET_TOKEN;
        url = url.replace("{code}", code);
        execute(context, url, response);
    }

    public static void getAvailableSubtitles(Context context, long id, Response response) {
        String url = BASE + SUBTITLES_AVAILABLE.replace("{id}", Long.toString(id));
        execute(context, url, response);
    }

    public static void getSubtitles(Context context, long id, String key, ResponseString response) {
        String url = BASE + SUBTITLES
                .replace("{id}", Long.toString(id))
                .replace("{key}", key);

        getBaseCall(context, url)
                .asString()
                .withResponse()
                .setCallback(response);
    }

    public static void getFiles(Context context, Response response) {
        getFiles(context, NO_PARENT, response);
    }

    public static void getFiles(Context context, long parentId, Response response) {
        String url = BASE + FILES + "?stream_url=true&mp4_stream_url=true&file_type=FOLDER,VIDEO&mp4_status=true&stream_url_parent=true&mp4_stream_url_parent=true&mp4_status_parent=true";

        if (parentId != NO_PARENT) {
            url += "&parent_id=" + parentId;
        }

        execute(context, url, response);
    }

    public static JsonObject getFiles(Context context, long parentId) {
        String url = BASE + FILES + "?stream_url=true&mp4_stream_url=true&file_type=FOLDER,VIDEO&mp4_status=true&stream_url_parent=true&mp4_stream_url_parent=true&mp4_status_parent=true";

        if (parentId != NO_PARENT) {
            url += "&parent_id=" + parentId;
        }

        try {
            return getBaseCall(context, url)
                    .asJsonObject()
                    .get();
        } catch (InterruptedException | ExecutionException e) {
            return null;
        }
    }

    public static void getResumeTime(Context context, long id, Response response) {
        String url = BASE + RESUME_TIME;

        url = url.replace("{id}", Long.toString(id));

        execute(context, url, response);
    }

    public static void setResumeTime(Context context, long id, long seconds, Response response) {
        String url = BASE + RESUME_TIME;
        url = url.replace("{id}", Long.toString(id));

        JsonObject body = new JsonObject();
        body.addProperty("time", seconds);

        getBaseCall(context, url)
                .setHeader("Authorization", "Bearer " + Application.getPutToken())
                .setJsonObjectBody(body)
                .asJsonObject()
                .withResponse()
                .setCallback(response);
    }

    public static void getConversionStatus(Context context, long id, Response response) {
        String url = BASE + CONVERT;
        url = url.replace("{id}", Long.toString(id));
        execute(context, url, response);
    }

    public static void convertFile(Context context, long id, Response response) {
        String url = BASE + CONVERT;
        url = url.replace("{id}", Long.toString(id));

        getBaseCall(context, url)
                .setJsonObjectBody(new JsonObject())
                .asJsonObject()
                .withResponse()
                .setCallback(response);
    }

    public static void getDownloadUrl(Context context, long id, Response response) {
        String url = BASE + DOWNLOAD_URL;

        url = url.replace("{id}", Long.toString(id));

        execute(context, url, response);
    }

    private static Builders.Any.B getBaseCall(Context context, String url) {
        return Ion.with(context)
                .load(url)
                .setHeader("client_id", BuildConfig.PUTIO_CLIENT_ID)
                .setHeader("client_secret", BuildConfig.PUTIO_CLIENT_SECRET)
                .setHeader("Authorization", "Bearer " + Application.getPutToken());
    }

    private static void execute(Context context, String url, Response response) {
        getBaseCall(context, url)
                .asJsonObject()
                .withResponse()
                .setCallback(response);
    }

}
