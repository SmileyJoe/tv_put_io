package io.smileyjoe.putio.tv.action.video;

import android.app.Activity;
import android.util.Log;

import java.util.ArrayList;

import io.smileyjoe.putio.tv.object.Video;
import io.smileyjoe.putio.tv.object.VideoType;
import io.smileyjoe.putio.tv.ui.activity.PlaybackActivity;

public interface Play extends Action{

    default void playVideo(){
        play(getActivity(), getVideo(), false);
    }

    @Override
    default void handleClick(ActionOption option){
        playVideo();
    }

    @Override
    default void setupActions(){
        addAction(ActionOption.WATCH, true);
    }
}
