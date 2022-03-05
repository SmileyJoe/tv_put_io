package io.smileyjoe.putio.tv.util;

import android.content.Context;
import android.content.SharedPreferences;

import io.smileyjoe.putio.tv.BuildConfig;

public class Settings {

    private static final String NAME = BuildConfig.APPLICATION_ID + ".prefs_settings";
    private static final String KEY_SHOW_RECENTLY_ADDED = "show_recently_added";

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

    public void shouldShowRecentlyAdded(boolean shouldShow) {
        mPrefs.edit().putBoolean(KEY_SHOW_RECENTLY_ADDED, shouldShow).apply();
    }

    public boolean shouldShowRecentlyAdded() {
        return mPrefs.getBoolean(KEY_SHOW_RECENTLY_ADDED, true);
    }

}
