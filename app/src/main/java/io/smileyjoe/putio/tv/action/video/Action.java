package io.smileyjoe.putio.tv.action.video;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;

import androidx.leanback.widget.OnActionClickedListener;

import java.util.ArrayList;

import io.smileyjoe.putio.tv.object.Video;
import io.smileyjoe.putio.tv.object.VideoType;
import io.smileyjoe.putio.tv.ui.activity.PlaybackActivity;

public interface Action {
    Activity getActivity();
    Video getVideo();
    void handleClick(ActionOption option);
    void addAction(ActionOption option, String title, String subtitle, boolean shouldShow);
    void setupActions();

    default void handleClick(androidx.leanback.widget.Action action){
        // do nothing //
    }

    default Context getBaseContext(){
        return getActivity().getBaseContext();
    }

    default void addAction(ActionOption option, boolean shouldShow){
        addAction(option, getBaseContext().getString(option.getTitleResId()), null, shouldShow);
    }

    default String getTitleFormatted(String title, String subtitle){
        if(!TextUtils.isEmpty(subtitle)){
            return title + ": " + subtitle;
        } else {
            return title;
        }
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

    default void play(Activity activity, Video video, boolean shouldResume){
        play(activity, video, null, shouldResume);
    }

    default void play(Activity activity, Video video, ArrayList<Video> videos, boolean shouldResume){
        if(video.getVideoType() == VideoType.EPISODE){
            if(videos != null && !videos.isEmpty()) {
                activity.startActivity(PlaybackActivity.getIntent(activity.getBaseContext(), videos, video, shouldResume));
                return;
            }
        }

        activity.startActivity(PlaybackActivity.getIntent(activity.getBaseContext(), video, shouldResume));
    }
}
