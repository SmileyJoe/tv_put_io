package io.smileyjoe.putio.tv.video;

import android.content.Context;

import com.google.gson.JsonObject;

import io.smileyjoe.putio.tv.network.Response;

public class OnPutResponse extends Response {
    private long mPutId;
    private Context mContext;

    public OnPutResponse(Context context, long putId) {
        mContext = context;
        mPutId = putId;
    }

    @Override
    public void onSuccess(JsonObject result) {
        new ProcessPutResponse(mContext, mPutId, result).run();
    }
}
