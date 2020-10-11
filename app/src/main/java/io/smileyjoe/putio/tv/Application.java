package io.smileyjoe.putio.tv;

import android.text.TextUtils;

import com.facebook.stetho.Stetho;

import io.smileyjoe.putio.tv.util.SharedPrefs;

public class Application extends android.app.Application {

    private static String sPutToken;

    public void onCreate() {
        super.onCreate();
        Stetho.initializeWithDefaults(this);
        setPutToken(SharedPrefs.getInstance(getApplicationContext()).getPutToken());
    }

    public static String getPutToken() {
        return sPutToken;
    }

    public void setPutToken(String putToken){
        sPutToken = putToken;
    }
}
