package io.smileyjoe.putio.tv.interfaces;

import android.content.Context;
import android.os.Parcelable;

import androidx.annotation.DrawableRes;

import io.smileyjoe.putio.tv.object.FolderType;

public interface Folder extends Parcelable {

    @DrawableRes
    int getIconResId();
    String getTitle();
    String getSubTextOne(Context context);
    String getSubTextTwo(Context context);
    FolderType getFolderType();

}
