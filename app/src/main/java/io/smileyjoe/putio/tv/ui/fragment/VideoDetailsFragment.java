package io.smileyjoe.putio.tv.ui.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.leanback.app.DetailsFragment;
import androidx.leanback.app.DetailsFragmentBackgroundController;
import androidx.leanback.widget.Action;
import androidx.leanback.widget.ArrayObjectAdapter;
import androidx.leanback.widget.ClassPresenterSelector;
import androidx.leanback.widget.DetailsOverviewRow;
import androidx.leanback.widget.FullWidthDetailsOverviewRowPresenter;
import androidx.leanback.widget.FullWidthDetailsOverviewSharedElementHelper;
import androidx.leanback.widget.HeaderItem;
import androidx.leanback.widget.ListRow;
import androidx.leanback.widget.ListRowPresenter;
import androidx.leanback.widget.OnItemViewClickedListener;
import androidx.leanback.widget.Presenter;
import androidx.leanback.widget.Row;
import androidx.leanback.widget.RowPresenter;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import java.util.ArrayList;

import io.smileyjoe.putio.tv.R;
import io.smileyjoe.putio.tv.network.Tmdb;
import io.smileyjoe.putio.tv.object.Video;
import io.smileyjoe.putio.tv.object.VideoType;
import io.smileyjoe.putio.tv.ui.activity.VideoDetailsActivity;
import io.smileyjoe.putio.tv.ui.activity.MainActivity;
import io.smileyjoe.putio.tv.ui.viewholder.RelatedVideoCardPresenter;
import io.smileyjoe.putio.tv.ui.viewholder.VideoDetailsDescriptionPresenter;
import io.smileyjoe.putio.tv.util.TmdbUtil;
import io.smileyjoe.putio.tv.util.VideoAction;
import io.smileyjoe.putio.tv.util.VideoUtil;

/*
 * LeanbackDetailsFragment extends DetailsFragment, a Wrapper fragment for leanback details screens.
 * It shows a detailed view of video and its meta plus related videos.
 */
public class VideoDetailsFragment extends DetailsFragment implements TmdbUtil.Listener, VideoAction.Listener {

    public interface Listener {
        void onRelatedClicked(Video video, ArrayList<Video> relatedVideos);
    }

    private Video mVideo;
    private ArrayList<Video> mRelatedVideos;

    private ArrayObjectAdapter mAdapter;
    private ClassPresenterSelector mPresenterSelector;
    private DetailsOverviewRow mRow;

    private ArrayObjectAdapter mActionAdapter;

    private DetailsFragmentBackgroundController mDetailsBackground;

    private Listener mListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDetailsBackground = new DetailsFragmentBackgroundController(this);

        handleIntent();

        if (mVideo != null) {
            if (mVideo.getResumeTime() <= 0) {
                getResumeTime();
            }

//            getConversionStatus();
            mPresenterSelector = new ClassPresenterSelector();
            mAdapter = new ArrayObjectAdapter(mPresenterSelector);
            populate();
            setAdapter(mAdapter);
            initializeBackground(mVideo);
            setOnItemViewClickedListener(new OnRelatedItemClick());
            addGroupActions((group, verb, title) -> {
                Action action = new Action(getGroupActionId(group.getId()), verb, title);
                mActionAdapter.add(action);
            });
        } else {
            Intent intent = new Intent(getActivity(), MainActivity.class);
            startActivity(intent);
        }
    }

//    private void getConversionStatus() {
//        if (!mVideo.isConverted()) {
//            Putio.getConversionStatus(getContext(), mVideo.getPutId(), new OnConvertResponse());
//        }
//    }

    private void populate() {
        setupDetailsOverviewRow();
        setupDetailsOverviewRowPresenter();
        setupRelatedVideoListRow();
    }

    public void handleIntent() {
        mVideo = getActivity().getIntent().getParcelableExtra(VideoDetailsActivity.VIDEO);
        mRelatedVideos = getActivity().getIntent().getParcelableArrayListExtra(VideoDetailsActivity.RELATED_VIDEOS);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getActivity() instanceof Listener) {
            mListener = (Listener) getActivity();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getResumeTime();
    }

    private void initializeBackground(Video video) {
        mDetailsBackground.enableParallax();
        Glide.with(getActivity())
                .load(video.getBackdropAsUri())
                .asBitmap()
                .centerCrop()
                .into(new OnBackgroundLoaded());
    }

    private void setupDetailsOverviewRow() {
        mRow = new DetailsOverviewRow(mVideo);

        loadThumb(mRow);
        addActions(mRow);

        mAdapter.add(mRow);

        if(mVideo.getVideoType() == VideoType.MOVIE && mVideo.isTmdbFound() && TextUtils.isEmpty(mVideo.getTagLine())) {
            TmdbUtil.OnTmdbResponse response = new TmdbUtil.OnTmdbResponse(getContext(), mVideo);
            response.setListener(this);
            Tmdb.Movie.get(getContext(), mVideo.getTmdbId(), response);
        }
    }

    @Override
    public void update(Video video) {
        if(mRow.getItem() == null) {
            mRow.setItem(video);
        } else {
            mRow.setItem(new Video(video));
        }

        if(!TextUtils.isEmpty(mVideo.getYoutubeTrailerUrl())){
            Action action = new Action(VideoAction.Option.TRAILER.getId(), getResources().getString(VideoAction.Option.TRAILER.getTitleResId()));
            mActionAdapter.add(action);
        }
    }

    private void loadThumb(DetailsOverviewRow row) {
        Glide.with(getActivity())
                .load(mVideo.getPosterAsUri())
                .centerCrop()
                .into(new OnThumbLoaded(row));
    }

    private void addActions(DetailsOverviewRow row) {
        mActionAdapter = new ArrayObjectAdapter();

        for (VideoAction.Option option : VideoAction.Option.values()) {
            if(option.shouldShow()) {
                Action action = null;

                switch (option) {
                    case REFRESH_DATA:
                    case WATCH:
                        action = new Action(option.getId(), getResources().getString(option.getTitleResId()));
                        break;
                    case RESUME:
                        if (mVideo.getResumeTime() > 0) {
                            action = new Action(option.getId(), getResources().getString(option.getTitleResId()), mVideo.getResumeTimeFormatted());
                        }
                        break;
                    case TRAILER:
                        if (!TextUtils.isEmpty(mVideo.getYoutubeTrailerUrl())) {
                            action = new Action(option.getId(), getResources().getString(option.getTitleResId()));
                        }
                        break;
//                case CONVERT:
//                    if (!mVideo.isConverted()) {
//                        action = new Action(option.getId(), getResources().getString(option.getTitleResId()));
//                    }
//                    break;
                }

                if (action != null) {
                    mActionAdapter.add(action);
                }
            }
        }

        row.setActionsAdapter(mActionAdapter);
    }

    private void setupDetailsOverviewRowPresenter() {
        // Set detail background.
        FullWidthDetailsOverviewRowPresenter detailsPresenter = new FullWidthDetailsOverviewRowPresenter(new VideoDetailsDescriptionPresenter());

        // Hook up transition element.
        FullWidthDetailsOverviewSharedElementHelper sharedElementHelper = new FullWidthDetailsOverviewSharedElementHelper();
        sharedElementHelper.setSharedElementEnterTransition(getActivity(), VideoDetailsActivity.SHARED_ELEMENT_NAME);

        detailsPresenter.setListener(sharedElementHelper);
        detailsPresenter.setParticipatingEntranceTransition(true);

        detailsPresenter.setOnActionClickedListener(new VideoAction.OnActionClicked(this));
        mPresenterSelector.addClassPresenter(DetailsOverviewRow.class, detailsPresenter);
    }

    private void setupRelatedVideoListRow() {
        ArrayList<Video> relatedVideos = VideoUtil.getRelated(mVideo,mRelatedVideos);

        if (relatedVideos != null && !relatedVideos.isEmpty()) {
            ArrayObjectAdapter listRowAdapter = new ArrayObjectAdapter(new RelatedVideoCardPresenter());

            for (Video video : relatedVideos) {
                listRowAdapter.add(video);
            }

            HeaderItem header = new HeaderItem(0, getString(R.string.related_videos));
            mAdapter.add(new ListRow(header, listRowAdapter));
            mPresenterSelector.addClassPresenter(ListRow.class, new ListRowPresenter());
        }
    }

    private Action getAction(long id) {
        for (int i = 0; i < mActionAdapter.size(); i++) {
            Action action = (Action) mActionAdapter.get(i);

            if (action.getId() == id) {
                return action;
            }
        }

        return null;
    }

    private void updateActions(Action action) {
        int position = mActionAdapter.indexOf(action);
        mActionAdapter.notifyItemRangeChanged(position, position);
    }

    @Override
    public Video getVideo() {
        return mVideo;
    }

    @Override
    public void updateActionGroup(long groupId, int verb) {
        long actionId = getGroupActionId(groupId);
        Action action = getAction(actionId);
        action.setLabel1(getString(verb));
        updateActions(action);
    }

    @Override
    public void updateActionResume() {
        Action action = getAction(VideoAction.Option.RESUME.getId());

        if (action == null) {
            int currentRange = mActionAdapter.size() - 1;
            action = new Action(VideoAction.Option.RESUME.getId(), getResources().getString(VideoAction.Option.RESUME.getTitleResId()), mVideo.getResumeTimeFormatted());

            mActionAdapter.add(action);
            mActionAdapter.notifyItemRangeChanged(currentRange, currentRange + 1);
        } else {
            action.setLabel2(mVideo.getResumeTimeFormatted());
            updateActions(action);
        }
    }

    private class OnRelatedItemClick implements OnItemViewClickedListener {
        @Override
        public void onItemClicked(
                Presenter.ViewHolder itemViewHolder,
                Object item,
                RowPresenter.ViewHolder rowViewHolder,
                Row row) {

            if (mListener != null && item instanceof Video) {
                mListener.onRelatedClicked((Video) item, mRelatedVideos);
            }
        }
    }

    private class OnBackgroundLoaded extends SimpleTarget<Bitmap> {
        @Override
        public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
            mDetailsBackground.setCoverBitmap(resource);
            mAdapter.notifyArrayItemRangeChanged(0, mAdapter.size());
        }
    }

    private class OnThumbLoaded extends SimpleTarget<GlideDrawable> {

        private DetailsOverviewRow mRow;

        public OnThumbLoaded(DetailsOverviewRow row) {
            mRow = row;
        }

        @Override
        public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
            mRow.setImageDrawable(resource);
            mAdapter.notifyArrayItemRangeChanged(0, mAdapter.size());
        }
    }
}
