package io.smileyjoe.putio.tv.action.video;

import io.smileyjoe.putio.tv.broadcast.UpdateVideoReceiver;
import io.smileyjoe.putio.tv.db.AppDatabase;
import io.smileyjoe.putio.tv.network.Putio;
import io.smileyjoe.putio.tv.object.Video;
import io.smileyjoe.putio.tv.util.Async;
import io.smileyjoe.putio.tv.util.PutioHelper;
import io.smileyjoe.putio.tv.video.VideoCache;

public interface RefreshAction extends Action, UpdateVideoReceiver {

    @Override
    default void update(Video video){
        UpdateVideoReceiver.super.deregisterReceiver();
    }

    default void refreshData() {
        Async.run(() -> {
            AppDatabase.getInstance(getContext()).videoDao().delete(getVideo().getPutId());
            PutioHelper helper = new PutioHelper(getContext());
            helper.parse(getVideo().getPutId(), getVideo().getParentTmdbId(), Putio.Files.get(getContext(), getVideo().getPutId()));
        });
    }

    @Override
    default void setupActions() {
        addAction(ActionOption.REFRESH_DATA, true);
    }

    @Override
    default void handleClick(ActionOption option) {
        UpdateVideoReceiver.super.registerReceiver();
        refreshData();
    }

}
