package io.smileyjoe.putio.tv.util;

import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import io.smileyjoe.putio.tv.R;
import io.smileyjoe.putio.tv.db.AppDatabase;
import io.smileyjoe.putio.tv.object.Genre;
import io.smileyjoe.putio.tv.object.Video;
import io.smileyjoe.putio.tv.ui.view.PillView;

public class PopulateGenres extends AsyncTask<Void, Void, List<Genre>> {

    private enum Type{
        CSV, PILL
    }

    private TextView mTextView;
    private LinearLayout mLayout;
    private Video mVideo;
    private boolean mHideOnEmpty = false;
    private Type mType;
    private Context mContext;

    public PopulateGenres(LinearLayout layout, Video video){
        mLayout = layout;
        mVideo = video;
        mType = Type.PILL;
        mContext = mLayout.getContext();
    }

    public PopulateGenres(TextView textView, Video video) {
        mTextView = textView;
        mVideo = video;
        mType = Type.CSV;
        mContext = mTextView.getContext();
    }

    public void setHideOnEmpty(boolean hideOnEmpty) {
        mHideOnEmpty = hideOnEmpty;
    }

    @Override
    protected List<Genre> doInBackground(Void... voids) {
        ArrayList<Integer> genreIds = mVideo.getGenreIds();

        if (genreIds != null && !genreIds.isEmpty()) {
            return AppDatabase.getInstance(mContext).genreDao().getByIds(genreIds);
        } else {
            return null;
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
    protected void onPostExecute(List<Genre> genres) {
        switch (mType){
            case CSV:
                populateCsv(genres);
                break;
            case PILL:
                populatePill(genres);
                break;
        }
    }

    private void populateCsv(List<Genre> genres){
        boolean isVisible = handleVisibility(genres, mTextView);

        if(isVisible) {
            mTextView.setText(format(genres));
        }
    }

    private void populatePill(List<Genre> genres){
        boolean isVisible = handleVisibility(genres, mLayout);

        if(isVisible) {
            mLayout.removeAllViews();

            genres.forEach(genre -> {
                PillView pill = (PillView) LayoutInflater.from(mContext).inflate(R.layout.item_video_details_genre, mLayout, false);
                pill.setText(genre.getTitle());

                mLayout.addView(pill);
            });
        }
    }

    private boolean handleVisibility(List<Genre> genres, View view){
        if(genres != null && !genres.isEmpty()){
            view.setVisibility(View.VISIBLE);
            return true;
        } else {
            if(mHideOnEmpty){
                view.setVisibility(View.GONE);
            }
            return false;
        }
    }
}
