package io.smileyjoe.putio.tv.object;

import androidx.annotation.StringRes;

import io.smileyjoe.putio.tv.R;

public enum DetailsAction {
    UNKNOWN(0, 0, false, -1),
    WATCH(1, R.string.action_watch, true, 0),
    RESUME(2, R.string.action_resume, true, 1),
    CONVERT(3, R.string.action_convert, false, 2),
    TRAILER(4, R.string.action_trailer, false, 3),
    REFRESH_DATA(5, R.string.action_refresh, true, 4);

    private long mId;
    private @StringRes int mTitleResId;
    private boolean mShow;
    private int mPosition;

    DetailsAction(long id, int titleResId, boolean show, int position) {
        mId = id;
        mTitleResId = titleResId;
        mShow = show;
        mPosition = position;
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

    public int getPosition() {
        return mPosition;
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
