package io.smileyjoe.putio.tv.util;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.smileyjoe.putio.tv.db.AppDatabase;
import io.smileyjoe.putio.tv.network.Putio;
import io.smileyjoe.putio.tv.network.Response;
import io.smileyjoe.putio.tv.network.Tmdb;
import io.smileyjoe.putio.tv.object.Group;
import io.smileyjoe.putio.tv.object.Video;
import io.smileyjoe.putio.tv.object.VideoType;
import io.smileyjoe.putio.tv.ui.activity.MainActivity;

public class VideoLoader {

    public interface Listener{
        void onVideosLoadStarted();
        void onVideosLoadFinished(Video current, ArrayList<Video> videos, boolean shouldAddToHistory);
        void update(Video video);
    }

    private Context mContext;
    private HashMap<Long, ArrayList<Video>> mVideos;
    private ArrayList<Video> mHistory;
    private Listener mListener;

    public VideoLoader(Context context, Listener listener) {
        mContext = context;
        mVideos = new HashMap<>();
        mHistory = new ArrayList<>();
        mListener = listener;
    }

    public void load(){
        getFromPut(Putio.NO_PARENT);
    }

    public void load(Video video){
        ArrayList<Video> videos = getVideos(video.getPutId());

        if(videos == null){
            getFromPut(video.getPutId());
        } else {
            onVideosLoaded(video, videos, true);
        }
    }

    public boolean back(){
        if(mHistory != null && mHistory.size() >= 2) {
            Video current = getCurrent();
            mHistory.remove(current);
            current = getCurrent();
            onVideosLoaded(current, getVideos(current.getPutId()), false);
            return true;
        }

        return false;
    }

    public Video getCurrent(){
        return mHistory.get(mHistory.size() - 1);
    }

    private ArrayList<Video> getVideos(long putId){
        return mVideos.get(putId);
    }

    private void onVideosLoaded(Video current, ArrayList<Video> videos, boolean shouldAddToHistory){
        mListener.onVideosLoadFinished(current, videos, shouldAddToHistory);
    }

    private void getFromPut(long putId){
        mListener.onVideosLoadStarted();
        Putio.getFiles(mContext, putId, new OnPutResponse(putId));
    }

    public void addToHistory(Video video){
        mHistory.add(video);
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

    private class ProcessPutResponse extends AsyncTask<Void, Void, ArrayList<Video>> {

        private long mPutId;
        private JsonObject mResult;
        private Video mCurrent;

        public ProcessPutResponse(long putId, JsonObject result) {
            mPutId = putId;
            mResult = result;
        }

        @Override
        protected ArrayList<Video> doInBackground(Void... params) {
            JsonArray filesJson = mResult.getAsJsonArray("files");
            JsonObject parentObject = mResult.getAsJsonObject("parent");

            List<Group> groups = null;

            if(mPutId == Putio.NO_PARENT){
                groups = AppDatabase.getInstance(mContext).groupDao().getAll();
            }

            ArrayList<Video> videos = VideoUtil.filter(VideoUtil.parseFromPut(mContext, filesJson));
            VideoUtil.sort(videos);
            mCurrent = VideoUtil.parseFromPut(mContext, parentObject);

            if(videos != null && videos.size() == 1){
                Video currentDbVideo = AppDatabase.getInstance(mContext).videoDao().getByPutId(mCurrent.getPutId());

                if(currentDbVideo != null && currentDbVideo.isTmdbFound()){
                    Video updated = VideoUtil.updateFromDb(videos.get(0), currentDbVideo);
                    AppDatabase.getInstance(mContext).videoDao().insert(updated);
                }
            }

            for (Video video : videos) {
                if (video.getVideoType() == VideoType.MOVIE) {
                    if(!video.isTmdbChecked()) {
                        Tmdb.searchMovie(mContext, video.getTitle(), video.getYear(), new OnTmdbSearchResponse(video));
                    }
                }
            }

            if(groups != null && !groups.isEmpty()){
                for(Group group:groups){
                    videos.add(group.toVideo());
                }
            }

            mVideos.put(mCurrent.getPutId(), videos);

            return videos;
        }

        @Override
        protected void onPostExecute(ArrayList<Video> videos) {
            onVideosLoaded(mCurrent, videos, true);
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
