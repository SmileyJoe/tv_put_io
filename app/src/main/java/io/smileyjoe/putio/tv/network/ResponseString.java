package io.smileyjoe.putio.tv.network;

import android.util.Log;

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Response;

public abstract class ResponseString implements FutureCallback<Response<String>> {

    public abstract void onSuccess(String result);

    public void onFail(Exception e) {
    }

    @Override
    public void onCompleted(Exception e, com.koushikdutta.ion.Response<String> result) {
        int responseCode = result.getHeaders().code();

        if(responseCode == 401){
            Log.e("PutThings", result.getRequest().getPath());
//            Context context = Application.getStaticContext();
//
//            SharedPrefs prefs = SharedPrefs.getInstance(context);
//            prefs.clearPutToken();
//            Application.setPutToken(null);
//
//            context.startActivity(AuthActivity.getIntent(context));
        }

        if (e == null) {
            onSuccess(result.getResult());
        } else {
            onFail(e);
        }
    }

}
