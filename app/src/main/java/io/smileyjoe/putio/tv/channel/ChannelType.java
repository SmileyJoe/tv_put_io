package io.smileyjoe.putio.tv.channel;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import androidx.annotation.StringRes;

import io.smileyjoe.putio.tv.R;
import io.smileyjoe.putio.tv.ui.activity.MainActivity;

public enum ChannelType {

    DEFAULT("default", R.string.channel_default_title, R.string.channel_default_description);

    private String mInternalId;
    @StringRes
    private int mTitle;
    @StringRes
    private int mDescription;

    ChannelType(String internalId,@StringRes int title,@StringRes int description) {
        mInternalId = internalId;
        mTitle = title;
        mDescription = description;
    }

    public String getInternalId() {
        return mInternalId;
    }

    public String getDescription(Context context) {
        return context.getString(mDescription);
    }

    public String getTitle(Context context) {
        return context.getString(mTitle);
    }

    public Uri getIntentUri(Context context){
        switch (this){
            case DEFAULT:
                return Uri.parse(MainActivity.getIntent(context).toUri(Intent.URI_INTENT_SCHEME));
            default:
                return null;
        }
    }
}
