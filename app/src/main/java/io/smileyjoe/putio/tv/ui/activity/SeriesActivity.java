package io.smileyjoe.putio.tv.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;

import io.smileyjoe.putio.tv.R;
import io.smileyjoe.putio.tv.broadcast.BroadcastVideosReceiver;
import io.smileyjoe.putio.tv.channel.ChannelType;
import io.smileyjoe.putio.tv.channel.Channels;
import io.smileyjoe.putio.tv.databinding.ActivitySeriesBinding;
import io.smileyjoe.putio.tv.interfaces.Folder;
import io.smileyjoe.putio.tv.object.FragmentType;
import io.smileyjoe.putio.tv.object.HistoryItem;
import io.smileyjoe.putio.tv.object.Video;
import io.smileyjoe.putio.tv.ui.adapter.VideosAdapter;
import io.smileyjoe.putio.tv.ui.fragment.SeasonDetailsFragment;
import io.smileyjoe.putio.tv.ui.fragment.VideosFragment;
import io.smileyjoe.putio.tv.util.FragmentUtil;
import io.smileyjoe.putio.tv.util.VideoLoader;

public class SeriesActivity extends BaseActivity<ActivitySeriesBinding> implements BroadcastVideosReceiver, VideoLoader.Listener {

    private static final String EXTRA_SERIES = "series";

    private SeasonDetailsFragment mFragmentSeasonDetails;
    private VideosFragment mFragmentVideoList;
    private VideoLoader mVideoLoader;
    private Video mSeries;

    public static Intent getIntent(Context context, Video series) {
        Intent intent = new Intent(context, SeriesActivity.class);

        intent.putExtra(EXTRA_SERIES, series);

        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mFragmentVideoList = (VideosFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_video_list);
        mFragmentVideoList.setType(FragmentType.VIDEO);
        mFragmentVideoList.setStyle(VideosAdapter.Style.LIST);
        mFragmentSeasonDetails = (SeasonDetailsFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_season_details);

        mFragmentVideoList.setListener(new VideoListListener());

        mVideoLoader = VideoLoader.getInstance(getApplicationContext(), this);

        handleExtras();
    }

    @Override
    public void onResume() {
        super.onResume();
        BroadcastVideosReceiver.super.onResume();

        if (mVideoLoader != null) {
            mVideoLoader.setListener(this);
        } else {
            mVideoLoader = VideoLoader.getInstance(getBaseContext(), this);
        }

        mVideoLoader.loadDirectory(mSeries.getPutId(), mSeries.getTitle());
    }

    @Override
    public void onPause() {
        super.onPause();
        BroadcastVideosReceiver.super.onPause();
    }

    @Override
    protected ActivitySeriesBinding inflate() {
        return ActivitySeriesBinding.inflate(getLayoutInflater());
    }

    private void handleExtras() {
        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            if (extras.containsKey(EXTRA_SERIES)) {
                mSeries = extras.getParcelable(EXTRA_SERIES);
                mFragmentSeasonDetails.update(mSeries);

                Glide.with(getBaseContext())
                        .load(mSeries.getBackdropAsUri())
                        .dontAnimate()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(mView.imagePoster);

                mVideoLoader.loadDirectory(mSeries.getPutId(), mSeries.getTitle());

                Channels.addProgramme(getBaseContext(), ChannelType.DEFAULT, mSeries);
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
        mFragmentVideoList.setVideos(mSeries, videos);
    }

    private class VideoListListener implements VideosFragment.Listener {
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
