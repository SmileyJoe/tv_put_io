package io.smileyjoe.putio.tv.util;

import android.content.Context;

import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;
import java.util.stream.Collectors;

import io.smileyjoe.putio.tv.db.AppDatabase;
import io.smileyjoe.putio.tv.interfaces.Folder;
import io.smileyjoe.putio.tv.network.Putio;
import io.smileyjoe.putio.tv.network.Response;
import io.smileyjoe.putio.tv.object.Directory;
import io.smileyjoe.putio.tv.object.Group;
import io.smileyjoe.putio.tv.object.HistoryItem;
import io.smileyjoe.putio.tv.object.Video;
import io.smileyjoe.putio.tv.object.VirtualDirectory;

public class VideoLoader {

    public interface Listener extends PutioHelper.Listener {
        void onVideosLoadStarted();
        void onVideosLoadFinished(HistoryItem item, ArrayList<Video> videos, ArrayList<Folder> folders, boolean shouldAddToHistory);
    }

    private Context mContext;
    private HashMap<Long, ArrayList<Video>> mVideos;
    private HashMap<Long, ArrayList<Folder>> mFolders;
    private HashMap<Long, Video> mParents;
    private ArrayList<HistoryItem> mHistory;
    private Optional<Listener> mListener = Optional.empty();
    private static VideoLoader sInstance;

    private static VideoLoader getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new VideoLoader(context);
        }

        return sInstance;
    }

    public static VideoLoader getInstance(Context context, Listener listener) {
        if (sInstance == null) {
            sInstance = new VideoLoader(context);
        }

        sInstance.setListener(listener);

        return sInstance;
    }

    private VideoLoader(Context context) {
        mContext = context;
        mVideos = new HashMap<>();
        mFolders = new HashMap<>();
        mParents = new HashMap<>();
        mHistory = new ArrayList<>();
    }

    public void setListener(Listener listener) {
        mListener = Optional.ofNullable(listener);
    }

    public Video getVideo(HistoryItem historyItem) {
        if (historyItem != null && mParents.containsKey(historyItem.getId())) {
            return mParents.get(historyItem.getId());
        }

        return null;
    }

    public Video getParent() {
        if (mHistory != null && mHistory.size() > 0) {
            HistoryItem historyItem = mHistory.get(mHistory.size() - 1);
            return getVideo(historyItem);
        }

        return null;
    }

    public static void update(Context context, Video updateVideo){
        // this is so we don't have to expose the class without a listener attached //
        getInstance(context).update(updateVideo);
    }

    public void update(Video updateVideo) {
        mVideos.entrySet().stream()
                .forEach(entry -> {
                    mVideos.put(entry.getKey(), entry.getValue().stream()
                            .map(video -> video.getPutId() == updateVideo.getPutId() ? updateVideo : video)
                            .collect(Collectors.toCollection(ArrayList::new)));
                });

        mParents.entrySet().stream()
                .filter(entry -> entry.getValue().getPutId() == updateVideo.getPutId())
                .forEach(entry -> mParents.put(entry.getKey(), updateVideo));
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

    public void loadDirectory() {
        mHistory = new ArrayList<>();
        getFromPut(Putio.Files.NO_PARENT);
    }

    public void loadDirectory(Long putId, String title) {
        loadDirectory(putId, title, true);
    }

    public void loadDirectory(Long putId, String title, boolean shouldAddToHistory) {
        ArrayList<Video> videos = getVideos(putId);

        if (videos == null) {
            getFromPut(putId);
        } else {
            onVideosLoaded(HistoryItem.directory(putId, title), videos, getFolders(putId), shouldAddToHistory);
        }
    }

    public void loadGroup(Integer id) {
        loadGroup(new Long(id), true);
    }

    public void loadGroup(Long id) {
        loadGroup(id, true);
    }

    public void loadGroup(Long id, boolean shouldAddToHistory) {
        mListener.ifPresent(listener -> listener.onVideosLoadStarted());
        new GetGroup(id, shouldAddToHistory).run();
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

    public boolean hasHistory() {
        if (mHistory == null || mHistory.size() <= 1) {
            return false;
        } else {
            return true;
        }
    }

    public HistoryItem getCurrentHistory() {
        return mHistory.get(mHistory.size() - 1);
    }

    private ArrayList<Video> getVideos(long putId) {
        return mVideos.get(putId);
    }

    private ArrayList<Folder> getFolders(long putId) {
        return mFolders.get(putId);
    }

    private void onVideosLoaded(HistoryItem item, ArrayList<Video> videos, ArrayList<Folder> folders, boolean shouldAddToHistory) {
        ArrayList<Folder> tempFolders = new ArrayList<>(folders);
        if (item.getId() == Putio.Files.NO_PARENT) {
            if (Settings.getInstance(mContext).shouldShowRecentlyAdded()) {
                tempFolders.add(0, VirtualDirectory.getRecentAdded(mContext));
            }
            Async.run(() -> AppDatabase.getInstance(mContext).groupDao().getEnabled(), groups -> {
                if (groups != null && !groups.isEmpty()) {
                    groups.forEach(group -> tempFolders.add(0, group));
                }
                mListener.ifPresent(listener -> listener.onVideosLoadFinished(item, videos, tempFolders, shouldAddToHistory));
            });
        } else {
            mListener.ifPresent(listener -> listener.onVideosLoadFinished(item, videos, tempFolders, shouldAddToHistory));
        }
    }

    private void getFromPut(long putId) {
        mListener.ifPresent(listener -> listener.onVideosLoadStarted());
        Putio.Files.get(mContext, putId, new OnPutResponse(putId));
    }

    public void addToHistory(HistoryItem item) {
        mHistory.add(item);
    }

    private class GetGroup extends Async.Runner<Void> {
        private Long mId;
        private Group mGroup;
        private ArrayList<Video> mGroupVideos;
        private ArrayList<Folder> mGroupFolders;
        private boolean mShouldAddToHistory;

        public GetGroup(Long id, boolean shouldAddToHistory) {
            mGroupFolders = new ArrayList<>();
            mGroupVideos = new ArrayList<>();
            mId = id;
            mShouldAddToHistory = shouldAddToHistory;
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
                ArrayList<Video> videos = getVideos(id);
                ArrayList<Folder> folders = getFolders(id);

                if (videos == null) {
                    PutioHelper helper = new PutioHelper(mContext);
                    mListener.ifPresent(helper::setListener);

                    JsonObject result = Putio.Files.get(mContext, id);

                    helper.parse(id, result);

                    videos = helper.getVideos();
                    folders = helper.getFolders();

                    mParents.put(id, helper.getCurrent());
                    mVideos.put(id, videos);
                    mFolders.put(id, folders);
                }

                mGroupVideos.addAll(videos);
                mGroupFolders.addAll(folders.stream()
                        .filter(folder -> !putIds.contains(((Directory) folder).getPutId()))
                        .collect(Collectors.toList()));
            }
        }

        private void handleParents(ArrayList<Long> putIds) {
            for (long id : putIds) {
                Video parent = mParents.get(id);

                if (parent == null) {
                    PutioHelper helper = new PutioHelper(mContext);
                    mListener.ifPresent(helper::setListener);

                    JsonObject result = Putio.Files.get(mContext, id);

                    helper.parse(id, result);

                    parent = helper.getCurrent();
                    mParents.put(id, parent);
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
            onVideosLoaded(HistoryItem.group(mId, mGroup.getTitle()), mGroupVideos, mGroupFolders, mShouldAddToHistory);
        }
    }

    private class OnPutResponse extends Response {

        private long mPutId;

        public OnPutResponse(long putId) {
            mPutId = putId;
        }

        @Override
        public void onSuccess(JsonObject result) {
            new ProcessPutResponse(mPutId, result).run();
        }
    }

    private class ProcessPutResponse extends Async.Runner<Void> {

        private long mPutId;
        private JsonObject mResult;
        private long mCurrentPutId;
        private String mCurrentTitle;

        public ProcessPutResponse(long putId, JsonObject result) {
            mPutId = putId;
            mResult = result;
        }

        @Override
        protected Void onBackground() {
            PutioHelper helper = new PutioHelper(mContext);
            mListener.ifPresent(helper::setListener);
            helper.parse(mPutId, mResult);

            mCurrentPutId = helper.getCurrent().getPutId();
            mCurrentTitle = helper.getCurrent().getTitleFormatted(mContext, true);

            mParents.put(mCurrentPutId, helper.getCurrent());
            mVideos.put(mCurrentPutId, helper.getVideos());
            mFolders.put(mCurrentPutId, helper.getFolders());

            return null;
        }

        @Override
        protected void onMain(Void param) {
            VirtualDirectory virtual = VirtualDirectory.getFromPutId(mContext, mCurrentPutId);
            HistoryItem historyItem;

            if (virtual == null) {
                historyItem = HistoryItem.directory(mCurrentPutId, mCurrentTitle);
            } else {
                historyItem = HistoryItem.virtualDirectory(mCurrentPutId, mCurrentTitle);
            }

            onVideosLoaded(historyItem, mVideos.get(mCurrentPutId), mFolders.get(mCurrentPutId), true);
        }
    }


}
