package io.smileyjoe.putio.tv.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.Map;
import java.util.Set;

import io.smileyjoe.putio.tv.BuildConfig;
import io.smileyjoe.putio.tv.db.AppDatabase;
import io.smileyjoe.putio.tv.network.Putio;
import io.smileyjoe.putio.tv.network.Response;
import io.smileyjoe.putio.tv.object.Group;
import io.smileyjoe.putio.tv.ui.adapter.VideosAdapter;

public class Settings {

    public interface RestoreListener {
        void proceed();
    }

    private static final String NAME = BuildConfig.APPLICATION_ID + ".prefs_settings";
    private static final String KEY_SHOW_RECENTLY_ADDED = "show_recently_added";
    private static final String KEY_VIDEO_LAYOUT = "video_layout";
    private static final String KEY_VIDEO_NUM_COLS = "video_num_cols";
    private static final String KEY_GROUP_ENABLED = "group_enabled_";
    private static final String KEY_GROUP_PUT_IDS = "group_put_ids_";
    public static final String KEY_LAST_PUT_UPDATE = "last_config_update";

    private SharedPreferences mPrefs;
    private static Settings sInstance;
    private boolean mFromRestore = false;

    private Settings(Context context) {
        mPrefs = context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
    }

    public static Settings getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new Settings(context);
        }

        return sInstance;
    }

    public void setFromRestore(boolean fromRestore) {
        mFromRestore = fromRestore;
    }

    public void shouldShowRecentlyAdded(Context context, boolean shouldShow) {
        mPrefs.edit().putBoolean(KEY_SHOW_RECENTLY_ADDED, shouldShow).apply();

        if (!mFromRestore) {
            Putio.Config.save(context, KEY_SHOW_RECENTLY_ADDED, shouldShow);
        }
    }

    public boolean shouldShowRecentlyAdded() {
        return mPrefs.getBoolean(KEY_SHOW_RECENTLY_ADDED, true);
    }

    public void setVideoLayout(Context context, int styleId) {
        mPrefs.edit().putInt(KEY_VIDEO_LAYOUT, styleId).apply();

        if (!mFromRestore) {
            Putio.Config.save(context, KEY_VIDEO_LAYOUT, styleId);
        }
    }

    public VideosAdapter.Style getVideoLayout() {
        return VideosAdapter.Style.fromId(mPrefs.getInt(KEY_VIDEO_LAYOUT, VideosAdapter.Style.GRID.getId()));
    }

    public void setVideoNumCols(Context context, int cols) {
        mPrefs.edit().putInt(KEY_VIDEO_NUM_COLS, cols).apply();

        if (!mFromRestore) {
            Putio.Config.save(context, KEY_VIDEO_NUM_COLS, cols);
        }
    }

    public int getVideoNumCols() {
        return mPrefs.getInt(KEY_VIDEO_NUM_COLS, 7);
    }

    public long updateLastPutUpdate() {
        long millies = System.currentTimeMillis();
        mPrefs.edit().putLong(KEY_LAST_PUT_UPDATE, millies).apply();

        return millies;
    }

    public long getLastPutUpdate() {
        return mPrefs.getLong(KEY_LAST_PUT_UPDATE, -1);
    }

    public void saveGroupEnabled(Context context, long id, boolean isEnabled) {
        Putio.Config.save(context, KEY_GROUP_ENABLED + Long.toString(id), isEnabled);
    }

    public void saveGroupPutIds(Context context, Group group) {
        Putio.Config.save(context, KEY_GROUP_PUT_IDS + Long.toString(group.getId()), group.getPutIdsJson());
    }

    public static void restore(Context context, RestoreListener restoreListener) {
        Putio.Config.get(context, new Response() {
            @Override
            public void onSuccess(JsonObject result) {
                Async.run(() -> {
                    Settings settings = new Settings(context);
                    JsonObject config = result.getAsJsonObject("config");

                    if (config.has(KEY_LAST_PUT_UPDATE) && (config.get(KEY_LAST_PUT_UPDATE).getAsLong()) > settings.getLastPutUpdate()) {
                        settings.setFromRestore(true);
                        Set<Map.Entry<String, JsonElement>> entrySet = config.entrySet();
                        for (Map.Entry<String, JsonElement> entry : entrySet) {
                            switch (entry.getKey()) {
                                case KEY_SHOW_RECENTLY_ADDED:
                                    settings.shouldShowRecentlyAdded(context, entry.getValue().getAsBoolean());
                                    break;
                                case KEY_VIDEO_LAYOUT:
                                    settings.setVideoLayout(context, entry.getValue().getAsInt());
                                    break;
                                case KEY_VIDEO_NUM_COLS:
                                    settings.setVideoNumCols(context, entry.getValue().getAsInt());
                                    break;
                                default:
                                    if (entry.getKey().contains(KEY_GROUP_ENABLED)) {
                                        try {
                                            long id = Long.parseLong(entry.getKey().replace(KEY_GROUP_ENABLED, "").trim());
                                            AppDatabase.getInstance(context).groupDao().enabled(id, entry.getValue().getAsBoolean());
                                        } catch (NumberFormatException e) {
                                            // do nothing, the setting just won't be restored //
                                        }
                                    } else if (entry.getKey().contains(KEY_GROUP_PUT_IDS)) {
                                        try {
                                            int id = Integer.parseInt(entry.getKey().replace(KEY_GROUP_PUT_IDS, "").trim());
                                            AppDatabase.getInstance(context).groupDao().updatePutIds(id, entry.getValue().getAsString());
                                        } catch (NumberFormatException e) {
                                            // do nothing, the setting just won't be restored //
                                        }
                                    }
                                    break;
                            }
                        }

                        settings.updateLastPutUpdate();
                        settings.setFromRestore(false);
                    }
                    restoreListener.proceed();
                });
            }

            @Override
            public void onFail(Exception e) {
                restoreListener.proceed();
            }
        });
    }

}
