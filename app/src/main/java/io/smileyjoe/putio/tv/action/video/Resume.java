package io.smileyjoe.putio.tv.action.video;

import android.util.Log;

import com.google.gson.JsonObject;

import io.smileyjoe.putio.tv.network.Putio;
import io.smileyjoe.putio.tv.network.Response;
import io.smileyjoe.putio.tv.object.Video;

public interface Resume extends Action{

    void updateActionResume();

    default void resumeVideo(){
        play(getActivity(), getVideo(), true);
    }

    @Override
    default void setupActions(){
        ActionOption option = ActionOption.RESUME;
        Video video = getVideo();

        boolean shouldShow = true;
        String title = getBaseContext().getString(option.getTitleResId());
        switch (option) {
            case RESUME:
                if (video.getResumeTime() > 0) {
                    title = title + " : " + video.getResumeTimeFormatted();
                } else {
                    shouldShow = false;
                }
                break;
        }

        addAction(ActionOption.RESUME, shouldShow);
    }

    @Override
    default void handleClick(ActionOption option){
        resumeVideo();
    }

    default void getResumeTime() {
        Putio.getResumeTime(getBaseContext(), getVideo().getPutId(), new OnResumeResponse(getVideo(), this));
    }

    class OnResumeResponse extends Response {
        private Video mVideo;
        private Resume mListener;

        public OnResumeResponse(Video video, Resume listener) {
            mVideo = video;
            mListener = listener;
        }

        @Override
        public void onSuccess(JsonObject result) {
            try {
                long resumeTime = result.get("start_from").getAsLong();
                mVideo.setResumeTime(resumeTime);
            } catch (UnsupportedOperationException | NullPointerException e) {
                mVideo.setResumeTime(0);
            }

            if (mVideo.getResumeTime() > 0) {
                if(mListener != null) {
                    mListener.updateActionResume();
                }
            }
        }
    }
}
