package io.smileyjoe.putio.tv.interfaces;

import android.content.Context;

import androidx.annotation.DrawableRes;

import io.smileyjoe.putio.tv.object.FolderType;

public interface Folder {

    @DrawableRes
    int getIconResId();
    String getTitle();
    String getSubTextOne(Context context);
    String getSubTextTwo(Context context);
    FolderType getFolderType();

}
