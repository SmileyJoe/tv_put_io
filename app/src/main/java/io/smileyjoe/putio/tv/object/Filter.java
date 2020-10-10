package io.smileyjoe.putio.tv.object;

import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;

import io.smileyjoe.putio.tv.R;

public enum Filter {

    SHOW_WATCHED(R.drawable.ic_hide_watched_24, R.string.text_hide_watched, R.string.text_show_watched, false),
    SORT_CREATED(R.drawable.ic_sort_by_created_24, R.string.text_sort_created, R.string.text_sort_default, false);

    @DrawableRes private int mIconResId;
    @StringRes private int mTextResId;
    @StringRes private int mSelectedTextResId;
    private boolean mDefaultSelected;

    Filter(int iconResId, int textResId, int selectedTextResId, boolean defaultSelected) {
        mIconResId = iconResId;
        mTextResId = textResId;
        mSelectedTextResId = selectedTextResId;
        mDefaultSelected = defaultSelected;
    }

    public int getIconResId() {
        return mIconResId;
    }

    public int getTextResId() {
        return mTextResId;
    }

    public int getSelectedTextResId() {
        return mSelectedTextResId;
    }

    public boolean isDefaultSelected() {
        return mDefaultSelected;
    }
}
