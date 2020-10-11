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
import io.smileyjoe.putio.tv.util.PopulateGenres;

public class DetailsDescriptionPresenter extends AbstractDetailsDescriptionPresenter {

    @Override
    protected void onBindDescription(ViewHolder viewHolder, Object item) {
        Video video = (Video) item;

        if (video != null) {
            int colorText = ContextCompat.getColor(viewHolder.getTitle().getContext(), R.color.text);
            String body = "Added on: " + video.getCreatedAtFormatted();

            if(!TextUtils.isEmpty(video.getOverView())){
                body += "\n\n" + video.getOverView();
            }

            viewHolder.getTitle().setText(video.getTitle());
            viewHolder.getBody().setText(body);

            viewHolder.getTitle().setTextColor(colorText);
            viewHolder.getSubtitle().setTextColor(colorText);
            viewHolder.getBody().setTextColor(colorText);

            PopulateGenres task = new PopulateGenres(viewHolder.getSubtitle(), video);
            task.execute();
        }
    }
}
