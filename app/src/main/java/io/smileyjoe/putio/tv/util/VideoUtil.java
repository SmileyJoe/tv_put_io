package io.smileyjoe.putio.tv.util;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;

import io.smileyjoe.putio.tv.comparator.VideoComparator;
import io.smileyjoe.putio.tv.db.AppDatabase;
import io.smileyjoe.putio.tv.object.FileType;
import io.smileyjoe.putio.tv.object.Filter;
import io.smileyjoe.putio.tv.object.Video;
import io.smileyjoe.putio.tv.object.VideoType;
import io.smileyjoe.putio.tv.torrent.Parse;

public class VideoUtil {

    private VideoUtil() {

    }

    public static Video getFromDbByPutId(Context context, long putId){
        AppDatabase db = AppDatabase.getInstance(context);
        Video currentDbVideo = db.videoDao().getByPutId(putId);

        if(currentDbVideo != null && currentDbVideo.getTmdbId() > 0){
            currentDbVideo.setCharacters(new ArrayList<>(db.characterDao().getByTmdbId(currentDbVideo.getTmdbId())));
        }

        return currentDbVideo;
    }

    public static ArrayList<Video> filter(ArrayList<Video> videos) {
        ArrayList<Video> videosFiltered = new ArrayList<>();

        for (Video video : videos) {
            if (video.getFileType() != FileType.UNKNOWN && video.getSize() > 0) {
                videosFiltered.add(video);
            }
        }

        return videosFiltered;
    }

    public static void sort(ArrayList<Video> videos) {
        sort(videos, VideoComparator.Order.ALPHABETICAL);
    }

    public static void sort(ArrayList<Video> videos, Filter filter) {
        sort(videos, VideoComparator.Order.fromFilter(filter));
    }

    public static void sort(ArrayList<Video> videos, VideoComparator.Order order) {
        Collections.sort(videos, new VideoComparator(order));
    }

    public static Video parseFromPut(Context context, JsonObject jsonObject) {
        JsonUtil json = new JsonUtil(jsonObject);
        long putId = json.getLong("id");
        boolean hasTmdbData = false;

        Video video = VideoUtil.getFromDbByPutId(context, putId);

        if(video != null){
            hasTmdbData = video.isTmdbFound();
        } else {
            video = new Video();
        }

        if(!hasTmdbData){
            video.setTitle(json.getString("name"));
            video.setPutId(json.getLong("id"));
            video.setBackdrop(json.getString("screenshot"));
            video.setPoster(json.getString("screenshot"));
        }

        video.setConverted(!json.getBoolean("need_convert", false));
        video.setFileType(json.getString("file_type"));
        video.setStreamUri(json.getString("stream_url"), json.getString("mp4_stream_url"));
        video.setSize(json.getLong("size"));
        video.setCreatedAt(json.getString("created_at"));
        video.setUpdatedAt(json.getString("updated_at"));
        video.setResumeTime(json.getLong("start_from"));

        String firstAccessedAt = json.getString("first_accessed_at");
        video.setWatched(!TextUtils.isEmpty(firstAccessedAt));

        video = Parse.update(video);

        return video;
    }

    public static long getMillies(String putDate){
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            Date date = formatter.parse(putDate);
            return date.getTime();
        } catch (ParseException | NullPointerException e){
            return -1;
        }
    }

    public static String getFormatted(long millies){
        SimpleDateFormat formatter = new SimpleDateFormat("dd MMMM yyyy");
        return formatter.format(new Date(millies));
    }

    public static ArrayList<Video> parseFromPut(Context context, JsonArray jsonArray) {
        ArrayList<Video> videos = new ArrayList<>();

        for (JsonElement jsonElement : jsonArray) {
            videos.add(parseFromPut(context, jsonElement.getAsJsonObject()));
        }

        return videos;
    }

    public static Video updateFromDb(Video putVideo, Video dbVideo){
        putVideo.setTmdbId(dbVideo.getTmdbId());
        putVideo.setBackdrop(dbVideo.getBackdrop());
        putVideo.setOverView(dbVideo.getOverView());
        putVideo.setPoster(dbVideo.getPoster());
        putVideo.setTitle(dbVideo.getTitle());
        putVideo.isTmdbFound(true);
        putVideo.isTmdbChecked(true);
        putVideo.setGenreIds(dbVideo.getGenreIds());
        putVideo.setRuntime(dbVideo.getRuntime());
        putVideo.setTagLine(dbVideo.getTagLine());
        return putVideo;
    }

    public static ArrayList<Video> getRelated(Video videoPrimary, ArrayList<Video> videoList){
        switch (videoPrimary.getVideoType()){
            case EPISODE:
                return getRelatedSeries(videoPrimary, videoList);
            case MOVIE:
                return getRelatedMovie(videoPrimary, videoList);
            case UNKNOWN:
            default:
                return videoList;
        }
    }

    private static ArrayList<Video> getRelatedSeries(Video videoPrimary, ArrayList<Video> videoList){
        ArrayList<Video> relatedVideos = new ArrayList<>();

        for(Video video:videoList) {
            if (video.getPutId() != videoPrimary.getPutId()
                    && video.getVideoType() == VideoType.EPISODE
                    && video.getTitle().equalsIgnoreCase(videoPrimary.getTitle())) {
                relatedVideos.add(video);
            }
        }

        return relatedVideos;
    }

    private static ArrayList<Video> getRelatedMovie(Video videoPrimary, ArrayList<Video> videoList){
        ArrayList<Video> relatedVideos = new ArrayList<>();
        HashMap<Integer, ArrayList<Video>> relatedVideosMap = new HashMap<>();

        for(Video video:videoList){
            if(video.getPutId() != videoPrimary.getPutId() && video.getVideoType() == VideoType.MOVIE) {
                int count = 0;
                for (int genreId : video.getGenreIds()) {
                    if (videoPrimary.getGenreIds().contains(genreId)) {
                        count++;
                    }
                }

                if (count > 0) {
                    ArrayList<Video> videosFromMap;

                    if (relatedVideosMap.containsKey(count)) {
                        videosFromMap = relatedVideosMap.get(count);
                    } else {
                        videosFromMap = new ArrayList<>();
                    }

                    videosFromMap.add(video);

                    relatedVideosMap.put(count, videosFromMap);
                }
            }
        }

        for(int i = videoPrimary.getGenreIds().size(); i >= 1; i--){
            if(relatedVideosMap.containsKey(i)){
                relatedVideos.addAll(relatedVideosMap.get(i));
            }
        }

        return relatedVideos;
    }
}
