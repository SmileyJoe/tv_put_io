package io.smileyjoe.putio.tv.broadcast;

import io.smileyjoe.putio.tv.object.Video;
import io.smileyjoe.putio.tv.video.VideoCache;

public interface UpdateVideoReceiver extends BroadcastReceiver{

    void update(Video video);

    @Override
    default void registerReceiver() {
        registerReceiver(Broadcast.Videos.UPDATE, (context, intent) -> {
            Video video = intent.getParcelableExtra(Broadcast.Videos.EXTRA_VIDEO);
            VideoCache.getInstance().update(video);
            update(video);
        });
    }
}
