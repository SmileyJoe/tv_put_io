package io.smileyjoe.putio.tv.object;

import androidx.annotation.StringRes;

import io.smileyjoe.putio.tv.R;

public enum DetailsAction {
    UNKNOWN(0, 0, false),
    WATCH(1, R.string.action_watch, true),
    RESUME(2, R.string.action_resume, true),
    CONVERT(3, R.string.action_convert, false),
    TRAILER(4, R.string.action_trailer, true),
    REFRESH_DATA(5, R.string.action_refresh, true);

    private long mId;
    private @StringRes int mTitleResId;
    private boolean mShow;


    DetailsAction(long id, int titleResId, boolean show) {
        mId = id;
        mTitleResId = titleResId;
        mShow = show;
    }

    public long getId() {
        return mId;
    }

    public int getTitleResId() {
        return mTitleResId;
    }

    public boolean shouldShow() {
        return mShow;
    }

    public static DetailsAction fromId(long id) {
        for (DetailsAction option : values()) {
            if (option.getId() == id) {
                return option;
            }
        }

        return UNKNOWN;
    }
}
