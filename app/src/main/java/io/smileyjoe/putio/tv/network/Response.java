package io.smileyjoe.putio.tv.network;

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;

public abstract class Response implements FutureCallback<JsonObject> {

    public abstract void onSuccess(JsonObject result);

    public void onFail(Exception e) {
    }

    @Override
    public void onCompleted(Exception e, JsonObject result) {
        if (e == null) {
            onSuccess(result);
        } else {
            onFail(e);
        }
    }
}
