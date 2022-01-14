package io.smileyjoe.putio.tv.action.video;

import androidx.annotation.StringRes;

import io.smileyjoe.putio.tv.R;

public enum ActionOption {
    UNKNOWN(0, 0),
    WATCH(1, R.string.action_watch),
    RESUME(2, R.string.action_resume),
    CONVERT(3, R.string.action_convert),
    TRAILER(4, R.string.action_trailer),
    REFRESH_DATA(5, R.string.action_refresh);

    private long mId;
    private @StringRes
    int mTitleResId;

    ActionOption(long id, int titleResId) {
        mId = id;
        mTitleResId = titleResId;
    }

    public long getId() {
        return mId;
    }

    public int getTitleResId() {
        return mTitleResId;
    }

    public static ActionOption fromId(long id) {
        for (ActionOption option : ActionOption.values()) {
            if (option.getId() == id) {
                return option;
            }
        }

        return UNKNOWN;
    }
}
