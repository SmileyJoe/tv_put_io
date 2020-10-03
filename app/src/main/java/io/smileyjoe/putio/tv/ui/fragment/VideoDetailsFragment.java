package io.smileyjoe.putio.tv.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.core.content.ContextCompat;
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
import androidx.leanback.widget.OnActionClickedListener;
import androidx.leanback.widget.OnItemViewClickedListener;
import androidx.leanback.widget.Presenter;
import androidx.leanback.widget.Row;
import androidx.leanback.widget.RowPresenter;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.google.gson.JsonObject;

import java.util.ArrayList;

import io.smileyjoe.putio.tv.R;
import io.smileyjoe.putio.tv.network.Putio;
import io.smileyjoe.putio.tv.network.Response;
import io.smileyjoe.putio.tv.object.Video;
import io.smileyjoe.putio.tv.ui.activity.DetailsActivity;
import io.smileyjoe.putio.tv.ui.activity.MainActivity;
import io.smileyjoe.putio.tv.ui.viewholder.CardPresenter;
import io.smileyjoe.putio.tv.ui.viewholder.DetailsDescriptionPresenter;

/*
 * LeanbackDetailsFragment extends DetailsFragment, a Wrapper fragment for leanback details screens.
 * It shows a detailed view of video and its meta plus related videos.
 */
public class VideoDetailsFragment extends DetailsFragment {

    public interface Listener {
        void onWatchClicked(Video video);

        void onConvertClicked(Video video);

        void onRelatedClicked(Video video, ArrayList<Video> relatedVideos);

        void onResumeClick(Video video);
    }

    private enum ActionOption {
        WATCH(1, R.string.action_watch),
        RESUME(2, R.string.action_resume),
        CONVERT(3, R.string.action_convert);

        private long mId;
        private @StringRes
        int mTitleResId;

        ActionOption(long id, int titleResId) {
            mId = id;
            mTitleResId = titleResId;
        }

        public long getId() {
            return mId;
        }

        public int getTitleResId() {
            return mTitleResId;
        }

        public static ActionOption fromId(long id) {
            for (ActionOption option : values()) {
                if (option.getId() == id) {
                    return option;
                }
            }

            return null;
        }
    }

    private static final int DETAIL_THUMB_WIDTH = 274;
    private static final int DETAIL_THUMB_HEIGHT = 274;

    private static final int NUM_COLS = 10;

    private Video mVideo;
    private ArrayList<Video> mRelatedVideos;

    private ArrayObjectAdapter mAdapter;
    private ClassPresenterSelector mPresenterSelector;

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

            getConversionStatus();
            mPresenterSelector = new ClassPresenterSelector();
            mAdapter = new ArrayObjectAdapter(mPresenterSelector);
            setAdapter(mAdapter);
            populate();
            initializeBackground(mVideo);
            setOnItemViewClickedListener(new OnRelatedItemClick());
        } else {
            Intent intent = new Intent(getActivity(), MainActivity.class);
            startActivity(intent);
        }
    }

    private void getResumeTime() {
        Putio.getResumeTime(getContext(), mVideo.getPutId(), new OnResumeResponse());
    }

    private void getConversionStatus() {
        if (!mVideo.isConverted()) {
            Putio.getConversionStatus(getContext(), mVideo.getPutId(), new OnConvertResponse());
        }
    }

    private void populate() {
        setupDetailsOverviewRow();
        setupDetailsOverviewRowPresenter();
        setupRelatedVideoListRow();
    }

    public void handleIntent() {
        mVideo = getActivity().getIntent().getParcelableExtra(DetailsActivity.VIDEO);
        mRelatedVideos = getActivity().getIntent().getParcelableArrayListExtra(DetailsActivity.RELATED_VIDEOS);
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
                .load(video.getBackdrop())
                .asBitmap()
                .centerCrop()
                .error(R.drawable.default_background)
                .into(new OnBackgroundLoaded());
    }

    private void setupDetailsOverviewRow() {
        DetailsOverviewRow row = new DetailsOverviewRow(mVideo);
        row.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.default_background));

        loadThumb(row);
        addActions(row);

        mAdapter.add(row);
    }

    private void loadThumb(DetailsOverviewRow row) {
        int width = convertDpToPixel(getActivity().getApplicationContext(), DETAIL_THUMB_WIDTH);
        int height = convertDpToPixel(getActivity().getApplicationContext(), DETAIL_THUMB_HEIGHT);

        Glide.with(getActivity())
                .load(mVideo.getPoster())
                .centerCrop()
                .error(R.drawable.default_background)
                .into(new OnThumbLoaded(width, height, row));
    }

    private void addActions(DetailsOverviewRow row) {
        mActionAdapter = new ArrayObjectAdapter();

        for (ActionOption option : ActionOption.values()) {
            Action action = null;

            switch (option) {
                case WATCH:
                    action = new Action(option.getId(), getResources().getString(option.getTitleResId()));
                    break;
                case RESUME:
                    if (mVideo.getResumeTime() > 0) {
                        action = new Action(option.getId(), getResources().getString(option.getTitleResId()), mVideo.getResumeTimeFormatted());
                    }
                    break;
                case CONVERT:
                    if (!mVideo.isConverted()) {
                        action = new Action(option.getId(), getResources().getString(option.getTitleResId()));
                    }
                    break;
            }

            if (action != null) {
                mActionAdapter.add(action);
            }
        }

        row.setActionsAdapter(mActionAdapter);
    }

    private void setupDetailsOverviewRowPresenter() {
        // Set detail background.
        FullWidthDetailsOverviewRowPresenter detailsPresenter = new FullWidthDetailsOverviewRowPresenter(new DetailsDescriptionPresenter());
        detailsPresenter.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.selected_background));

        // Hook up transition element.
        FullWidthDetailsOverviewSharedElementHelper sharedElementHelper = new FullWidthDetailsOverviewSharedElementHelper();
        sharedElementHelper.setSharedElementEnterTransition(getActivity(), DetailsActivity.SHARED_ELEMENT_NAME);

        detailsPresenter.setListener(sharedElementHelper);
        detailsPresenter.setParticipatingEntranceTransition(true);

        detailsPresenter.setOnActionClickedListener(new OnActionClicked());
        mPresenterSelector.addClassPresenter(DetailsOverviewRow.class, detailsPresenter);
    }

    private void setupRelatedVideoListRow() {
        if (mRelatedVideos != null && !mRelatedVideos.isEmpty()) {
            ArrayObjectAdapter listRowAdapter = new ArrayObjectAdapter(new CardPresenter());
            for (Video video : mRelatedVideos) {
                listRowAdapter.add(video);
            }

            HeaderItem header = new HeaderItem(0, getString(R.string.related_videos));
            mAdapter.add(new ListRow(header, listRowAdapter));
            mPresenterSelector.addClassPresenter(ListRow.class, new ListRowPresenter());
        }
    }

    public void conversionStarted() {
        getConversionStatus();
    }

    private int convertDpToPixel(Context context, int dp) {
        float density = context.getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }

    private Action getAction(ActionOption option) {
        for (int i = 0; i < mActionAdapter.size(); i++) {
            Action action = (Action) mActionAdapter.get(i);

            if (action.getId() == option.getId()) {
                return action;
            }
        }

        return null;
    }

    private void updateActions(Action action) {
        int position = mActionAdapter.indexOf(action);
        mActionAdapter.notifyItemRangeChanged(position, position);
    }

    private class OnConvertResponse extends Response {
        @Override
        public void onSuccess(JsonObject result) {
            int percentDone = -1;

            try {
                percentDone = result.get("mp4").getAsJsonObject().get("percent_done").getAsInt();
            } catch (UnsupportedOperationException | NullPointerException e) {

            }

            if (percentDone >= 0) {
                Action action = getAction(ActionOption.CONVERT);

                if (action != null) {
                    if (percentDone < 100) {
                        action.setLabel2(percentDone + "%");
                        updateActions(action);

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                getConversionStatus();
                            }
                        }, 1000);
                    } else {
                        mActionAdapter.remove(action);
                        updateActions(action);
                        mVideo.setConverted(true);
                    }
                }
            }
        }
    }

    private class OnResumeResponse extends Response {
        @Override
        public void onSuccess(JsonObject result) {
            try {
                long resumeTime = result.get("start_from").getAsLong();
                mVideo.setResumeTime(resumeTime);
            } catch (UnsupportedOperationException | NullPointerException e) {
                mVideo.setResumeTime(0);
            }

            if (mVideo.getResumeTime() > 0) {
                Action action = getAction(ActionOption.RESUME);

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
        }
    }

    private class OnActionClicked implements OnActionClickedListener {
        @Override
        public void onActionClicked(Action action) {
            ActionOption option = ActionOption.fromId(action.getId());

            switch (option) {
                case WATCH:
                    if (mListener != null) {
                        mListener.onWatchClicked(mVideo);
                    }
                    break;
                case CONVERT:
                    if (mListener != null) {
                        mListener.onConvertClicked(mVideo);
                    }
                    break;
                case RESUME:
                    if (mListener != null) {
                        mListener.onResumeClick(mVideo);
                    }
                    break;
            }
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

        public OnThumbLoaded(int width, int height, DetailsOverviewRow row) {
            super(width, height);
            mRow = row;
        }

        @Override
        public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
            mRow.setImageDrawable(resource);
            mAdapter.notifyArrayItemRangeChanged(0, mAdapter.size());
        }
    }
}