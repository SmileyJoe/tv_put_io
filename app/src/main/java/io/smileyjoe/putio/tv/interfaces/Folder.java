package io.smileyjoe.putio.tv.interfaces;

import android.content.Context;

import androidx.annotation.DrawableRes;

public interface Folder {

    @DrawableRes int getIconResId();
    String getTitle();
    String getSubTextOne(Context context);
    String getSubTextTwo(Context context);

}
