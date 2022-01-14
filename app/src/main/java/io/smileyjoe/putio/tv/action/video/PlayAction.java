package io.smileyjoe.putio.tv.action.video;

public interface PlayAction extends Action {

    default void playVideo() {
        play(getActivity(), getVideo(), false);
    }

    @Override
    default void handleClick(ActionOption option) {
        playVideo();
    }

    @Override
    default void setupActions() {
        addAction(ActionOption.WATCH, true);
    }
}
