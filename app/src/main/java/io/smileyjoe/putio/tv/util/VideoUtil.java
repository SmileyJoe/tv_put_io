package io.smileyjoe.putio.tv.util;

import android.content.Context;
import android.text.TextUtils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import io.smileyjoe.putio.tv.comparator.VideoComparator;
import io.smileyjoe.putio.tv.db.AppDatabase;
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

    public static Video parseFromPut(Context context, JsonObject jsonObject) {
        JsonUtil json = new JsonUtil(jsonObject);
        long putId = json.getLong("id");
        boolean hasTmdbData = false;

        Video video = AppDatabase.getInstance(context).videoDao().getByPutId(putId);

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
        video.setType(json.getString("file_type"));
        video.setStreamUri(json.getString("stream_url"), json.getString("mp4_stream_url"));
        video.setSize(json.getLong("size"));

        String firstAccessedAt = json.getString("first_accessed_at");
        video.setWatched(!TextUtils.isEmpty(firstAccessedAt));

        video = Parse.update(video);

        return video;
    }

    public static ArrayList<Video> parseFromPut(Context context, JsonArray jsonArray) {
        ArrayList<Video> videos = new ArrayList<>();

        for (JsonElement jsonElement : jsonArray) {
            videos.add(parseFromPut(context, jsonElement.getAsJsonObject()));
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
            video.isTmdbFound(true);

            JsonArray genreJson = json.getJsonArray("genre_ids");

            if(genreJson != null) {
                ArrayList<Integer> genreIds = new ArrayList<>();
                for (JsonElement genreElement : genreJson) {
                    genreIds.add(genreElement.getAsInt());
                }
                video.setGenreIds(genreIds);
            }

            break;
        }

        video.isTmdbChecked(true);

        return video;
    }
}
