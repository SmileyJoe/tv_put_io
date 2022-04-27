package io.smileyjoe.putio.tv.action.video;

import io.smileyjoe.putio.tv.db.AppDatabase;
import io.smileyjoe.putio.tv.network.Putio;
import io.smileyjoe.putio.tv.object.Video;
import io.smileyjoe.putio.tv.util.Async;
import io.smileyjoe.putio.tv.util.PutioHelper;
import io.smileyjoe.putio.tv.util.VideoLoader;

public interface RefreshAction extends Action {

    void update(Video video);

    default void refreshData() {
        Async.run(() -> {
            AppDatabase.getInstance(getContext()).videoDao().delete(getVideo().getPutId());
            PutioHelper helper = new PutioHelper(getContext());
            helper.setListener(video -> {
                VideoLoader.update(getContext(), video);
                update(video);
            });
            helper.parse(getVideo().getPutId(), getVideo().getParentTmdbId(), Putio.Files.get(getContext(), getVideo().getPutId()));
        });
    }

    @Override
    default void setupActions() {
        addAction(ActionOption.REFRESH_DATA, true);
    }

    @Override
    default void handleClick(ActionOption option) {
        refreshData();
    }

}
