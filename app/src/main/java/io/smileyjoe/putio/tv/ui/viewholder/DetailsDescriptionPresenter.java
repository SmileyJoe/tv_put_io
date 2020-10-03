package io.smileyjoe.putio.tv.ui.viewholder;

import androidx.core.content.ContextCompat;
import androidx.leanback.widget.AbstractDetailsDescriptionPresenter;

import io.smileyjoe.putio.tv.R;
import io.smileyjoe.putio.tv.putio.File;

public class DetailsDescriptionPresenter extends AbstractDetailsDescriptionPresenter {

    @Override
    protected void onBindDescription(ViewHolder viewHolder, Object item) {
        File movie = (File) item;

        if (movie != null) {
            int colorText = ContextCompat.getColor(viewHolder.getTitle().getContext(), R.color.text_selected);
            viewHolder.getTitle().setText(movie.getName());
            viewHolder.getSubtitle().setText(Long.toString(movie.getId()));
            viewHolder.getBody().setText(movie.getOverview());

            viewHolder.getTitle().setTextColor(colorText);
            viewHolder.getSubtitle().setTextColor(colorText);
            viewHolder.getBody().setTextColor(colorText);
        }
    }
}
