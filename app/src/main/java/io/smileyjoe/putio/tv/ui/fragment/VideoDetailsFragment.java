package io.smileyjoe.putio.tv.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

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
import androidx.core.content.ContextCompat;

import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import java.util.ArrayList;

import io.smileyjoe.putio.tv.ui.viewholder.CardPresenter;
import io.smileyjoe.putio.tv.ui.viewholder.DetailsDescriptionPresenter;
import io.smileyjoe.putio.tv.R;
import io.smileyjoe.putio.tv.putio.File;
import io.smileyjoe.putio.tv.ui.activity.DetailsActivity;
import io.smileyjoe.putio.tv.ui.activity.MainActivity;

/*
 * LeanbackDetailsFragment extends DetailsFragment, a Wrapper fragment for leanback details screens.
 * It shows a detailed view of video and its meta plus related videos.
 */
public class VideoDetailsFragment extends DetailsFragment {

    public interface Listener{
        void onWatchClicked(File file);
        void onConvertClicked(File file);
        void onRelatedClicked(File file, ArrayList<File> relatedVideos);
    }

    private enum ActionOption{
        WATCH(1, R.string.action_watch),
        CONVERT(2, R.string.action_convert);

        private long mId;
        private @StringRes int mTitleResId;

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

        public static ActionOption fromId(long id){
            for(ActionOption option:values()){
                if(option.getId() == id){
                    return option;
                }
            }

            return null;
        }
    }

    private static final int DETAIL_THUMB_WIDTH = 274;
    private static final int DETAIL_THUMB_HEIGHT = 274;

    private static final int NUM_COLS = 10;

    private File mFile;
    private ArrayList<File> mRelatedVideos;

    private ArrayObjectAdapter mAdapter;
    private ClassPresenterSelector mPresenterSelector;

    private DetailsFragmentBackgroundController mDetailsBackground;

    private Listener mListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDetailsBackground = new DetailsFragmentBackgroundController(this);

        mFile = getActivity().getIntent().getParcelableExtra(DetailsActivity.VIDEO);
        mRelatedVideos = getActivity().getIntent().getParcelableArrayListExtra(DetailsActivity.RELATED_VIDEOS);

        if (mFile != null) {
            mPresenterSelector = new ClassPresenterSelector();
            mAdapter = new ArrayObjectAdapter(mPresenterSelector);
            setupDetailsOverviewRow();
            setupDetailsOverviewRowPresenter();
            setupRelatedVideoListRow();
            setAdapter(mAdapter);
            initializeBackground(mFile);
            setOnItemViewClickedListener(new OnRelatedItemClick());
        } else {
            Intent intent = new Intent(getActivity(), MainActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if(getActivity() instanceof Listener){
            mListener = (Listener) getActivity();
        }
    }

    private void initializeBackground(File file) {
        mDetailsBackground.enableParallax();
        Glide.with(getActivity())
                .load(Uri.parse(file.getScreenShot()))
                .asBitmap()
                .centerCrop()
                .error(R.drawable.default_background)
                .into(new OnBackgroundLoaded());
    }

    private void setupDetailsOverviewRow() {
        DetailsOverviewRow row = new DetailsOverviewRow(mFile);
        row.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.default_background));

        loadThumb(row);
        addActions(row);

        mAdapter.add(row);
    }

    private void loadThumb(DetailsOverviewRow row){
        int width = convertDpToPixel(getActivity().getApplicationContext(), DETAIL_THUMB_WIDTH);
        int height = convertDpToPixel(getActivity().getApplicationContext(), DETAIL_THUMB_HEIGHT);

        Glide.with(getActivity())
                .load(Uri.parse(mFile.getScreenShot()))
                .centerCrop()
                .error(R.drawable.default_background)
                .into(new OnThumbLoaded(width, height, row));
    }

    private void addActions(DetailsOverviewRow row){
        ArrayObjectAdapter actionAdapter = new ArrayObjectAdapter();

        for(ActionOption option:ActionOption.values()){
            Action action = new Action(option.getId(), getResources().getString(option.getTitleResId()));
            actionAdapter.add(action);
        }

        row.setActionsAdapter(actionAdapter);
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
        if(mRelatedVideos != null && !mRelatedVideos.isEmpty()) {
            ArrayObjectAdapter listRowAdapter = new ArrayObjectAdapter(new CardPresenter());
            for (File file:mRelatedVideos) {
                listRowAdapter.add(file);
            }

            HeaderItem header = new HeaderItem(0, getString(R.string.related_videos));
            mAdapter.add(new ListRow(header, listRowAdapter));
            mPresenterSelector.addClassPresenter(ListRow.class, new ListRowPresenter());
        }
    }

    private int convertDpToPixel(Context context, int dp) {
        float density = context.getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }

    private class OnActionClicked implements OnActionClickedListener{
        @Override
        public void onActionClicked(Action action) {
            ActionOption option = ActionOption.fromId(action.getId());

            switch (option){
                case WATCH:
                    if(mListener != null){
                        mListener.onWatchClicked(mFile);
                    }
                    break;
                case CONVERT:
                    if(mListener != null){
                        mListener.onConvertClicked(mFile);
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

            if(mListener != null && item instanceof File){
                mListener.onRelatedClicked((File) item, mRelatedVideos);
            }
        }
    }

    private class OnBackgroundLoaded extends SimpleTarget<Bitmap>{
        @Override
        public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
            mDetailsBackground.setCoverBitmap(resource);
            mAdapter.notifyArrayItemRangeChanged(0, mAdapter.size());
        }
    }

    private class OnThumbLoaded extends SimpleTarget<GlideDrawable>{

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
