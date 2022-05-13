package io.smileyjoe.putio.tv.action.video;

import io.smileyjoe.putio.tv.broadcast.UpdateVideoReceiver;
import io.smileyjoe.putio.tv.object.Video;
import io.smileyjoe.putio.tv.util.Async;
import io.smileyjoe.putio.tv.video.VideoLoader;

public interface RefreshAction extends Action, UpdateVideoReceiver {

    @Override
    default void update(Video video) {
        UpdateVideoReceiver.super.deregisterReceiver();
    }

    default void refreshData() {
        Async.run(() -> new VideoLoader(getContext()).refresh(getVideo()));
    }

    @Override
    default void setupActions() {
        addAction(ActionOption.REFRESH_DATA, true);
    }

    @Override
    default void handleClick(ActionOption option) {
        if (option == ActionOption.REFRESH_DATA) {
            UpdateVideoReceiver.super.registerReceiver();
            refreshData();
        }
    }

}
