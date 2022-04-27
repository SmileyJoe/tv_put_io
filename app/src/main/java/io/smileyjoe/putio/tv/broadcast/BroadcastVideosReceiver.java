package io.smileyjoe.putio.tv.broadcast;

import java.util.ArrayList;

import io.smileyjoe.putio.tv.db.AppDatabase;
import io.smileyjoe.putio.tv.interfaces.Folder;
import io.smileyjoe.putio.tv.network.Putio;
import io.smileyjoe.putio.tv.object.HistoryItem;
import io.smileyjoe.putio.tv.object.Video;
import io.smileyjoe.putio.tv.object.VirtualDirectory;
import io.smileyjoe.putio.tv.util.Async;
import io.smileyjoe.putio.tv.util.Settings;

public interface BroadcastVideosReceiver extends Base {

    void update(Video video);
    void onVideosLoadStarted();
    void onVideosLoadFinished(HistoryItem item, ArrayList<Video> videos, ArrayList<Folder> folders, boolean shouldAddToHistory);

    default void onResume() {
        registerReceiver(Broadcast.Videos.STARTED, (context, intent) -> onVideosLoadStarted());

        registerReceiver(Broadcast.Videos.LOADED, (context, intent) -> {

            HistoryItem historyItem = intent.getParcelableExtra(Broadcast.Videos.EXTRA_HISTORY);
            ArrayList<Video> videos = intent.getParcelableArrayListExtra(Broadcast.Videos.EXTRA_VIDEOS);
            ArrayList<Folder> folders = intent.getParcelableArrayListExtra(Broadcast.Videos.EXTRA_FOLDERS);
            boolean shouldAddToHistory = intent.getBooleanExtra(Broadcast.Videos.EXTRA_SHOULD_ADD_HISTORY, false);

            if (historyItem.getId() == Putio.Files.NO_PARENT) {
                if (Settings.getInstance(getBaseContext()).shouldShowRecentlyAdded()) {
                    folders.add(0, VirtualDirectory.getRecentAdded(getBaseContext()));
                }
                Async.run(() -> AppDatabase.getInstance(getBaseContext()).groupDao().getEnabled(), groups -> {
                    if (groups != null && !groups.isEmpty()) {
                        groups.forEach(group -> folders.add(0, group));
                    }

                    onVideosLoadFinished(
                            historyItem,
                            videos,
                            folders,
                            shouldAddToHistory);
                });
            } else {
                onVideosLoadFinished(
                        historyItem,
                        videos,
                        folders,
                        shouldAddToHistory);
            }
        });

        registerReceiver(Broadcast.Videos.UPDATE, (context, intent) -> update(
                intent.getParcelableExtra(Broadcast.Videos.EXTRA_VIDEO)
        ));
    }

}
