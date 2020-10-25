package io.smileyjoe.putio.tv.util;

import android.content.Context;
import android.os.AsyncTask;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import io.smileyjoe.putio.tv.comparator.FolderComparator;
import io.smileyjoe.putio.tv.db.AppDatabase;
import io.smileyjoe.putio.tv.interfaces.Folder;
import io.smileyjoe.putio.tv.network.Putio;
import io.smileyjoe.putio.tv.network.Response;
import io.smileyjoe.putio.tv.network.Tmdb;
import io.smileyjoe.putio.tv.object.Directory;
import io.smileyjoe.putio.tv.object.Group;
import io.smileyjoe.putio.tv.object.Video;

public class VideoLoader {

    public interface Listener{
        void onVideosLoadStarted();
        void onVideosLoadFinished(Long currentPutId, ArrayList<Video> videos, ArrayList<Folder> folders, boolean shouldAddToHistory);
        void update(Video video);
    }

    private Context mContext;
    private HashMap<Long, ArrayList<Video>> mVideos;
    private HashMap<Long, ArrayList<Folder>> mFolders;
    private ArrayList<Long> mHistory;
    private Listener mListener;

    public VideoLoader(Context context, Listener listener) {
        mContext = context;
        mVideos = new HashMap<>();
        mFolders = new HashMap<>();
        mHistory = new ArrayList<>();
        mListener = listener;
    }

    public void load(){
        getFromPut(Putio.NO_PARENT);
    }

    public void load(Long putId){
        ArrayList<Video> videos = getVideos(putId);

        if(videos == null){
            getFromPut(putId);
        } else {
            onVideosLoaded(putId, videos, getFolders(putId), true);
        }
    }

    public boolean back(){
        if(mHistory != null && mHistory.size() >= 2) {
            Long current = getCurrentPutId();
            mHistory.remove(current);
            current = getCurrentPutId();
            onVideosLoaded(current, getVideos(current), getFolders(current), false);
            return true;
        }

        return false;
    }

    public Long getCurrentPutId(){
        return mHistory.get(mHistory.size() - 1);
    }

    private ArrayList<Video> getVideos(long putId){
        return mVideos.get(putId);
    }

    private ArrayList<Folder> getFolders(long putId){
        return mFolders.get(putId);
    }

    private void onVideosLoaded(Long current, ArrayList<Video> videos, ArrayList<Folder> folders, boolean shouldAddToHistory){
        mListener.onVideosLoadFinished(current, videos, folders, shouldAddToHistory);
    }

    private void getFromPut(long putId){
        mListener.onVideosLoadStarted();
        Putio.getFiles(mContext, putId, new OnPutResponse(putId));
    }

    public void addToHistory(Long currentPutId){
        mHistory.add(currentPutId);
    }

    private class OnPutResponse extends Response {

        private long mPutId;

        public OnPutResponse(long putId) {
            mPutId = putId;
        }

        @Override
        public void onSuccess(JsonObject result) {
            ProcessPutResponse task = new ProcessPutResponse(mPutId, result);
            task.execute();
        }
    }

    private class ProcessPutResponse extends AsyncTask<Void, Void, Void> {

        private long mPutId;
        private JsonObject mResult;
        private Video mCurrent;

        public ProcessPutResponse(long putId, JsonObject result) {
            mPutId = putId;
            mResult = result;
        }

        // todo: This needs to split folders and videos and add groups //

        @Override
        protected Void doInBackground(Void... params) {
            ArrayList<Folder> folders = new ArrayList<>();
            ArrayList<Video> videosSorted = new ArrayList<>();

            JsonArray filesJson = mResult.getAsJsonArray("files");
            JsonObject parentObject = mResult.getAsJsonObject("parent");

            if(mPutId == Putio.NO_PARENT){
                List<Group> groups = AppDatabase.getInstance(mContext).groupDao().getAll();

                if(groups != null && !groups.isEmpty()){
                    for(Group group:groups){
                        folders.add(group);
                    }
                }
            }

            ArrayList<Video> videos = VideoUtil.filter(VideoUtil.parseFromPut(mContext, filesJson));
            mCurrent = VideoUtil.parseFromPut(mContext, parentObject);

            if(videos != null && videos.size() == 1){
                Video currentDbVideo = AppDatabase.getInstance(mContext).videoDao().getByPutId(mCurrent.getPutId());

                if(currentDbVideo != null && currentDbVideo.isTmdbFound()){
                    Video updated = VideoUtil.updateFromDb(videos.get(0), currentDbVideo);
                    AppDatabase.getInstance(mContext).videoDao().insert(updated);
                }
            }

            for (Video video : videos) {
                switch (video.getVideoType()) {
                    case MOVIE:
                        if(!video.isTmdbChecked()) {
                            Tmdb.searchMovie(mContext, video.getTitle(), video.getYear(), new OnTmdbSearchResponse(video));
                        }
                    case EPISODE:
                        videosSorted.add(video);
                        break;
                    case UNKNOWN:
                        folders.add(new Directory(video));
                        break;
                }

            }

            VideoUtil.sort(videos);
            Collections.sort(folders, new FolderComparator());
            mVideos.put(mCurrent.getPutId(), videosSorted);
            mFolders.put(mCurrent.getPutId(), folders);

            return null;
        }

        @Override
        protected void onPostExecute(Void param) {
            onVideosLoaded(mCurrent.getPutId(), mVideos.get(mCurrent.getPutId()), mFolders.get(mCurrent.getPutId()), true);
        }
    }

    private class OnTmdbSearchResponse extends Response {

        private Video mVideo;

        public OnTmdbSearchResponse(Video video) {
            mVideo = video;
        }

        @Override
        public void onSuccess(JsonObject result) {
            ProcessTmdbResponse task = new ProcessTmdbResponse(mVideo, result);
            task.execute();
        }
    }

    private class ProcessTmdbResponse extends AsyncTask<Void, Void, Video>{
        private JsonObject mResult;
        private Video mVideo;

        public ProcessTmdbResponse(Video video, JsonObject result) {
            mVideo = video;
            mResult = result;
        }

        @Override
        protected Video doInBackground(Void... voids) {
            VideoUtil.updateFromTmdb(mVideo, mResult.get("results").getAsJsonArray());

            AppDatabase.getInstance(mContext).videoDao().insert(mVideo);
            return mVideo;
        }

        @Override
        protected void onPostExecute(Video video) {
            mListener.update(video);
        }
    }
}
