package io.smileyjoe.putio.tv.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
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
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;

import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.IntStream;

import io.smileyjoe.putio.tv.R;
import io.smileyjoe.putio.tv.action.video.ActionOption;
import io.smileyjoe.putio.tv.action.video.GroupAction;
import io.smileyjoe.putio.tv.action.video.PlayAction;
import io.smileyjoe.putio.tv.action.video.RefreshAction;
import io.smileyjoe.putio.tv.action.video.ResumeAction;
import io.smileyjoe.putio.tv.interfaces.VideoDetails;
import io.smileyjoe.putio.tv.object.Group;
import io.smileyjoe.putio.tv.object.Video;
import io.smileyjoe.putio.tv.ui.activity.MainActivity;
import io.smileyjoe.putio.tv.ui.activity.VideoDetailsActivity;
import io.smileyjoe.putio.tv.ui.activity.VideoDetailsBackdropActivity;
import io.smileyjoe.putio.tv.ui.viewholder.RelatedVideoCardPresenter;
import io.smileyjoe.putio.tv.ui.viewholder.VideoDetailsDescriptionPresenter;
import io.smileyjoe.putio.tv.util.VideoUtil;

/*
 * LeanbackDetailsFragment extends DetailsFragment, a Wrapper fragment for leanback details screens.
 * It shows a detailed view of video and its meta plus related videos.
 */
public class VideoDetailsFragment extends DetailsFragment implements VideoDetails, PlayAction, ResumeAction, RefreshAction, GroupAction {

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

    private Optional<Listener> mListener = Optional.empty();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDetailsBackground = new DetailsFragmentBackgroundController(this);

        handleIntent();

        if (mVideo != null) {
            if (mVideo.getResumeTime() <= 0) {
                getResumeTime();
            }

            mPresenterSelector = new ClassPresenterSelector();
            mAdapter = new ArrayObjectAdapter(mPresenterSelector);
            populate();
            setAdapter(mAdapter);
            initializeBackground(mVideo);
            setOnItemViewClickedListener(new OnRelatedItemClick());
        } else {
            Intent intent = new Intent(getActivity(), MainActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void handleClick(ActionOption option) {
        switch (option) {
            case RESUME:
                ResumeAction.super.handleClick(option);
                break;
            case WATCH:
                PlayAction.super.handleClick(option);
                break;
            case REFRESH_DATA:
                RefreshAction.super.handleClick(option);
                break;
        }
    }

    @Override
    public void handleClick(Action action) {
        ActionOption option = ActionOption.fromId(action.getId());
        if (option == ActionOption.UNKNOWN) {
            onGroupActionClicked(getGroupId(action.getId()));
        } else {
            handleClick(ActionOption.fromId(action.getId()));
        }
    }

    @Override
    public void setupActions() {
        mActionAdapter = new ArrayObjectAdapter();
        PlayAction.super.setupActions();
        ResumeAction.super.setupActions();
        RefreshAction.super.setupActions();
        GroupAction.super.setupActions();
        mRow.setActionsAdapter(mActionAdapter);
    }

    @Override
    public void addAction(ActionOption option, String title, String subtitle, boolean shouldShow) {
        if (shouldShow) {
            mActionAdapter.add(new Action(option.getId(), title, subtitle));
        }
    }

    @Override
    public void addActionGroup(Group group, String verb, String title) {
        Action action = new Action(getGroupActionId(group.getId()), verb, title);
        mActionAdapter.add(action);
    }

    @Override
    public Context getContext() {
        return getActivity();
    }

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
            mListener = Optional.ofNullable((Listener) getActivity());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        updateActionResume();
    }

    private void initializeBackground(Video video) {
        mDetailsBackground.enableParallax();
        Glide.with(getActivity())
                .asBitmap()
                .load(video.getBackdropAsUri())
                .centerCrop()
                .into(new OnBackgroundLoaded());
    }

    private void setupDetailsOverviewRow() {
        mRow = new DetailsOverviewRow(mVideo);

        loadThumb(mRow);
        setupActions();

        mAdapter.add(mRow);

        getData();
    }

    @Override
    public void update(Video video) {
        RefreshAction.super.update(video);
        if (video.isTmdbFound()) {
            startActivity(VideoDetailsBackdropActivity.getIntent(this.getContext(), video));
            getActivity().finish();
            return;
        }

        if (mRow.getItem() == null) {
            mRow.setItem(video);
        } else {
            mRow.setItem(new Video(video));
        }

        if (!TextUtils.isEmpty(mVideo.getYoutubeTrailerUrl())) {
            Action action = new Action(ActionOption.TRAILER.getId(), getResources().getString(ActionOption.TRAILER.getTitleResId()));
            mActionAdapter.add(action);
        }
    }

    private void loadThumb(DetailsOverviewRow row) {
        Glide.with(getActivity())
                .asDrawable()
                .load(mVideo.getPosterAsUri())
                .centerCrop()
                .into(new OnThumbLoaded(row));
    }

    private void setupDetailsOverviewRowPresenter() {
        // Set detail background.
        FullWidthDetailsOverviewRowPresenter detailsPresenter = new FullWidthDetailsOverviewRowPresenter(new VideoDetailsDescriptionPresenter());

        // Hook up transition element.
        FullWidthDetailsOverviewSharedElementHelper sharedElementHelper = new FullWidthDetailsOverviewSharedElementHelper();
        sharedElementHelper.setSharedElementEnterTransition(getActivity(), VideoDetailsActivity.SHARED_ELEMENT_NAME);

        detailsPresenter.setListener(sharedElementHelper);
        detailsPresenter.setParticipatingEntranceTransition(true);

        detailsPresenter.setOnActionClickedListener(new OnActionClicked(this));
        mPresenterSelector.addClassPresenter(DetailsOverviewRow.class, detailsPresenter);
    }

    private void setupRelatedVideoListRow() {
        ArrayList<Video> relatedVideos = VideoUtil.getRelated(mVideo, mRelatedVideos);

        if (relatedVideos != null && !relatedVideos.isEmpty()) {
            ArrayObjectAdapter listRowAdapter = new ArrayObjectAdapter(new RelatedVideoCardPresenter());
            relatedVideos.forEach(listRowAdapter::add);

            HeaderItem header = new HeaderItem(0, getString(R.string.related_videos));
            mAdapter.add(new ListRow(header, listRowAdapter));
            mPresenterSelector.addClassPresenter(ListRow.class, new ListRowPresenter());
        }
    }

    private Action getAction(long id) {
        return IntStream.range(0, mActionAdapter.size())
                .filter(i -> ((Action) mActionAdapter.get(i)).getId() == id)
                .mapToObj(i -> ((Action) mActionAdapter.get(i)))
                .findFirst()
                .orElse(null);
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
        Action action = getAction(ActionOption.RESUME.getId());

        if (action == null) {
            int currentRange = mActionAdapter.size() - 1;
            action = new Action(ActionOption.RESUME.getId(), getResources().getString(ActionOption.RESUME.getTitleResId()), mVideo.getResumeTimeFormatted());

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

            if (mListener.isPresent() && item instanceof Video) {
                mListener.get().onRelatedClicked((Video) item, mRelatedVideos);
            }
        }
    }

    private class OnBackgroundLoaded extends CustomTarget<Bitmap> {

        @Override
        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
            mDetailsBackground.setCoverBitmap(resource);
            mAdapter.notifyArrayItemRangeChanged(0, mAdapter.size());
        }

        @Override
        public void onLoadCleared(@Nullable Drawable placeholder) {

        }
    }

    private class OnThumbLoaded extends CustomTarget<Drawable> {

        private DetailsOverviewRow mRow;

        public OnThumbLoaded(DetailsOverviewRow row) {
            mRow = row;
        }

        @Override
        public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
            mRow.setImageDrawable(resource);
            mAdapter.notifyArrayItemRangeChanged(0, mAdapter.size());
        }

        @Override
        public void onLoadCleared(@Nullable Drawable placeholder) {

        }
    }
}
