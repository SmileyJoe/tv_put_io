package io.smileyjoe.putio.tv.ui.viewholder;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.leanback.widget.Presenter;

import io.smileyjoe.putio.tv.R;
import io.smileyjoe.putio.tv.object.Genre;
import io.smileyjoe.putio.tv.object.Video;
import io.smileyjoe.putio.tv.ui.view.PillView;
import io.smileyjoe.putio.tv.util.PopulateGenres;

public class VideoDetailsViewHolder extends Presenter.ViewHolder {

    private TextView mTextTitle;
    private TextView mTextOverview;
    private LinearLayout mLayoutGenres;
    private Context mContext;

    public VideoDetailsViewHolder(View view) {
        super(view);

        mContext = view.getContext();

        mTextTitle = view.findViewById(R.id.text_title);
        mTextOverview = view.findViewById(R.id.text_overview);
        mLayoutGenres = view.findViewById(R.id.layout_genres);
    }

    public void bind(Video video){
        mTextTitle.setText(video.getTitle());
        mTextOverview.setText(video.getOverView());

        PopulateGenres task = new PopulateGenres(mLayoutGenres, video);
        task.setHideOnEmpty(true);
        task.execute();
    }
}
