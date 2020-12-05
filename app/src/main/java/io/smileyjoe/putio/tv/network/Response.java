package io.smileyjoe.putio.tv.network;

import android.content.Context;
import android.util.Log;

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;

import io.smileyjoe.putio.tv.Application;
import io.smileyjoe.putio.tv.ui.activity.AuthActivity;
import io.smileyjoe.putio.tv.util.SharedPrefs;

public abstract class Response implements FutureCallback<com.koushikdutta.ion.Response<JsonObject>> {

    public abstract void onSuccess(JsonObject result);

    public void onFail(Exception e) {
    }

    @Override
    public void onCompleted(Exception e, com.koushikdutta.ion.Response<JsonObject> result) {
        int responseCode = result.getHeaders().code();

        if(responseCode == 401){
            Context context = Application.getStaticContext();

            SharedPrefs prefs = SharedPrefs.getInstance(context);
            prefs.clearPutToken();
            Application.setPutToken(null);

            context.startActivity(AuthActivity.getIntent(context));
        } else if (e == null) {
            onSuccess(result.getResult());
        } else {
            onFail(e);
        }
    }
}
