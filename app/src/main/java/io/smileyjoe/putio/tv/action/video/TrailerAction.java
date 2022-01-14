package io.smileyjoe.putio.tv.action.video;

import android.text.TextUtils;

import io.smileyjoe.putio.tv.ui.activity.PlaybackActivity;

public interface TrailerAction extends Action {

    default void playTrailer() {
        getActivity().startActivity(PlaybackActivity.getIntent(getBaseContext(), getVideo().getYoutubeTrailerUrl()));
    }

    @Override
    default void handleClick(ActionOption option) {
        playTrailer();
    }

    @Override
    default void setupActions() {
        if (!TextUtils.isEmpty(getVideo().getYoutubeTrailerUrl())) {
            addAction(ActionOption.TRAILER, true);
        }
    }

}
