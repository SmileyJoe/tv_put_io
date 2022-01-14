package io.smileyjoe.putio.tv.action.video;

import io.smileyjoe.putio.tv.network.Tmdb;
import io.smileyjoe.putio.tv.object.Video;

public interface Refresh extends Action{

    void update(Video video);

    default void refreshData(){
        Tmdb.update(getBaseContext(), getVideo(), updatedVideo -> update(updatedVideo));
    }

    @Override
    default void setupActions(){
        addAction(ActionOption.REFRESH_DATA, true);
    }

    @Override
    default void handleClick(ActionOption option){
        refreshData();
    }

}
