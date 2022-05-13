package io.smileyjoe.putio.tv.action.video;

import android.content.Context;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.StringRes;
import androidx.leanback.widget.OnActionClickedListener;

import java.util.ArrayList;

import io.smileyjoe.putio.tv.object.Video;
import io.smileyjoe.putio.tv.object.VideoType;
import io.smileyjoe.putio.tv.ui.activity.PlaybackActivity;

public interface Action {

    Context getContext();
    Video getVideo();
    void handleClick(ActionOption option);
    void setupActions();

    default void handleClick(androidx.leanback.widget.Action action) {
        // do nothing //
    }

    default void addAction(ActionOption option, boolean shouldShow) {
        addAction(option, getContext().getString(option.getTitleResId()), null, shouldShow);
    }

    default void addAction(ActionOption option, String title, String subtitle, boolean shouldShow) {
        // do nothing //
    }

    default String getTitleFormatted(String title, String subtitle) {
        if (!TextUtils.isEmpty(subtitle)) {
            return title + ": " + subtitle;
        } else {
            return title;
        }
    }

    default void play(Video video, boolean shouldResume) {
        play(video, null, false, shouldResume);
    }

    default void play(Video video, boolean playMp4, boolean shouldResume) {
        play(video, null, playMp4, shouldResume);
    }

    default void play(Video video, ArrayList<Video> videos, boolean playMp4, boolean shouldResume) {
        if (video.getVideoType() == VideoType.EPISODE) {
            if (videos != null && !videos.isEmpty()) {
                getContext().startActivity(PlaybackActivity.getIntent(getContext(), videos, video, playMp4, shouldResume));
                return;
            }
        }

        getContext().startActivity(PlaybackActivity.getIntent(getContext(), video, playMp4, shouldResume));
    }

    default String getString(@StringRes int resId) {
        return getContext().getString(resId);
    }

    class OnButtonClicked implements View.OnClickListener {
        private Action mListener;
        private ActionOption mOption;

        public OnButtonClicked(ActionOption option, Action listener) {
            mOption = option;
            mListener = listener;
        }

        @Override
        public void onClick(View v) {
            mListener.handleClick(mOption);
        }
    }

    class OnActionClicked implements OnActionClickedListener {
        private Action mListener;

        public OnActionClicked(Action listener) {
            mListener = listener;
        }

        @Override
        public void onActionClicked(androidx.leanback.widget.Action action) {
            mListener.handleClick(action);
        }
    }

    class OnContextItemClicked implements MenuItem.OnMenuItemClickListener {
        private Action mListener;
        private ActionOption mOption;

        public OnContextItemClicked(ActionOption option, Action listener) {
            mListener = listener;
            mOption = option;
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            mListener.handleClick(mOption);
            return true;
        }
    }
}
