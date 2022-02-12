package io.smileyjoe.putio.tv.channel;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;

import androidx.annotation.StringRes;

import java.util.Arrays;

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

    public static ChannelType fromInternalId(String id){
        return Arrays.stream(values())
                .filter(type -> type.getInternalId().equals(id))
                .findFirst()
                .orElse(null);
    }
}
