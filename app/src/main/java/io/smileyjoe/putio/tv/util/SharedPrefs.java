package io.smileyjoe.putio.tv.util;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefs {

    private static final String NAME = "putio_prefs";
    private static final String KEY_PUT_IO_TOKEN = "put_io_token";

    private SharedPreferences mPrefs;
    private static SharedPrefs sInstance;

    private SharedPrefs(Context context) {
        mPrefs = context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
    }

    public static SharedPrefs getInstance(Context context){
        if(sInstance == null){
            sInstance = new SharedPrefs(context);
        }

        return sInstance;
    }

    public void savePutToken(String token){
        mPrefs.edit().putString(KEY_PUT_IO_TOKEN, token).apply();
    }

    public String getPutToken(){
        return mPrefs.getString(KEY_PUT_IO_TOKEN, null);
    }
}
