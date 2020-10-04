package io.smileyjoe.putio.tv.ui.viewholder;

import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.leanback.widget.AbstractDetailsDescriptionPresenter;
import androidx.loader.content.AsyncTaskLoader;

import java.util.ArrayList;
import java.util.List;

import io.smileyjoe.putio.tv.R;
import io.smileyjoe.putio.tv.db.AppDatabase;
import io.smileyjoe.putio.tv.object.Genre;
import io.smileyjoe.putio.tv.object.Video;

public class DetailsDescriptionPresenter extends AbstractDetailsDescriptionPresenter {

    @Override
    protected void onBindDescription(ViewHolder viewHolder, Object item) {
        Video video = (Video) item;

        if (video != null) {
            int colorText = ContextCompat.getColor(viewHolder.getTitle().getContext(), R.color.text_selected);
            viewHolder.getTitle().setText(video.getTitle());
            viewHolder.getBody().setText(video.getOverView());

            viewHolder.getTitle().setTextColor(colorText);
            viewHolder.getSubtitle().setTextColor(colorText);
            viewHolder.getBody().setTextColor(colorText);

            GetGenres task = new GetGenres(viewHolder.getSubtitle(), video);
            task.execute();
        }
    }

    private class GetGenres extends AsyncTask<Void, Void, List<Genre>>{
        private TextView mTextView;
        private Video mVideo;

        public GetGenres(TextView textView, Video video) {
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
}
