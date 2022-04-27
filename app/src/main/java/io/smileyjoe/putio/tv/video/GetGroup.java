package io.smileyjoe.putio.tv.video;

import android.content.Context;

import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.stream.Collectors;

import io.smileyjoe.putio.tv.broadcast.Broadcast;
import io.smileyjoe.putio.tv.db.AppDatabase;
import io.smileyjoe.putio.tv.interfaces.Folder;
import io.smileyjoe.putio.tv.network.Putio;
import io.smileyjoe.putio.tv.object.Directory;
import io.smileyjoe.putio.tv.object.Group;
import io.smileyjoe.putio.tv.object.HistoryItem;
import io.smileyjoe.putio.tv.object.Video;
import io.smileyjoe.putio.tv.util.Async;
import io.smileyjoe.putio.tv.util.PutioHelper;

public class GetGroup extends Async.Runner<Void> {

    private Context mContext;
    private Long mId;
    private Group mGroup;
    private ArrayList<Video> mGroupVideos;
    private ArrayList<Folder> mGroupFolders;
    private boolean mShouldAddToHistory;
    private VideoCache mCache;

    public GetGroup(Context context, Long id, boolean shouldAddToHistory) {
        mContext = context;
        mGroupFolders = new ArrayList<>();
        mGroupVideos = new ArrayList<>();
        mId = id;
        mShouldAddToHistory = shouldAddToHistory;
        mCache = VideoCache.getInstance();
    }

    @Override
    protected Void onBackground() {
        mGroup = AppDatabase.getInstance(mContext).groupDao().get(mId);
        ArrayList<Long> putIds = mGroup.getPutIds();

        if (putIds != null && !putIds.isEmpty()) {
            if (mGroup.isUseParent()) {
                handleParents(putIds);
            } else {
                handleVideos(putIds);
            }
        }

        return null;
    }

    private void handleVideos(ArrayList<Long> putIds) {
        for (long id : putIds) {
            ArrayList<Video> videos = mCache.getVideos(id);
            ArrayList<Folder> folders = mCache.getFolders(id);

            if (videos == null) {
                PutioHelper helper = new PutioHelper(mContext);
                helper.setListener(video -> Broadcast.Videos.update(mContext, video));

                JsonObject result = Putio.Files.get(mContext, id);

                helper.parse(id, result);

                videos = helper.getVideos();
                folders = helper.getFolders();

                mCache.add(id, videos, folders, helper.getCurrent());
            }

            mGroupVideos.addAll(videos);
            mGroupFolders.addAll(folders.stream()
                    .filter(folder -> !putIds.contains(((Directory) folder).getPutId()))
                    .collect(Collectors.toList()));
        }
    }

    private void handleParents(ArrayList<Long> putIds) {
        for (long id : putIds) {
            Video parent = mCache.getParent(id);

            if (parent == null) {
                PutioHelper helper = new PutioHelper(mContext);
                helper.setListener(video -> Broadcast.Videos.update(mContext, video));

                JsonObject result = Putio.Files.get(mContext, id);

                helper.parse(id, result);

                parent = helper.getCurrent();
                mCache.addParent(id, parent);
            }

            switch (parent.getFileType()) {
                case VIDEO:
                    mGroupVideos.add(parent);
                    break;
                case FOLDER:
                case UNKNOWN:
                default:
                    mGroupFolders.add(new Directory(parent));
                    break;
            }
        }
    }

    @Override
    protected void onMain(Void aVoid) {
        Broadcast.Videos.loaded(mContext, HistoryItem.group(mId, mGroup.getTitle()), mGroupVideos, mGroupFolders, mShouldAddToHistory);
    }
}
