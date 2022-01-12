package io.smileyjoe.putio.tv.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
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
import java.util.List;

import io.smileyjoe.putio.tv.R;
import io.smileyjoe.putio.tv.db.AppDatabase;
import io.smileyjoe.putio.tv.db.GroupDao;
import io.smileyjoe.putio.tv.network.Putio;
import io.smileyjoe.putio.tv.network.Response;
import io.smileyjoe.putio.tv.network.Tmdb;
import io.smileyjoe.putio.tv.object.Character;
import io.smileyjoe.putio.tv.object.DetailsAction;
import io.smileyjoe.putio.tv.object.Group;
import io.smileyjoe.putio.tv.object.GroupType;
import io.smileyjoe.putio.tv.object.Subtitle;
import io.smileyjoe.putio.tv.object.Video;
import io.smileyjoe.putio.tv.object.VideoType;
import io.smileyjoe.putio.tv.ui.activity.VideoDetailsActivity;
import io.smileyjoe.putio.tv.ui.activity.MainActivity;
import io.smileyjoe.putio.tv.ui.viewholder.RelatedVideoCardPresenter;
import io.smileyjoe.putio.tv.ui.viewholder.VideoDetailsDescriptionPresenter;
import io.smileyjoe.putio.tv.util.TmdbUtil;
import io.smileyjoe.putio.tv.util.VideoDetailsHelper;
import io.smileyjoe.putio.tv.util.VideoUtil;

/*
 * LeanbackDetailsFragment extends DetailsFragment, a Wrapper fragment for leanback details screens.
 * It shows a detailed view of video and its meta plus related videos.
 */
public class VideoDetailsFragment extends DetailsFragment implements TmdbUtil.Listener, VideoDetailsHelper.ActionListener{

    public interface Listener {
        void onWatchClicked(Video video, ArrayList<Video> videos);

        void onConvertClicked(Video video);

        void onRelatedClicked(Video video, ArrayList<Video> relatedVideos);

        void onResumeClick(Video video, ArrayList<Video> videos);

        void onTrailerClick(String youtubeUrl);

        void onRefreshDataClicked(Video video);
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
                Action action = new Action(group.getId() + 100, verb, title);
                mActionAdapter.add(action);
            });
        } else {
            Intent intent = new Intent(getActivity(), MainActivity.class);
            startActivity(intent);
        }
    }

    private void getResumeTime() {
        Putio.getResumeTime(getContext(), mVideo.getPutId(), new OnResumeResponse());
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
            Action action = new Action(DetailsAction.TRAILER.getId(), getResources().getString(DetailsAction.TRAILER.getTitleResId()));
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

        for (DetailsAction option : DetailsAction.values()) {
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
                    if(!TextUtils.isEmpty(mVideo.getYoutubeTrailerUrl())){
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

        detailsPresenter.setOnActionClickedListener(new VideoDetailsHelper.OnActionClicked(this));
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

//    public void conversionStarted() {
//        getConversionStatus();
//    }

    private int convertDpToPixel(Context context, int dp) {
        float density = context.getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }

    private Action getAction(int id) {
        return getAction(new Long(id));
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
    public void updateGroupAction(long groupId, int verb) {
        long actionId = groupId + 100;
        Action action = getAction(actionId);
        action.setLabel1(getString(verb));
        updateActions(action);
    }

//    private class GetGroups extends AsyncTask<Void, Void, List<Group>>{
//        @Override
//        protected List<Group> doInBackground(Void... voids) {
//            return AppDatabase.getInstance(getContext()).groupDao().getByType(GroupType.VIDEO.getId());
//        }
//
//        @Override
//        protected void onPostExecute(List<Group> groups) {
//            super.onPostExecute(groups);
//
//            if(groups != null && !groups.isEmpty()){
//                for(Group group:groups){
//                    @StringRes int subTextResId;
//
//                    if(group.getPutIds().contains(mVideo.getPutId())){
//                        subTextResId = R.string.text_remove_from;
//                    } else {
//                        subTextResId = R.string.text_add_to;
//                    }
//
//                    Action action = new Action(group.getId() + 100, getString(subTextResId), group.getTitle());
//                    mActionAdapter.add(action);
//                }
//            }
//        }
//    }

//    private class OnConvertResponse extends Response {
//        @Override
//        public void onSuccess(JsonObject result) {
//            int percentDone = -1;
//
//            try {
//                percentDone = result.get("mp4").getAsJsonObject().get("percent_done").getAsInt();
//            } catch (UnsupportedOperationException | NullPointerException e) {
//
//            }
//
//            if (percentDone >= 0) {
//                Action action = getAction(DetailsAction.CONVERT.getId());
//
//                if (action != null) {
//                    if (percentDone < 100) {
//                        action.setLabel2(percentDone + "%");
//                        updateActions(action);
//
//                        new Handler().postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
//                                getConversionStatus();
//                            }
//                        }, 1000);
//                    } else {
//                        mActionAdapter.remove(action);
//                        updateActions(action);
//                        mVideo.setConverted(true);
//                    }
//                }
//            }
//        }
//    }

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
                Action action = getAction(DetailsAction.RESUME.getId());

                if (action == null) {
                    int currentRange = mActionAdapter.size() - 1;
                    action = new Action(DetailsAction.RESUME.getId(), getResources().getString(DetailsAction.RESUME.getTitleResId()), mVideo.getResumeTimeFormatted());

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
            DetailsAction option = DetailsAction.fromId(action.getId());

            switch (option) {
                case WATCH:
                    if (mListener != null) {
                        mListener.onWatchClicked(mVideo, mRelatedVideos);
                    }
                    break;
                case CONVERT:
                    if (mListener != null) {
                        mListener.onConvertClicked(mVideo);
                    }
                    break;
                case RESUME:
                    if (mListener != null) {
                        mListener.onResumeClick(mVideo, mRelatedVideos);
                    }
                    break;
                case TRAILER:
                    if(mListener != null){
                        mListener.onTrailerClick(mVideo.getYoutubeTrailerUrl());
                    }
                    break;
                case REFRESH_DATA:
                    if(mListener != null){
                        mListener.onRefreshDataClicked(mVideo);
                    }
//                case UNKNOWN:
//                    OnGroupClicked task = new OnGroupClicked(action);
//                    task.execute();
//                    break;
            }
        }
    }

//    private class OnGroupClicked extends AsyncTask<Void, Void, Action>{
//        private Action mAction;
//
//        public OnGroupClicked(Action action) {
//            mAction = action;
//        }
//
//        @Override
//        protected Action doInBackground(Void... voids) {
//            final long id = mAction.getId() - 100;
//
//            if(id == Group.DEFAULT_ID_WATCH_LATER
//                || id == Group.DEFAULT_ID_FAVOURITE) {
//                long putId = mVideo.getPutId();
//                @StringRes int labelOne;
//
//                GroupDao groupDao = AppDatabase.getInstance(getContext()).groupDao();
//                Group group = groupDao.get(id);
//
//                if(group.getPutIds().contains(putId)){
//                    group.removePutId(putId);
//                    labelOne = R.string.text_add_to;
//                } else {
//                    group.addPutId(putId);
//                    labelOne = R.string.text_remove_from;
//                }
//
//                mAction.setLabel1(getString(labelOne));
//                groupDao.insert(group);
//            }
//
//            return mAction;
//        }
//
//        @Override
//        protected void onPostExecute(Action action) {
//            super.onPostExecute(action);
//
//            updateActions(action);
//        }
//    }

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
