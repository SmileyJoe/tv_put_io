package io.smileyjoe.putio.tv.util;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;

import io.smileyjoe.putio.tv.db.AppDatabase;
import io.smileyjoe.putio.tv.network.Response;
import io.smileyjoe.putio.tv.network.Tmdb;
import io.smileyjoe.putio.tv.object.Character;
import io.smileyjoe.putio.tv.object.Video;

public class TmdbUtil {

    public interface Listener{
        void update(Video video);
    }

    private TmdbUtil() {
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
            task.execute();
        }
    }

    public static class ProcessTmdbResponse extends AsyncTask<Void, Void, Video> {

        private Context mContext;
        private JsonObject mResult;
        private Video mVideo;
        private Listener mListener;

        public ProcessTmdbResponse(Context context, Video video, JsonObject result) {
            mContext = context;
            mVideo = video;
            mResult = result;
        }

        public void setListener(Listener listener) {
            mListener = listener;
        }

        @Override
        protected Video doInBackground(Void... voids) {
            if(mResult.has("results")) {
                update(mVideo, mResult.get("results").getAsJsonArray());
            } else {
                update(mVideo, mResult.getAsJsonObject());
            }

            AppDatabase.getInstance(mContext).videoDao().insert(mVideo);
            return mVideo;
        }

        @Override
        protected void onPostExecute(Video video) {
            if(mListener != null) {
                mListener.update(video);
            }
        }

        private void handleCast(Video video, JsonObject jsonObject){
            if(jsonObject.has("credits")){
                JsonObject creditsJsonObject = jsonObject.getAsJsonObject("credits");

                if(creditsJsonObject.has("cast")) {
                    ArrayList<Character> characters = new ArrayList<>();
                    JsonArray jsonArray = creditsJsonObject.getAsJsonArray("cast");

                    for (JsonElement jsonElement : jsonArray) {
                        Character character = new Character();
                        JsonUtil json = new JsonUtil(jsonElement.getAsJsonObject());

                        character.setCastMemberName(json.getString("name"));
                        character.setCastMemberTmdbId(json.getLong("id"));
                        character.setName(json.getString("character"));
                        character.setOrder(json.getInt("order"));
                        character.setProfileImage(Tmdb.getImageUrl(json.getString("profile_path")));
                        character.setVideoTmdbId(video.getTmdbId());

                        characters.add(character);
                    }

                    if(characters != null && !characters.isEmpty()) {
                        AppDatabase.getInstance(mContext).characterDao().insert(characters);
                        video.setCharacters(characters);
                    }
                }
            }
        }

        private Video update(Video video, JsonObject jsonObject){
            handleCast(video, jsonObject);
            JsonUtil json = new JsonUtil(jsonObject);

            video.setTmdbId(json.getLong("id"));
            video.setBackdrop(Tmdb.getImageUrl(json.getString("backdrop_path")));
            video.setOverView(json.getString("overview"));
            video.setPoster(Tmdb.getImageUrl(json.getString("poster_path")));
            video.setTitle(json.getString("title"));
            video.setReleaseDate(Format.fromTmdbToMillies(json.getString("release_date")));
            video.setTagLine(json.getString("tagline"));
            video.setRuntime(json.getInt("runtime"));
            video.isTmdbFound(true);

            JsonArray genreJson = json.getJsonArray("genre_ids");

            if(genreJson != null) {
                ArrayList<Integer> genreIds = new ArrayList<>();
                for (JsonElement genreElement : genreJson) {
                    genreIds.add(genreElement.getAsInt());
                }
                video.setGenreIds(genreIds);
            }

            return video;
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
