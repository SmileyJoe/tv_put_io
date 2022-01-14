package io.smileyjoe.putio.tv.action.video;

import com.google.gson.JsonObject;

import io.smileyjoe.putio.tv.network.Putio;
import io.smileyjoe.putio.tv.network.Response;
import io.smileyjoe.putio.tv.object.Video;

public interface ResumeAction extends Action {

    void updateActionResume();

    default void resumeVideo() {
        play(getActivity(), getVideo(), true);
    }

    @Override
    default void setupActions() {
        ActionOption option = ActionOption.RESUME;
        Video video = getVideo();

        boolean shouldShow = true;
        if (video.getResumeTime() <= 0) {
            shouldShow = false;
        }

        addAction(ActionOption.RESUME, getBaseContext().getString(option.getTitleResId()), video.getResumeTimeFormatted(), shouldShow);
    }

    @Override
    default void handleClick(ActionOption option) {
        resumeVideo();
    }

    default void getResumeTime() {
        Putio.getResumeTime(getBaseContext(), getVideo().getPutId(), new OnResumeResponse(getVideo(), this));
    }

    class OnResumeResponse extends Response {
        private Video mVideo;
        private ResumeAction mListener;

        public OnResumeResponse(Video video, ResumeAction listener) {
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
                if (mListener != null) {
                    mListener.updateActionResume();
                }
            }
        }
    }
}
