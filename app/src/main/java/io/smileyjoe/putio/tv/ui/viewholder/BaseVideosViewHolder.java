package io.smileyjoe.putio.tv.ui.viewholder;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.ContextMenu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewbinding.ViewBinding;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import io.smileyjoe.putio.tv.R;
import io.smileyjoe.putio.tv.action.video.ActionOption;
import io.smileyjoe.putio.tv.action.video.PlayAction;
import io.smileyjoe.putio.tv.action.video.RefreshAction;
import io.smileyjoe.putio.tv.object.FileType;
import io.smileyjoe.putio.tv.object.FragmentType;
import io.smileyjoe.putio.tv.object.Video;

public abstract class BaseVideosViewHolder<V extends ViewBinding> extends BaseViewHolder<Video, V> {

    private int mPosterPadding;

    public BaseVideosViewHolder(@NonNull View itemView, FragmentType fragmentType) {
        super(itemView, fragmentType);
        mPosterPadding = itemView.getContext().getResources().getDimensionPixelOffset(R.dimen.file_grid_poster_padding);
    }

    protected void populateSummary(Video video, TextView view) {
        view.setText(video.getOverView());
    }

    @Override
    public void bindView(Video item, int position) {
        super.bindView(item, position);

        mView.getRoot().setOnCreateContextMenuListener(new ContextMenu(getContext(), item));
    }

    @Override
    public boolean onLongClick(View v) {
        v.showContextMenu();
        return true;
    }

    protected void populatePoster(Video video, ImageView view) {
        if (video.getPosterAsUri() != null) {
            view.setPadding(0, 0, 0, 0);
            Glide.with(getContext())
                    .load(video.getPosterAsUri())
                    .dontAnimate()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(view);
        } else {
            view.setPadding(mPosterPadding, mPosterPadding, mPosterPadding, mPosterPadding);
            view.setImageResource(R.drawable.ic_movie_24);
        }
    }

    private class ContextMenu implements View.OnCreateContextMenuListener, RefreshAction, PlayAction {

        private Video mVideo;
        private List<ActionOption> mItems = new ArrayList<>();
        private Context mContext;

        public ContextMenu(Context context, Video video) {
            mContext = context;
            mVideo = video;
            setupActions();
        }

        @Override
        public Context getContext() {
            return mContext;
        }

        @Override
        public void handleClick(ActionOption option) {
            switch (option){
                case REFRESH_DATA:
                    RefreshAction.super.handleClick(option);
                    break;
                case WATCH:
                    PlayAction.super.handleClick(option);
                    break;
            }
        }

        @Override
        public void onCreateContextMenu(android.view.ContextMenu menu, View v, android.view.ContextMenu.ContextMenuInfo menuInfo) {
            menu.setHeaderTitle(getVideo().getTitleFormatted(getContext(), false));
            mItems.forEach(option -> {
                menu.add(option.getTitleResId()).setOnMenuItemClickListener(new OnContextItemClicked(option, this));
            });
        }

        @Override
        public Video getVideo() {
            return mVideo;
        }

        @Override
        public void addAction(ActionOption option, boolean shouldShow) {
            switch (option){
                case WATCH:
                    if(getVideo().getFileType() == FileType.VIDEO){
                        mItems.add(option);
                    }
                    break;
                default:
                    mItems.add(option);
                    break;
            }

        }

        @Override
        public void setupActions() {
            RefreshAction.super.setupActions();
            PlayAction.super.setupActions();
        }

        @Override
        public void update(Video video) {
            getListener().ifPresent(l -> l.update(video, getInternalPosition()));
        }
    }
}
