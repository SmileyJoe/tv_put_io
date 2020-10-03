package io.smileyjoe.putio.tv.util;

import android.text.TextUtils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;

import io.smileyjoe.putio.tv.object.Video;
import io.smileyjoe.putio.tv.torrent.Parse;

public class VideoUtil {

    private VideoUtil(){

    }

    public static Video parseFromPut(JsonObject jsonObject){
        Video video = new Video();
        JsonUtil json = new JsonUtil(jsonObject);

        video.setTitle(json.getString("name"));
        video.setPutId(json.getLong("id"));
        video.setConverted(json.getBoolean("is_mp4_available", false));
        video.setType(json.getString("file_type"));
        video.setBackdrop(json.getString("screenshot"));
        video.setPoster("screenshot");
        video.setStreamUri(json.getString("stream_url"), json.getString("mp4_stream_url"));

        String firstAccessedAt = json.getString("first_accessed_at");
        video.setWatched(!TextUtils.isEmpty(firstAccessedAt));

        video = Parse.update(video);

        return video;
    }

    public static ArrayList<Video> parseFromPut(JsonArray jsonArray){
        ArrayList<Video> videos = new ArrayList<>();

        for(JsonElement jsonElement:jsonArray){
            videos.add(parseFromPut(jsonElement.getAsJsonObject()));
        }

        return videos;
    }

    public static Video updateFromTmdb(Video video, JsonArray jsonArray){
        for(JsonElement jsonElement:jsonArray){
            JsonUtil json = new JsonUtil(jsonElement.getAsJsonObject());

            video.setTmdbId(json.getLong("id"));
            video.setBackdrop(json.getString("backdrop_path"));
            video.setOverView(json.getString("overview"));
            video.setPoster(json.getString("poster_path"));
            video.setTitle(json.getString("title"));

            break;
        }

        return video;
    }
}
