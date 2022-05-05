package io.smileyjoe.putio.tv.video;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Collectors;

import io.smileyjoe.putio.tv.interfaces.Folder;
import io.smileyjoe.putio.tv.object.Video;

public class VideoCache {

    private HashMap<Long, ArrayList<Video>> mVideos;
    private HashMap<Long, ArrayList<Folder>> mFolders;
    private HashMap<Long, Video> mParents;
    private static VideoCache sInstance;

    public static VideoCache getInstance() {
        if (sInstance == null) {
            sInstance = new VideoCache();
        }

        return sInstance;
    }

    private VideoCache() {
        mVideos = new HashMap<>();
        mFolders = new HashMap<>();
        mParents = new HashMap<>();
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

    public void add(long currentId, ArrayList<Video> videos, ArrayList<Folder> folders, Video parent) {
        addParent(currentId, parent);
        mVideos.put(currentId, videos);
        mFolders.put(currentId, folders);
    }

    public void addParent(long id, Video parent) {
        mParents.put(id, parent);
    }

    public ArrayList<Video> getVideos(long putId) {
        return mVideos.get(putId);
    }

    public ArrayList<Folder> getFolders(long putId) {
        return mFolders.get(putId);
    }

    public Video getParent(long putId) {
        return mParents.get(putId);
    }

}
