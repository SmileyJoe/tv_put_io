package io.smileyjoe.putio.tv.util;

import android.os.AsyncTask;
import android.text.TextUtils;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import io.smileyjoe.putio.tv.db.AppDatabase;
import io.smileyjoe.putio.tv.object.Genre;
import io.smileyjoe.putio.tv.object.Video;

public class PopulateGenres extends AsyncTask<Void, Void, List<Genre>> {
    private TextView mTextView;
    private Video mVideo;

    public PopulateGenres(TextView textView, Video video) {
        mTextView = textView;
        mVideo = video;
    }

    @Override
    protected List<Genre> doInBackground(Void... voids) {
        ArrayList<Integer> genreIds = mVideo.getGenreIds();

        if(genreIds != null && !genreIds.isEmpty()) {
            return AppDatabase.getInstance(mTextView.getContext()).genreDao().getByIds(genreIds);
        } else {
            return null;
        }
    }

    @Override
    protected void onPostExecute(List<Genre> genres) {
        if (genres != null && !genres.isEmpty()) {
            String genreText = "";

            for (Genre genre : genres) {
                if (!TextUtils.isEmpty(genreText)) {
                    genreText += ", ";
                }

                genreText += genre.getTitle();
            }

            mTextView.setText(genreText);
        }
    }
}
