package io.smileyjoe.putio.tv.ui.viewholder;

import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.leanback.widget.ImageCardView;
import androidx.leanback.widget.Presenter;

import com.bumptech.glide.Glide;

import java.util.stream.IntStream;

import io.smileyjoe.putio.tv.R;
import io.smileyjoe.putio.tv.object.Video;

/*
 * A CardPresenter is used to generate Views and bind Objects to them on demand.
 * It contains an Image CardView
 */
public class RelatedVideoCardPresenter extends Presenter {
    private static final String TAG = "CardPresenter";

    private static final int CARD_WIDTH = 313;
    private static final int CARD_HEIGHT = 176;
    private static int sSelectedBackgroundColor;
    private static int sDefaultBackgroundColor;
    private static int sDefaultTextColor;
    private static int sSelectedTextColor;
    private Drawable mDefaultCardImage;
    private static int sPosterPadding;

    private static void updateCardBackgroundColor(ImageCardView view, boolean selected) {
        int color = selected ? sSelectedBackgroundColor : sDefaultBackgroundColor;
        view.setBackgroundColor(color);
        view.findViewById(R.id.info_field).setBackgroundColor(color);
    }

    private static void updateTextColor(ImageCardView imageCardView, boolean selected) {
        int color = selected ? sSelectedTextColor : sDefaultTextColor;
        ViewGroup viewInfo = imageCardView.findViewById(R.id.info_field);

        IntStream.range(0, viewInfo.getChildCount())
                .mapToObj(viewInfo::getChildAt)
                .filter(view -> view instanceof TextView)
                .forEach(view -> ((TextView) view).setTextColor(color));
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent) {
        sPosterPadding = parent.getContext().getResources().getDimensionPixelOffset(R.dimen.file_grid_poster_padding);
        sDefaultBackgroundColor = ContextCompat.getColor(parent.getContext(), R.color.bg_default);
        sSelectedBackgroundColor = ContextCompat.getColor(parent.getContext(), R.color.bg_selected);
        sSelectedTextColor = ContextCompat.getColor(parent.getContext(), R.color.text_selected_inverse);
        sDefaultTextColor = ContextCompat.getColor(parent.getContext(), R.color.text);

        ImageCardView cardView =
                new ImageCardView(parent.getContext()) {
                    @Override
                    public void setSelected(boolean selected) {
                        updateCardBackgroundColor(this, selected);
                        updateTextColor(this, selected);
                        super.setSelected(selected);
                    }
                };

        cardView.setFocusable(true);
        cardView.setFocusableInTouchMode(true);
        updateCardBackgroundColor(cardView, false);
        updateTextColor(cardView, false);
        return new ViewHolder(cardView);
    }

    @Override
    public void onBindViewHolder(Presenter.ViewHolder viewHolder, Object item) {
        Video video = (Video) item;
        ImageCardView cardView = (ImageCardView) viewHolder.view;

        cardView.setTitleText(video.getTitleFormatted());
        cardView.setMainImageDimensions(CARD_WIDTH, CARD_HEIGHT);

        if (video.getPosterAsUri() != null) {
            Glide.with(viewHolder.view.getContext())
                    .load(video.getPosterAsUri())
                    .centerCrop()
                    .error(mDefaultCardImage)
                    .into(cardView.getMainImageView());
        } else {
            cardView.getMainImageView().setPadding(sPosterPadding,sPosterPadding,sPosterPadding,sPosterPadding);
            cardView.getMainImageView().setImageResource(R.drawable.ic_movie_24);
        }

    }

    @Override
    public void onUnbindViewHolder(Presenter.ViewHolder viewHolder) {
        ImageCardView cardView = (ImageCardView) viewHolder.view;
        cardView.setBadgeImage(null);
        cardView.setMainImage(null);
    }
}
