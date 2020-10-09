package io.smileyjoe.putio.tv.util;

import android.os.AsyncTask;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import io.smileyjoe.putio.tv.db.AppDatabase;
import io.smileyjoe.putio.tv.object.Genre;
import io.smileyjoe.putio.tv.object.Video;

public class PopulateGenres extends AsyncTask<Void, Void, String> {
    private TextView mTextView;
    private Video mVideo;
    private boolean mHideOnEmpty = false;

    public PopulateGenres(TextView textView, Video video) {
        mTextView = textView;
        mVideo = video;
    }

    public void setHideOnEmpty(boolean hideOnEmpty) {
        mHideOnEmpty = hideOnEmpty;
    }

    @Override
    protected String doInBackground(Void... voids) {
        if(!TextUtils.isEmpty(mVideo.getGenresFormatted())) {
            return mVideo.getGenresFormatted();
        } else {
            ArrayList<Integer> genreIds = mVideo.getGenreIds();

            if (genreIds != null && !genreIds.isEmpty()) {
                return format(AppDatabase.getInstance(mTextView.getContext()).genreDao().getByIds(genreIds));
            } else {
                return null;
            }
        }
    }

    private String format(List<Genre> genres){
        if (genres != null && !genres.isEmpty()) {
            String genreText = "";

            for (Genre genre : genres) {
                if (!TextUtils.isEmpty(genreText)) {
                    genreText += ", ";
                }

                genreText += genre.getTitle();
            }

            return genreText;
        }

        return null;
    }

    @Override
    protected void onPostExecute(String genresFormatted) {
        if(mHideOnEmpty) {
            if(TextUtils.isEmpty(genresFormatted)){
                mTextView.setVisibility(View.GONE);
            } else {
                mTextView.setVisibility(View.VISIBLE);
            }

        }

        mTextView.setText(genresFormatted);
    }
}
