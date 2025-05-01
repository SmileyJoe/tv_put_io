package io.smileyjoe.putio.tv.object;

import androidx.annotation.DrawableRes;

import io.smileyjoe.putio.tv.R;
import io.smileyjoe.putio.tv.interfaces.ToggleItem;

public enum FolderAction implements ToggleItem {

    ACTION_REFRESH(1, R.drawable.ic_refresh);

    private int mId;
    @DrawableRes
    private int mIconResId;

    FolderAction(int mId, int mIconResId) {
        this.mId = mId;
        this.mIconResId = mIconResId;
    }

    @Override
    public int getIconResId() {
        return mIconResId;
    }

    @Override
    public boolean isSelected() {
        return false;
    }

    @Override
    public int getId() {
        return mId;
    }
}
