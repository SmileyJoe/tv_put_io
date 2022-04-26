package io.smileyjoe.putio.tv.util;

import android.content.Context;
import android.text.TextUtils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import io.smileyjoe.putio.tv.db.AppDatabase;
import io.smileyjoe.putio.tv.network.Response;
import io.smileyjoe.putio.tv.network.Tmdb;
import io.smileyjoe.putio.tv.object.Character;
import io.smileyjoe.putio.tv.object.Video;
import io.smileyjoe.putio.tv.object.VideoType;

public class TmdbUtil {

    public interface Listener {
        void update(Video video);
    }

    private TmdbUtil() {
    }

    public static class OnTmdbSeriesSearchResponse extends Response {

        private Context mContext;
        private Video mVideo;
        private Listener mListener;

        public OnTmdbSeriesSearchResponse(Context context, Video video) {
            mContext = context;
            mVideo = video;
        }

        public void setListener(Listener listener) {
            mListener = listener;
        }

        @Override
        public void onSuccess(JsonObject result) {
            JsonObject jsonObject = null;
            if (result.has("results")) {
                JsonArray jsonArray = result.get("results").getAsJsonArray();

                if (jsonArray.size() > 0) {
                    jsonObject = jsonArray.get(0).getAsJsonObject();
                }
            } else {
                jsonObject = result.getAsJsonObject();
            }

            if (jsonObject != null) {
                JsonUtil json = new JsonUtil(jsonObject);

                TmdbUtil.OnTmdbResponse response = new TmdbUtil.OnTmdbResponse(mContext, mVideo);
                response.setListener(mListener);
                Tmdb.Series.get(mContext, json.getLong("id"), response);
            }
        }
    }

    public static class OnTmdbResponse extends Response {

        private Context mContext;
        private Video mVideo;
        private Listener mListener;

        public OnTmdbResponse(Context context, Video video) {
            mContext = context;
            mVideo = video;
        }

        public void setListener(Listener listener) {
            mListener = listener;
        }

        @Override
        public void onSuccess(JsonObject result) {
            ProcessTmdbResponse task = new ProcessTmdbResponse(mContext, mVideo, result);
            task.setListener(mListener);
            task.run();
        }

        @Override
        public void onFail(Exception e) {
            if(mListener != null){
                mListener.update(mVideo);
            }
        }
    }

    public static class ProcessTmdbResponse extends Async.Runner<Video> {

        private Context mContext;
        private JsonObject mResult;
        private Video mVideo;
        private Optional<Listener> mListener = Optional.empty();

        public ProcessTmdbResponse(Context context, Video video, JsonObject result) {
            mContext = context;
            mVideo = video;
            mResult = result;
        }

        public void setListener(Listener listener) {
            mListener = Optional.ofNullable(listener);
        }

        @Override
        protected Video onBackground() {
            if (mResult.has("results")) {
                update(mVideo, mResult.get("results").getAsJsonArray());
            } else {
                update(mVideo, mResult.getAsJsonObject());
            }

            AppDatabase.getInstance(mContext).videoDao().insert(mVideo);
            return mVideo;
        }

        @Override
        protected void onMain(Video video) {
            mListener.ifPresent(listener -> listener.update(video));
        }

        private void handleCast(Video video, JsonObject jsonObject) {
            if (jsonObject.has("credits")) {
                JsonObject creditsJsonObject = jsonObject.getAsJsonObject("credits");

                if (creditsJsonObject.has("cast")) {
                    ArrayList<Character> characters = new ArrayList<>();
                    JsonArray jsonArray = creditsJsonObject.getAsJsonArray("cast");

                    for (JsonElement jsonElement : jsonArray) {
                        Character character = new Character();
                        JsonUtil json = new JsonUtil(jsonElement.getAsJsonObject());

                        character.setCastMemberName(json.getString("name"));
                        character.setCastMemberTmdbId(json.getLong("id"));
                        character.setName(json.getString("character"));
                        character.setOrder(json.getInt("order"));
                        character.setProfileImage(Tmdb.Image.getUrl(json.getString("profile_path")));
                        character.setVideoTmdbId(video.getTmdbId());

                        characters.add(character);
                    }

                    if (characters != null && !characters.isEmpty()) {
                        AppDatabase.getInstance(mContext).characterDao().insert(characters);
                        video.setCharacters(characters);
                    }
                }
            }
        }

        private Video update(Video video, JsonObject jsonObject) {
            handleCast(video, jsonObject);
            JsonUtil json = new JsonUtil(jsonObject);

            video.setTmdbId(json.getLong("id"));
            video.setOverView(json.getString("overview"));

            video.setBackdrop(Tmdb.Image.getUrl(json.getString("backdrop_path")));
            video.setPoster(Tmdb.Image.getUrl(json.getString("poster_path")));

            String title = json.getStringNotEmpty("title", "name");
            if (!TextUtils.isEmpty(title)) {
                video.setTitle(title);
            }

            String releaseDate = json.getStringNotEmpty("release_date", "first_air_date", "air_date");
            if (!TextUtils.isEmpty(releaseDate)) {
                video.setReleaseDate(Format.fromTmdbToMillies(releaseDate));
            }

            video.setTagLine(json.getString("tagline"));
            video.setRuntime(json.getInt("runtime"));
            video.isTmdbFound(true);
            video.isTmdbChecked(true);

            JsonArray genreJson = json.getJsonArray("genre_ids");

            if (genreJson != null) {
                video.setGenreIds(StreamSupport.stream(genreJson.spliterator(), false)
                        .map(JsonElement::getAsInt)
                        .collect(Collectors.toCollection(ArrayList::new)));
            }

            if (jsonObject.has("videos")) {
                JsonObject videosJsonObject = jsonObject.getAsJsonObject("videos");

                if (videosJsonObject.has("results")) {
                    JsonArray videosJson = videosJsonObject.getAsJsonArray("results");
                    int size = 0;
                    String key = null;

                    for (JsonElement jsonElement : videosJson) {
                        JsonUtil videoJson = new JsonUtil(jsonElement.getAsJsonObject());

                        String type = videoJson.getString("type");
                        String language = videoJson.getString("iso_639_1");
                        String site = videoJson.getString("site");

                        if (!TextUtils.isEmpty(type) && type.equalsIgnoreCase("trailer")
                                && !TextUtils.isEmpty(language) && language.equalsIgnoreCase("en")
                                && !TextUtils.isEmpty(site) && site.equalsIgnoreCase("youtube")) {

                            int tempSize = videoJson.getInt("size");

                            if (tempSize > size) {
                                key = videoJson.getString("key");
                            }
                        }
                    }

                    if (!TextUtils.isEmpty(key)) {
                        video.setYoutubeTrailerKey(key);
                    }
                }
            }

            handleSeason(video, jsonObject);

            return video;
        }

        private void handleSeason(Video video, JsonObject jsonObject) {
            if (video.getVideoType() == VideoType.SEASON && video.getSeason() > 0 && jsonObject.has("seasons")) {
                JsonArray jsonArray = jsonObject.getAsJsonArray("seasons");

                for (JsonElement jsonElement : jsonArray) {
                    JsonUtil json = new JsonUtil(jsonElement.getAsJsonObject());

                    if (video.getSeason() == json.getInt("season_number")) {
                        video.setTitle(video.getTitle());

                        String overView = json.getString("overview");
                        if (!TextUtils.isEmpty(overView)) {
                            video.setOverView(overView);
                        }

                        String poster = json.getString("poster_path");
                        if (!TextUtils.isEmpty(poster)) {
                            video.setPoster(Tmdb.Image.getUrl(poster));
                        }

                        String airDate = json.getString("air_date");
                        if (!TextUtils.isEmpty(airDate)) {
                            video.setReleaseDate(Format.fromTmdbToMillies(airDate));
                        }

                        break;
                    }
                }
            }
        }

        private Video update(Video video, JsonArray jsonArray) {
            for (JsonElement jsonElement : jsonArray) {
                update(video, jsonElement.getAsJsonObject());
                break;
            }

            video.isTmdbChecked(true);

            return video;
        }
    }
}
