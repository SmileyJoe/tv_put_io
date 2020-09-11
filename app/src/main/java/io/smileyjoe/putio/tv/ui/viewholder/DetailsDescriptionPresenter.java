package io.smileyjoe.putio.tv.ui.viewholder;

import androidx.leanback.widget.AbstractDetailsDescriptionPresenter;

import io.smileyjoe.putio.tv.putio.File;

public class DetailsDescriptionPresenter extends AbstractDetailsDescriptionPresenter {

    @Override
    protected void onBindDescription(ViewHolder viewHolder, Object item) {
        File movie = (File) item;

        if (movie != null) {
            viewHolder.getTitle().setText(movie.getName());
            viewHolder.getSubtitle().setText(Long.toString(movie.getId()));
            viewHolder.getBody().setText(movie.getStreamUri().toString());
        }
    }
}
