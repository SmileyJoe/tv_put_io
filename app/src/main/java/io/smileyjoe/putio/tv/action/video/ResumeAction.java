package io.smileyjoe.putio.tv.action.video;

import com.google.gson.JsonObject;

import java.util.Optional;

import io.smileyjoe.putio.tv.network.Putio;
import io.smileyjoe.putio.tv.network.Response;
import io.smileyjoe.putio.tv.object.Video;

public interface ResumeAction extends Action {

    void updateActionResume();

    default void resumeVideo() {
        play(getVideo(), true);
    }

    @Override
    default void setupActions() {
        ActionOption option = ActionOption.RESUME;
        Video video = getVideo();

        addAction(ActionOption.RESUME,
                getContext().getString(option.getTitleResId()),
                video.getResumeTimeFormatted(),
                video.getResumeTime() > 0);
    }

    @Override
    default void handleClick(ActionOption option) {
        resumeVideo();
    }

    default void getResumeTime() {
        Putio.Resume.get(getContext(), getVideo().getPutId(), new OnResumeResponse(getVideo(), this));
    }

    class OnResumeResponse extends Response {
        private Video mVideo;
        private Optional<ResumeAction> mListener;

        public OnResumeResponse(Video video, ResumeAction listener) {
            mVideo = video;
            mListener = Optional.ofNullable(listener);
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
                mListener.ifPresent(listener -> listener.updateActionResume());
            }
        }
    }
}
