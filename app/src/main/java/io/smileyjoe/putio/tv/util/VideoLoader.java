package io.smileyjoe.putio.tv.util;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

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
import io.smileyjoe.putio.tv.object.HistoryItem;
import io.smileyjoe.putio.tv.object.Video;

public class VideoLoader {

    public interface Listener extends PutioHelper.Listener{
        void onVideosLoadStarted();
        void onVideosLoadFinished(HistoryItem item, ArrayList<Video> videos, ArrayList<Folder> folders, boolean shouldAddToHistory);
    }

    private Context mContext;
    private HashMap<Long, ArrayList<Video>> mVideos;
    private HashMap<Long, ArrayList<Folder>> mFolders;
    private ArrayList<HistoryItem> mHistory;
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
        load(putId, true);
    }

    public void load(Long putId, boolean shouldAddToHistory){
        ArrayList<Video> videos = getVideos(putId);

        if(videos == null){
            getFromPut(putId);
        } else {
            HistoryItem item = new HistoryItem();
            item.setPutId(putId);
            onVideosLoaded(item, videos, getFolders(putId), shouldAddToHistory);
        }
    }

    public void load(ArrayList<Long> putIds){
        load(putIds, true);
    }

    public void load(ArrayList<Long> putIds, boolean shouldAddToHistory){
        mListener.onVideosLoadStarted();
        GetFromPut task = new GetFromPut(putIds, shouldAddToHistory);
        task.execute();
    }

    public boolean back(){
        if(mHistory != null && mHistory.size() >= 2) {
            HistoryItem current = getCurrentHistory();
            mHistory.remove(current);
            current = getCurrentHistory();

            if(current.isGroup()) {
                load(current.getPutIds(), false);
            } else {
                load(current.getPutId(), false);
            }
            return true;
        }

        return false;
    }

    public HistoryItem getCurrentHistory(){
        return mHistory.get(mHistory.size() - 1);
    }

    private ArrayList<Video> getVideos(long putId){
        return mVideos.get(putId);
    }

    private ArrayList<Folder> getFolders(long putId){
        return mFolders.get(putId);
    }

    private void onVideosLoaded(HistoryItem item, ArrayList<Video> videos, ArrayList<Folder> folders, boolean shouldAddToHistory){
        mListener.onVideosLoadFinished(item, videos, folders, shouldAddToHistory);
    }

    private void getFromPut(long putId){
        mListener.onVideosLoadStarted();
        Putio.getFiles(mContext, putId, new OnPutResponse(putId));
    }

    public void addToHistory(HistoryItem item){
        mHistory.add(item);
    }

    private class GetFromPut extends AsyncTask<Void, Void, Void>{
        private ArrayList<Long> mPutIds;
        private ArrayList<Video> mGroupVideos;
        private ArrayList<Folder> mGroupFolders;
        private boolean mShouldAddToHistory;

        public GetFromPut(ArrayList<Long> putIds, boolean shouldAddToHistory) {
            mGroupFolders = new ArrayList<>();
            mGroupVideos = new ArrayList<>();
            mPutIds = putIds;
            mShouldAddToHistory = shouldAddToHistory;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            if(mPutIds != null && !mPutIds.isEmpty()) {
                PutioHelper helper;
                for (long id:mPutIds){
                    ArrayList<Video> videos = getVideos(id);
                    ArrayList<Folder> folders = getFolders(id);

                    if(videos == null) {
                        helper = new PutioHelper(mContext);
                        helper.setListener(mListener);

                        JsonObject result = Putio.getFiles(mContext, id);
                        helper.parse(id, result);

                        videos = helper.getVideos();
                        folders = helper.getFolders();

                        mVideos.put(id, videos);
                        mFolders.put(id, folders);
                    }

                    ArrayList<Folder> foldersClean = new ArrayList<>();

                    for(Folder folder:folders){
                        if(!mPutIds.contains(((Directory) folder).getPutId())){
                            foldersClean.add(folder);
                        }
                    }

                    mGroupVideos.addAll(videos);
                    mGroupFolders.addAll(foldersClean);
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            HistoryItem item = new HistoryItem();
            item.setPutIds(mPutIds);
            onVideosLoaded(item, mGroupVideos, mGroupFolders, mShouldAddToHistory);
        }
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
        private long mCurrentPutId;

        public ProcessPutResponse(long putId, JsonObject result) {
            mPutId = putId;
            mResult = result;
        }

        @Override
        protected Void doInBackground(Void... params) {
            PutioHelper helper = new PutioHelper(mContext);
            helper.setListener(mListener);
            helper.parse(mPutId, mResult);

            mCurrentPutId = helper.getCurrent().getPutId();

            mVideos.put(mCurrentPutId, helper.getVideos());
            mFolders.put(mCurrentPutId, helper.getFolders());

            return null;
        }

        @Override
        protected void onPostExecute(Void param) {
            HistoryItem item = new HistoryItem();
            item.setPutId(mCurrentPutId);
            onVideosLoaded(item, mVideos.get(mCurrentPutId), mFolders.get(mCurrentPutId), true);
        }
    }


}
