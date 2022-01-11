package io.smileyjoe.putio.tv.util;

import android.app.Activity;

import java.util.ArrayList;

import io.smileyjoe.putio.tv.object.Video;
import io.smileyjoe.putio.tv.object.VideoType;
import io.smileyjoe.putio.tv.ui.activity.PlaybackActivity;

public class VideoDetailsHelper {

    public static void play(Activity activity, Video video, boolean shouldResume){
        play(activity, video, null, shouldResume);
    }

    public static void play(Activity activity, Video video, ArrayList<Video> videos, boolean shouldResume){
        if(video.getVideoType() == VideoType.EPISODE){
            if(videos != null && !videos.isEmpty()) {
                activity.startActivity(PlaybackActivity.getIntent(activity.getBaseContext(), videos, video, shouldResume));
                return;
            }
        }

        activity.startActivity(PlaybackActivity.getIntent(activity.getBaseContext(), video, shouldResume));
    }

}
