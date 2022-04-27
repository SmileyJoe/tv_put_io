package io.smileyjoe.putio.tv.network;

import android.content.Context;

import com.google.gson.JsonObject;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.builder.Builders;

import java.util.concurrent.ExecutionException;

import io.smileyjoe.putio.tv.Application;
import io.smileyjoe.putio.tv.BuildConfig;
import io.smileyjoe.putio.tv.util.Settings;

public class Putio {

    public static class Auth extends Base {
        private static final String URL_CODE = BASE + "/oauth2/oob/code";
        private static final String URL_TOKEN = URL_CODE + "/{code}";

        public static void getCode(Context context, Response response) {
            String url = URL_CODE + "?app_id=" + BuildConfig.PUTIO_CLIENT_ID;

            Ion.with(context)
                    .load(url)
                    .asJsonObject()
                    .withResponse()
                    .setCallback(response);
        }

        public static void getToken(Context context, String code, Response response) {
            String url = URL_TOKEN.replace("{code}", code);
            execute(context, url, response);
        }
    }

    public static class Subtitle extends Base {
        private static final String URL_AVAILABLE = BASE + "/files/{id}/subtitles";
        private static final String URL_SUBTITLES = URL_AVAILABLE + "/{key}";

        public static void available(Context context, long id, Response response) {
            String url = URL_AVAILABLE.replace("{id}", Long.toString(id));
            execute(context, url, response);
        }

        public static void get(Context context, long id, String key, ResponseString response) {
            String url = URL_SUBTITLES
                    .replace("{id}", Long.toString(id))
                    .replace("{key}", key);

            getBaseCall(context, url)
                    .asString()
                    .withResponse()
                    .setCallback(response);
        }
    }


    public static class Resume extends Base {
        private static final String URL = BASE + "/files/{id}/start-from";

        private static String getUrl(long id) {
            return URL.replace("{id}", Long.toString(id));
        }

        public static void set(Context context, long id, long seconds, Response response) {
            JsonObject body = new JsonObject();
            body.addProperty("time", seconds);

            execute(context, getUrl(id), body, response);
        }

        public static void get(Context context, long id, Response response) {
            execute(context, getUrl(id), response);
        }
    }

    public static class Convert extends Base {
        private static final String URL = BASE + "/files/{id}/mp4";

        private static String getUrl(long id) {
            return URL.replace("{id}", Long.toString(id));
        }

        public static void start(Context context, long id, Response response) {
            execute(context, getUrl(id), new JsonObject(), response);
        }

        public static void status(Context context, long id, Response response) {
            execute(context, getUrl(id), response);
        }
    }

    public static class Account extends Base {
        private static final String URL_INFO = BASE + "/account/info";

        public static void info(Context context, Response response) {
            execute(context, URL_INFO, response);
        }
    }

    public static class Files extends Base {
        public static final long NO_PARENT = 0;
        public static final long PARENT_ID_RECENT = -1;
        private static final String URL = BASE + "/files/list" +
                "?stream_url=true" +
                "&mp4_stream_url=true" +
                "&mp4_status=true" +
                "&stream_url_parent=true" +
                "&mp4_stream_url_parent=true" +
                "&mp4_status_parent=true";

        private static String getUrl(Context context, long parentId) {
            String url = URL;

            if (parentId == NO_PARENT) {
                url += "&file_type=FOLDER,VIDEO";
            } else if (parentId == PARENT_ID_RECENT) {
                url += "&file_type=VIDEO" +
                        "&per_page=" + (Settings.getInstance(context).getVideoNumCols() * 3) +
                        "&sort_by=DATE_DESC" +
                        "&parent_id=" + parentId;
            } else {
                url += "&file_type=FOLDER,VIDEO" +
                        "&parent_id=" + parentId;
            }

            return url;
        }

        public static void get(Context context, long parentId, Response response) {
            execute(context, getUrl(context, parentId), response);
        }

        public static JsonObject get(Context context, long parentId) {

            try {
                return getBaseCall(context, getUrl(context, parentId))
                        .asJsonObject()
                        .get();
            } catch (InterruptedException | ExecutionException e) {
                return null;
            }
        }
    }

    private abstract static class Base {
        protected static final String BASE = "https://api.put.io/v2";

        protected static Builders.Any.B getBaseCall(Context context, String url) {
            return Ion.with(context)
                    .load(url)
                    .setHeader("client_id", BuildConfig.PUTIO_CLIENT_ID)
                    .setHeader("client_secret", BuildConfig.PUTIO_CLIENT_SECRET)
                    .setHeader("Authorization", "Bearer " + Application.getPutToken());
        }

        protected static void execute(Context context, String url, Response response) {
            getBaseCall(context, url)
                    .asJsonObject()
                    .withResponse()
                    .setCallback(response);
        }

        protected static void execute(Context context, String url, JsonObject jsonObject, Response response) {
            getBaseCall(context, url)
                    .setJsonObjectBody(jsonObject)
                    .asJsonObject()
                    .withResponse()
                    .setCallback(response);
        }
    }

}
