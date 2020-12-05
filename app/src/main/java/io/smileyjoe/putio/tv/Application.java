package io.smileyjoe.putio.tv;

import android.content.Context;
import android.text.TextUtils;

import com.facebook.stetho.Stetho;

import io.smileyjoe.putio.tv.util.SharedPrefs;

public class Application extends android.app.Application {

    private static String sPutToken;
    private static Context sApplicationContext;

    public void onCreate() {
        super.onCreate();
        Stetho.initializeWithDefaults(this);
        sApplicationContext = getApplicationContext();
        setPutToken(SharedPrefs.getInstance(getApplicationContext()).getPutToken());
    }

    public static Context getStaticContext() {
        return sApplicationContext;
    }

    public static String getPutToken() {
        return sPutToken;
    }

    public static void setPutToken(String putToken){
        sPutToken = putToken;
    }
}
