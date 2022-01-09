package io.smileyjoe.putio.tv.util;

import android.view.View;
import android.widget.TextView;

import io.smileyjoe.putio.tv.R;
import io.smileyjoe.putio.tv.object.Video;

public class ViewUtil {

    public static void populateResumeTime(TextView view, Video video){
        if(view != null){
            if(video.getResumeTime() > 0){
                view.setVisibility(View.VISIBLE);
                view.setText(view.getContext().getString(R.string.text_resume) + ": " + video.getResumeTimeFormatted());
            } else {
                view.setVisibility(View.GONE);
            }
        }
    }

}
