package io.smileyjoe.putio.tv.action.video;

public interface PlayAction extends Action {

    default void playVideo() {
        play(getVideo(), false);
    }

    @Override
    default void handleClick(ActionOption option) {
        if (option == ActionOption.WATCH) {
            playVideo();
        }
    }

    @Override
    default void setupActions() {
        addAction(ActionOption.WATCH, true);
    }
}
