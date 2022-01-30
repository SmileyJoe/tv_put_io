package io.smileyjoe.putio.tv.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;

import io.smileyjoe.putio.tv.R;
import io.smileyjoe.putio.tv.databinding.ActivitySeriesBinding;
import io.smileyjoe.putio.tv.interfaces.Folder;
import io.smileyjoe.putio.tv.object.FragmentType;
import io.smileyjoe.putio.tv.object.HistoryItem;
import io.smileyjoe.putio.tv.object.Video;
import io.smileyjoe.putio.tv.ui.adapter.VideosAdapter;
import io.smileyjoe.putio.tv.ui.fragment.SeasonDetailsFragment;
import io.smileyjoe.putio.tv.ui.fragment.VideosFragment;
import io.smileyjoe.putio.tv.ui.viewholder.VideosViewHolder;
import io.smileyjoe.putio.tv.util.FragmentUtil;
import io.smileyjoe.putio.tv.util.VideoLoader;

public class SeriesActivity extends BaseActivity<ActivitySeriesBinding> implements VideoLoader.Listener{

    private static final String EXTRA_SERIES = "series";

    private SeasonDetailsFragment mFragmentSeasonDetails;
    private VideosFragment mFragmentVideoList;
    private VideoLoader mVideoLoader;

    public static Intent getIntent(Context context, Video series){
        Intent intent = new Intent(context, SeriesActivity.class);

        intent.putExtra(EXTRA_SERIES, series);

        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mFragmentVideoList = (VideosFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_video_list);
        mFragmentVideoList.setType(FragmentType.VIDEO);
        mFragmentVideoList.setStyle(VideosViewHolder.Style.LIST);
        mFragmentSeasonDetails = (SeasonDetailsFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_season_details);

        mFragmentVideoList.setListener(new VideoListListener());

        mVideoLoader = VideoLoader.getInstance(getApplicationContext(), this);

        handleExtras();
    }

    @Override
    protected ActivitySeriesBinding inflate() {
        return ActivitySeriesBinding.inflate(getLayoutInflater());
    }

    private void handleExtras(){
        Bundle extras = getIntent().getExtras();

        if(extras != null){
            if(extras.containsKey(EXTRA_SERIES)){
                Video series = extras.getParcelable(EXTRA_SERIES);
                mFragmentSeasonDetails.update(series);

                Glide.with(getBaseContext())
                        .load(series.getPosterAsUri())
                        .dontAnimate()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(mView.imagePoster);

                mVideoLoader.loadDirectory(series.getPutId(), series.getTitle());
            }
        }
    }

    @Override
    public void update(Video video) {
        mFragmentVideoList.update(video);
    }

    @Override
    public void onVideosLoadStarted() {
        mView.frameLoading.setVisibility(View.VISIBLE);
        FragmentUtil.hideFragment(getSupportFragmentManager(), mFragmentVideoList);
    }

    @Override
    public void onVideosLoadFinished(HistoryItem item, ArrayList<Video> videos, ArrayList<Folder> folders, boolean shouldAddToHistory) {
        mView.frameLoading.setVisibility(View.GONE);
        FragmentUtil.showFragment(getSupportFragmentManager(), mFragmentVideoList);
        mFragmentVideoList.setVideos(videos);
    }

    private class VideoListListener implements VideosAdapter.Listener<Video>{
        @Override
        public void onItemClicked(View view, Video item) {
            startActivity(PlaybackActivity.getIntent(getBaseContext(), mFragmentVideoList.getVideos(), item, true));
        }

        @Override
        public void hasFocus(FragmentType type, Video item, View view, int position) {
            // do nothing //
        }
    }
}
