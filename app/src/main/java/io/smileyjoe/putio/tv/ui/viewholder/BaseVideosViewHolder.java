package io.smileyjoe.putio.tv.ui.viewholder;

import android.content.Context;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewbinding.ViewBinding;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

import io.smileyjoe.putio.tv.R;
import io.smileyjoe.putio.tv.action.video.ActionOption;
import io.smileyjoe.putio.tv.action.video.ConvertAction;
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

    private class ContextMenu implements View.OnCreateContextMenuListener, ConvertAction, RefreshAction, PlayAction {

        private Video mVideo;
        private HashMap<ActionOption, String> mItems = new HashMap<>();
        private Context mContext;
        private android.view.ContextMenu mMenu;

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
            RefreshAction.super.handleClick(option);
            PlayAction.super.handleClick(option);
            ConvertAction.super.handleClick(option);
        }

        @Override
        public void onCreateContextMenu(android.view.ContextMenu menu, View v, android.view.ContextMenu.ContextMenuInfo menuInfo) {
            mMenu = menu;
            mMenu.setHeaderTitle(getVideo().getTitleFormatted(getContext(), false));

            AtomicInteger position = new AtomicInteger(0);
            mItems.forEach((option, title) -> {
                mMenu.add(Menu.NONE, Math.toIntExact(option.getId()), position.getAndIncrement(), title)
                        .setOnMenuItemClickListener(new OnContextItemClicked(option, this));
            });
        }

        @Override
        public Video getVideo() {
            return mVideo;
        }

        @Override
        public void addAction(ActionOption option, String title, String subtitle, boolean shouldShow) {
            switch (option) {
                case CONVERT:
                case WATCH:
                    if (getVideo().getFileType() == FileType.VIDEO) {
                        mItems.put(option, title);
                    }
                    break;
                default:
                    mItems.put(option, title);
                    break;
            }
        }

        @Override
        public void setupActions() {
            RefreshAction.super.setupActions();
            PlayAction.super.setupActions();
            ConvertAction.super.setupActions();
        }

        @Override
        public void updateActionConvert(String title) {
            mMenu.findItem(Math.toIntExact(ActionOption.CONVERT.getId())).setTitle(title);
        }

        @Override
        public void update(Video video) {
            getListener().ifPresent(l -> l.update(video, getInternalPosition()));
            RefreshAction.super.update(video);
        }
    }
}
