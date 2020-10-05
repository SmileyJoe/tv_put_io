/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.smileyjoe.putio.tv.ui.fragment;

import android.annotation.TargetApi;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.leanback.app.VideoSupportFragment;
import androidx.leanback.app.VideoSupportFragmentGlueHost;
import androidx.leanback.media.PlaybackGlue;

import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ext.leanback.LeanbackPlayerAdapter;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import io.smileyjoe.putio.tv.R;
import io.smileyjoe.putio.tv.network.Putio;
import io.smileyjoe.putio.tv.object.Video;
import io.smileyjoe.putio.tv.ui.activity.PlaybackActivity;
import io.smileyjoe.putio.tv.util.VideoPlayerGlue;

/**
 * https://github.com/googlearchive/androidtv-Leanback/blob/master/app/src/main/java/com/example/android/tvleanback/ui/PlaybackFragment.java
 * <p>
 * Plays selected video, loads playlist and related videos, and delegates playback to {@link
 * VideoPlayerGlue}.
 */
public class PlaybackVideoFragment extends VideoSupportFragment {

    public interface Listener{
        void onPlayComplete(Video video);
    }

    private static final int UPDATE_DELAY = 16;

    private VideoPlayerGlue mPlayerGlue;
    private LeanbackPlayerAdapter mPlayerAdapter;
    private SimpleExoPlayer mPlayer;
    private TrackSelector mTrackSelector;
    private Video mVideo;
    private boolean mShouldResume;
    private boolean mInitialized = false;
    private Listener mListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mShouldResume = getActivity().getIntent().getBooleanExtra(PlaybackActivity.EXTRA_SHOULD_RESUME, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if(getActivity() instanceof Listener){
            mListener = (Listener) getActivity();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (Util.SDK_INT > 23) {
            initializePlayer();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if ((Util.SDK_INT <= 23 || mPlayer == null)) {
            initializePlayer();
        }
    }

    /**
     * Pauses the player.
     */
    @TargetApi(Build.VERSION_CODES.N)
    @Override
    public void onPause() {
        super.onPause();

        if (mPlayerGlue != null && mPlayerGlue.isPlaying()) {
            mPlayerGlue.pause();
        }
        if (Util.SDK_INT <= 23) {
            releasePlayer();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (Util.SDK_INT > 23) {
            releasePlayer();
        }
    }

    private void initializePlayer() {
        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelection.Factory videoTrackSelectionFactory =
                new AdaptiveTrackSelection.Factory(bandwidthMeter);
        mTrackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);

        mPlayer = ExoPlayerFactory.newSimpleInstance(getActivity(), mTrackSelector);
        mPlayerAdapter = new LeanbackPlayerAdapter(getActivity(), mPlayer, UPDATE_DELAY);
        mPlayerGlue = new VideoPlayerGlue(getActivity(), mPlayerAdapter, null);
        mPlayerGlue.setHost(new VideoSupportFragmentGlueHost(this));
        mPlayerGlue.playWhenPrepared();

        mInitialized = true;

        if(mVideo != null) {
            play(mVideo);
        }
    }

    private void releasePlayer() {
        if (mPlayer != null) {
            mPlayer.release();
            mPlayer = null;
            mTrackSelector = null;
            mPlayerGlue = null;
            mPlayerAdapter = null;
        }
    }

    public void play(Video video) {
        mVideo = video;

        if(mInitialized) {
            mPlayerGlue.setTitle(video.getTitle());
            mPlayerGlue.setSubtitle(video.getTitle());

            prepareMediaForPlaying(video.getStreamUri());

            mPlayerGlue.addPlayerCallback(new PlayerCallback());

            mPlayerGlue.play();
        }
    }

    private void prepareMediaForPlaying(Uri mediaSourceUri) {
        String userAgent = Util.getUserAgent(getActivity(), getContext().getString(R.string.app_name));
        MediaSource mediaSource =
                new ExtractorMediaSource(
                        mediaSourceUri,
                        new DefaultDataSourceFactory(getActivity(), userAgent),
                        new DefaultExtractorsFactory(),
                        null,
                        null);

        mPlayer.prepare(mediaSource);
    }

    private class PlayerCallback extends PlaybackGlue.PlayerCallback {
        @Override
        public void onPreparedStateChanged(PlaybackGlue glue) {
            super.onPreparedStateChanged(glue);

            if (mShouldResume) {
                mPlayerGlue.seekTo(mVideo.getResumeTime() * 1000);
                // we only want to do this after the first load //
                mShouldResume = false;
            }
        }

        @Override
        public void onPlayStateChanged(PlaybackGlue glue) {
            super.onPlayStateChanged(glue);

            if (getSurfaceView() != null) {
                if (mPlayerGlue.isPlaying()) {
                    getSurfaceView().setKeepScreenOn(true);
                } else {
                    getSurfaceView().setKeepScreenOn(false);

                    Putio.setResumeTime(getContext(), mVideo.getPutId(), mPlayerGlue.getCurrentPosition() / 1000, null);
                }
            }
        }

        @Override
        public void onPlayCompleted(PlaybackGlue glue) {
            super.onPlayCompleted(glue);

            if(mListener != null){
                mListener.onPlayComplete(mVideo);
            }
        }
    }
}