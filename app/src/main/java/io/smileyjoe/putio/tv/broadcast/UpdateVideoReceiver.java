package io.smileyjoe.putio.tv.broadcast;

import io.smileyjoe.putio.tv.object.Video;

public interface UpdateVideoReceiver extends BroadcastReceiver{

    void update(Video video);

    @Override
    default void registerReceiver() {
        registerReceiver(Broadcast.Videos.UPDATE, (context, intent) -> update(
                intent.getParcelableExtra(Broadcast.Videos.EXTRA_VIDEO)
        ));
    }
}
