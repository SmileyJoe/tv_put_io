package io.smileyjoe.putio.tv.video;

import android.content.Context;

import java.util.ArrayList;

import io.smileyjoe.putio.tv.broadcast.Broadcast;
import io.smileyjoe.putio.tv.db.AppDatabase;
import io.smileyjoe.putio.tv.network.Putio;
import io.smileyjoe.putio.tv.object.HistoryItem;
import io.smileyjoe.putio.tv.object.Video;
import io.smileyjoe.putio.tv.util.PutioHelper;

public class VideoLoader {

    private ArrayList<HistoryItem> mHistory;
    private VideoCache mCache;
    private Context mContext;

    public VideoLoader(Context context) {
        mContext = context;
        mHistory = new ArrayList<>();
        mCache = VideoCache.getInstance();
    }

    public void loadDirectory() {
        mHistory = new ArrayList<>();
        getFromPut(Putio.Files.NO_PARENT);
    }

    public void loadDirectory(Long putId, String title) {
        loadDirectory(putId, title, true);
    }

    private void loadDirectory(Long putId, String title, boolean shouldAddToHistory) {
        ArrayList<Video> videos = mCache.getVideos(putId);

        if (videos == null) {
            getFromPut(putId);
        } else {
            Broadcast.Videos.loaded(mContext, HistoryItem.directory(putId, title), videos, mCache.getFolders(putId), shouldAddToHistory);
        }
    }

    public void loadGroup(Integer id) {
        loadGroup(new Long(id), true);
    }

    private void loadGroup(Long id, boolean shouldAddToHistory) {
        Broadcast.Videos.loadStarted(mContext);
        new GetGroup(mContext, id, shouldAddToHistory).run();
    }

    public void refresh(Video video) {
        AppDatabase.getInstance(mContext).videoDao().delete(video.getPutId());
        PutioHelper helper = new PutioHelper(mContext);
        helper.parse(video.getPutId(), video.getParentTmdbId(), Putio.Files.get(mContext, video.getPutId()));
    }

    public boolean back() {
        if (mHistory != null && mHistory.size() >= 2) {
            HistoryItem current = getCurrentHistory();
            mHistory.remove(current);
            current = getCurrentHistory();

            switch (current.getFolderType()) {
                case DIRECTORY:
                    loadDirectory(current.getId(), current.getTitle(), false);
                    break;
                case GROUP:
                    loadGroup(current.getId(), false);
                    break;
            }
            return true;
        }

        return false;
    }

    public void reload() {
        if (mHistory != null && !mHistory.isEmpty()) {
            HistoryItem current = getCurrentHistory();
            switch (current.getFolderType()) {
                case DIRECTORY:
                    loadDirectory(current.getId(), current.getTitle(), false);
                    break;
                case GROUP:
                    loadGroup(current.getId(), false);
                    break;
            }
        }
    }

    private void getFromPut(long putId) {
        Broadcast.Videos.loadStarted(mContext);
        Putio.Files.get(mContext, putId, new OnPutResponse(mContext, putId));
    }

    /**
     * History
     */

    public boolean hasHistory() {
        if (mHistory == null || mHistory.size() <= 1) {
            return false;
        } else {
            return true;
        }
    }

    public void addToHistory(HistoryItem item) {
        mHistory.add(item);
    }

    public HistoryItem getCurrentHistory() {
        return mHistory.get(mHistory.size() - 1);
    }

}
