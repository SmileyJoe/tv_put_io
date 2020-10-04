package io.smileyjoe.putio.tv.util;

import android.text.TextUtils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import io.smileyjoe.putio.tv.network.Tmdb;
import io.smileyjoe.putio.tv.object.Video;
import io.smileyjoe.putio.tv.object.VideoType;
import io.smileyjoe.putio.tv.torrent.Parse;

public class VideoUtil {

    private VideoUtil() {

    }

    public static ArrayList<Video> filter(ArrayList<Video> videos) {
        ArrayList<Video> videosFiltered = new ArrayList<>();

        for (Video video : videos) {
            if (video.getType() != VideoType.UNKNOWN) {
                videosFiltered.add(video);
            }
        }

        return videosFiltered;
    }

    public static void sort(ArrayList<Video> videos) {
        Collections.sort(videos, new VideoComparator());
    }

    public static Video parseFromPut(JsonObject jsonObject) {
        Video video = new Video();
        JsonUtil json = new JsonUtil(jsonObject);

        video.setTitle(json.getString("name"));
        video.setPutId(json.getLong("id"));
        video.setConverted(json.getBoolean("is_mp4_available", false));
        video.setType(json.getString("file_type"));
        video.setBackdrop(json.getString("screenshot"));
        video.setPoster(json.getString("screenshot"));
        video.setStreamUri(json.getString("stream_url"), json.getString("mp4_stream_url"));

        String firstAccessedAt = json.getString("first_accessed_at");
        video.setWatched(!TextUtils.isEmpty(firstAccessedAt));

        video = Parse.update(video);

        return video;
    }

    public static ArrayList<Video> parseFromPut(JsonArray jsonArray) {
        ArrayList<Video> videos = new ArrayList<>();

        for (JsonElement jsonElement : jsonArray) {
            videos.add(parseFromPut(jsonElement.getAsJsonObject()));
        }

        return videos;
    }

    public static Video updateFromTmdb(Video video, JsonArray jsonArray) {
        for (JsonElement jsonElement : jsonArray) {
            JsonUtil json = new JsonUtil(jsonElement.getAsJsonObject());

            video.setTmdbId(json.getLong("id"));
            video.setBackdrop(Tmdb.getImageUrl(json.getString("backdrop_path")));
            video.setOverView(json.getString("overview"));
            video.setPoster(Tmdb.getImageUrl(json.getString("poster_path")));
            video.setTitle(json.getString("title"));

            break;
        }

        return video;
    }

    private static class VideoComparator implements Comparator<Video> {
        @Override
        public int compare(Video videoOne, Video videoTwo) {
            int result = Integer.compare(videoOne.getType().getOrder(), videoTwo.getType().getOrder());

            if (result != 0) {
                return result;
            }

            result = videoOne.getTitle().compareTo(videoTwo.getTitle());

            if(videoOne.getType() == VideoType.EPISODE){
                result = Integer.compare(videoOne.getSeason(), videoTwo.getSeason());

                if(result != 0){
                    return result;
                }

                return Integer.compare(videoOne.getEpisode(), videoTwo.getEpisode());
            } else {
                return result;
            }
        }
    }
}
