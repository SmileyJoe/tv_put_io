package io.smileyjoe.putio.tv.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import java.util.ArrayList;

import io.smileyjoe.putio.tv.interfaces.Folder;
import io.smileyjoe.putio.tv.object.HistoryItem;
import io.smileyjoe.putio.tv.object.Video;

public interface BroadcastVideosReceiver extends Base{

    void update(Video video);
    void onVideosLoadStarted();
    void onVideosLoadFinished(HistoryItem item, ArrayList<Video> videos, ArrayList<Folder> folders, boolean shouldAddToHistory);

    default void onResume(){
        registerReceiver(Broadcast.Videos.STARTED, (context, intent) -> onVideosLoadStarted());

        registerReceiver(Broadcast.Videos.LOADED, (context, intent) -> onVideosLoadFinished(
                intent.getParcelableExtra(Broadcast.Videos.EXTRA_HISTORY),
                intent.getParcelableArrayListExtra(Broadcast.Videos.EXTRA_VIDEOS),
                intent.getParcelableArrayListExtra(Broadcast.Videos.EXTRA_FOLDERS),
                intent.getBooleanExtra(Broadcast.Videos.EXTRA_SHOULD_ADD_HISTORY, false)));

        registerReceiver(Broadcast.Videos.UPDATE, (context, intent) -> update(
                intent.getParcelableExtra(Broadcast.Videos.EXTRA_VIDEO)
        ));
    }

}
