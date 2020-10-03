package io.smileyjoe.putio.tv.ui.viewholder;

import androidx.core.content.ContextCompat;
import androidx.leanback.widget.AbstractDetailsDescriptionPresenter;

import io.smileyjoe.putio.tv.R;
import io.smileyjoe.putio.tv.object.Video;

public class DetailsDescriptionPresenter extends AbstractDetailsDescriptionPresenter {

    @Override
    protected void onBindDescription(ViewHolder viewHolder, Object item) {
        Video video = (Video) item;

        if (video != null) {
            int colorText = ContextCompat.getColor(viewHolder.getTitle().getContext(), R.color.text_selected);
            viewHolder.getTitle().setText(video.getTitle());
            viewHolder.getSubtitle().setText(Long.toString(video.getPutId()));
            viewHolder.getBody().setText(video.getOverView());

            viewHolder.getTitle().setTextColor(colorText);
            viewHolder.getSubtitle().setTextColor(colorText);
            viewHolder.getBody().setTextColor(colorText);
        }
    }
}