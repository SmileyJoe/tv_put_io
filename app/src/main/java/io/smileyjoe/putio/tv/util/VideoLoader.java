package io.smileyjoe.putio.tv.util;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.HashMap;

import io.smileyjoe.putio.tv.db.AppDatabase;
import io.smileyjoe.putio.tv.network.Putio;
import io.smileyjoe.putio.tv.network.Response;
import io.smileyjoe.putio.tv.network.Tmdb;
import io.smileyjoe.putio.tv.object.Video;
import io.smileyjoe.putio.tv.object.VideoType;
import io.smileyjoe.putio.tv.ui.activity.MainActivity;

public class VideoLoader {

    public interface Listener{
        void onVideosLoadStarted();
        void onVideosLoadFinished(Video current, ArrayList<Video> videos);
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
            mHistory.add(video);
            onVideosLoaded(video, videos);
        }
    }

    public boolean back(){
        if(mHistory != null && mHistory.size() >= 2) {
            Video current = getCurrent();
            mHistory.remove(current);
            current = getCurrent();
            onVideosLoaded(current, getVideos(current.getPutId()));
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

    private void onVideosLoaded(Video current, ArrayList<Video> videos){
        mListener.onVideosLoadFinished(current, videos);
    }

    private void getFromPut(long putId){
        mListener.onVideosLoadStarted();
        Putio.getFiles(mContext, putId, new OnPutResponse());
    }

    private class OnPutResponse extends Response {
        @Override
        public void onSuccess(JsonObject result) {
            ProcessPutResponse task = new ProcessPutResponse(result);
            task.execute();
        }
    }

    private class ProcessPutResponse extends AsyncTask<Void, Void, ArrayList<Video>> {

        private JsonObject mResult;

        public ProcessPutResponse(JsonObject result) {
            mResult = result;
        }

        @Override
        protected ArrayList<Video> doInBackground(Void... params) {
            JsonArray filesJson = mResult.getAsJsonArray("files");
            JsonObject parentObject = mResult.getAsJsonObject("parent");

            ArrayList<Video> videos = VideoUtil.filter(VideoUtil.parseFromPut(mContext, filesJson));
            VideoUtil.sort(videos);
            Video current = VideoUtil.parseFromPut(mContext, parentObject);
            mHistory.add(current);

            if(videos != null && videos.size() == 1){
                Video currentDbVideo = AppDatabase.getInstance(mContext).videoDao().getByPutId(current.getPutId());

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

            mVideos.put(current.getPutId(), videos);

            return videos;
        }

        @Override
        protected void onPostExecute(ArrayList<Video> videos) {
            onVideosLoaded(getCurrent(), videos);
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
