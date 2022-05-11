package io.smileyjoe.putio.tv.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.Map;
import java.util.Set;

import io.smileyjoe.putio.tv.BuildConfig;
import io.smileyjoe.putio.tv.network.Putio;
import io.smileyjoe.putio.tv.network.Response;
import io.smileyjoe.putio.tv.ui.adapter.VideosAdapter;

public class Settings {

    public interface RestoreListener{
        void proceed();
    }

    private static final String NAME = BuildConfig.APPLICATION_ID + ".prefs_settings";
    private static final String KEY_SHOW_RECENTLY_ADDED = "show_recently_added";
    private static final String KEY_VIDEO_LAYOUT = "video_layout";
    private static final String KEY_VIDEO_NUM_COLS = "video_num_cols";

    private SharedPreferences mPrefs;
    private static Settings sInstance;

    private Settings(Context context) {
        mPrefs = context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
    }

    public static Settings getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new Settings(context);
        }

        return sInstance;
    }

    public void shouldShowRecentlyAdded(Context context, boolean shouldShow) {
        mPrefs.edit().putBoolean(KEY_SHOW_RECENTLY_ADDED, shouldShow).apply();
        Putio.Config.save(context, KEY_SHOW_RECENTLY_ADDED, shouldShow);
    }

    public boolean shouldShowRecentlyAdded() {
        return mPrefs.getBoolean(KEY_SHOW_RECENTLY_ADDED, true);
    }

    public void setVideoLayout(Context context, int styleId) {
        mPrefs.edit().putInt(KEY_VIDEO_LAYOUT, styleId).apply();
        Putio.Config.save(context, KEY_VIDEO_LAYOUT, styleId);
    }

    public VideosAdapter.Style getVideoLayout() {
        return VideosAdapter.Style.fromId(mPrefs.getInt(KEY_VIDEO_LAYOUT, VideosAdapter.Style.GRID.getId()));
    }

    public void setVideoNumCols(Context context, int cols) {
        mPrefs.edit().putInt(KEY_VIDEO_NUM_COLS, cols).apply();
        Putio.Config.save(context, KEY_VIDEO_NUM_COLS, cols);
    }

    public int getVideoNumCols() {
        return mPrefs.getInt(KEY_VIDEO_NUM_COLS, 7);
    }

    public static void restore(Context context, RestoreListener restoreListener){
        Putio.Config.get(context, new Response() {
            @Override
            public void onSuccess(JsonObject result) {
                Settings settings = new Settings(context);

                JsonObject config = result.getAsJsonObject("config");
                Set<Map.Entry<String, JsonElement>> entrySet = config.entrySet();
                for(Map.Entry<String,JsonElement> entry : entrySet){
                    switch (entry.getKey()){
                        case KEY_SHOW_RECENTLY_ADDED:
                            settings.shouldShowRecentlyAdded(context, entry.getValue().getAsBoolean());
                            break;
                        case KEY_VIDEO_LAYOUT:
                            settings.setVideoLayout(context, entry.getValue().getAsInt());
                            break;
                        case KEY_VIDEO_NUM_COLS:
                            settings.setVideoNumCols(context, entry.getValue().getAsInt());
                            break;
                    }
                }

                restoreListener.proceed();
            }

            @Override
            public void onFail(Exception e) {
                restoreListener.proceed();
            }
        });
    }

}
