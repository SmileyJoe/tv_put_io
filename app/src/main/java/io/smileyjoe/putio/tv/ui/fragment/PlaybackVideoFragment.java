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
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.leanback.app.VideoSupportFragment;
import androidx.leanback.app.VideoSupportFragmentGlueHost;
import androidx.leanback.media.PlaybackGlue;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ext.leanback.LeanbackPlayerAdapter;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.MergingMediaSource;
import com.google.android.exoplayer2.source.SingleSampleMediaSource;
import com.google.android.exoplayer2.text.Cue;
import com.google.android.exoplayer2.text.TextOutput;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.MimeTypes;
import com.google.android.exoplayer2.util.Util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import io.smileyjoe.putio.tv.R;
import io.smileyjoe.putio.tv.network.Putio;
import io.smileyjoe.putio.tv.object.Video;
import io.smileyjoe.putio.tv.ui.activity.PlaybackActivity;
import io.smileyjoe.putio.tv.util.MediaUtil;
import io.smileyjoe.putio.tv.util.VideoPlayerGlue;
import io.smileyjoe.putio.tv.util.YoutubeUtil;

/**
 * https://github.com/googlearchive/androidtv-Leanback/blob/master/app/src/main/java/com/example/android/tvleanback/ui/PlaybackFragment.java
 * <p>
 * Plays selected video, loads playlist and related videos, and delegates playback to {@link
 * VideoPlayerGlue}.
 */
public class PlaybackVideoFragment extends VideoSupportFragment implements VideoPlayerGlue.OnActionClickedListener, YoutubeUtil.Listener {

    public interface Listener{
        void onPlayComplete(Video video);
        void onControlsVisibilityChanged(boolean isShown);
        void onSubtitlesClicked();
        void showSubtitle(String subTitle);
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
    private BroadcastTick mBroadcastTick;
    private SubtitleOutput mSubtitleOutput;
    private YoutubeUtil mYoutube;
    private MediaUtil mMediaUtil;
    private String mYoutubeUrl;

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

        mBroadcastTick = new BroadcastTick();

        getActivity().registerReceiver(mBroadcastTick, new IntentFilter(Intent.ACTION_TIME_TICK));
    }

    @Override
    public void onResume() {
        super.onResume();
        if ((Util.SDK_INT <= 23 || mPlayer == null)) {
            initializePlayer();
        }
    }

    @Override
    public void showControlsOverlay(boolean runAnimation) {
        super.showControlsOverlay(runAnimation);

        if(mListener != null){
            mListener.onControlsVisibilityChanged(true);
        }
    }

    @Override
    public void hideControlsOverlay(boolean runAnimation) {
        super.hideControlsOverlay(runAnimation);

        if(mListener != null){
            mListener.onControlsVisibilityChanged(false);
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

        if (mBroadcastTick != null) {
            getActivity().unregisterReceiver(mBroadcastTick);
        }
    }

    private void initializePlayer() {
        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelection.Factory videoTrackSelectionFactory =
                new AdaptiveTrackSelection.Factory(bandwidthMeter);
        mTrackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);

        mPlayer = ExoPlayerFactory.newSimpleInstance(getActivity(), mTrackSelector);
        mPlayer.addVideoListener(new VideoListener());
        mPlayerAdapter = new LeanbackPlayerAdapter(getActivity(), mPlayer, UPDATE_DELAY);
        mPlayerGlue = new VideoPlayerGlue(getActivity(), mPlayerAdapter, this);
        mPlayerGlue.setHost(new VideoSupportFragmentGlueHost(this));
        mPlayerGlue.playWhenPrepared();
        mSubtitleOutput = new SubtitleOutput();

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

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        mMediaUtil = new MediaUtil(context);
        if(mVideo != null){
            play(mVideo);
        }

        mYoutube = new YoutubeUtil(context);
        mYoutube.setListener(this);
        if(!TextUtils.isEmpty(mYoutubeUrl)){
            play(mYoutubeUrl);
            mYoutubeUrl = null;
        }
    }

    public void play(String youtubeUrl){
        if(mYoutube != null) {
            mYoutube.extract(youtubeUrl);
        } else {
            mYoutubeUrl = youtubeUrl;
        }
    }

    public void play(Video video){
        if(mMediaUtil != null){
            play(video, null);
        } else {
            mVideo = video;
        }
    }

    private void play(Video video, Uri subtitleUri) {
        mVideo = video;

        if(mInitialized) {
            mPlayerGlue.setTitle(video.getTitleFormatted());

            prepareMediaForPlaying(video.getStreamUri(), subtitleUri);

            mPlayerGlue.addPlayerCallback(new PlayerCallback());

            if (mShouldResume) {
                mPlayerGlue.seekTo(mVideo.getResumeTime() * 1000);
                // we only want to do this after the first load //
                mShouldResume = false;
            }

            mPlayerGlue.play();
        }
    }

    @Override
    public void onYoutubeExtracted(String title, String videoUrl, String audioUrl) {
        play(title, videoUrl, audioUrl);
    }

    private void play(String title, String videoUrl, String audioUrl) {
        if(mInitialized) {
            mPlayerGlue.setTitle(title);

            prepareMediaForPlaying(videoUrl, audioUrl);

            mPlayerGlue.addPlayerCallback(new PlayerCallback());

            mPlayerGlue.play();
        }
    }

    @Override
    public void onPrevious() {
        // todo: implement //
    }

    @Override
    public void onNext() {
        // todo: implement //
    }

    @Override
    public void onSubtitles() {
        if(mListener != null){
            mListener.onSubtitlesClicked();
        }
    }

    public void showSubtitles(Uri uri){
        mShouldResume = true;
        mVideo.setResumeTime(mPlayer.getCurrentPosition()/1000);
        play(mVideo, uri);
    }

    private void prepareMediaForPlaying(String youtubeVideoUrl, String youtubeAudioUrl) {
        mMediaUtil.reset();
        mMediaUtil.addMedia(youtubeVideoUrl);
        mMediaUtil.addMedia(youtubeAudioUrl);

        mPlayer.prepare(mMediaUtil.getSource());
    }

    private void prepareMediaForPlaying(Uri mediaSourceUri, Uri subtitleUri) {
        mMediaUtil.reset();
        mMediaUtil.addMedia(mediaSourceUri);

        if(subtitleUri != null){
            mMediaUtil.addSubtitles(subtitleUri);
            mPlayer.addTextOutput(mSubtitleOutput);
        } else {
            mPlayer.removeTextOutput(mSubtitleOutput);

            if(mListener != null){
                mListener.showSubtitle(null);
            }
        }

        mPlayer.prepare(mMediaUtil.getSource());
    }

    private void populateEndTime(){
        long current = mPlayerGlue.getCurrentPosition();
        long total = mPlayerGlue.getDuration();
        long left = total - current;
        Date now = new Date();
        now.setTime(now.getTime() + left);
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");

        mPlayerGlue.setSubtitle(getString(R.string.text_ends_at, dateFormat.format(now)));
    }

    private class SubtitleOutput implements TextOutput{
        @Override
        public void onCues(List<Cue> subtitles) {
            String subtitle = null;

            if(subtitles != null && !subtitles.isEmpty()){
                String text = subtitles.get(0).text.toString();

                if(!TextUtils.isEmpty(text)){
                    subtitle = text;
                }
            }

            if(mListener != null){
                mListener.showSubtitle(subtitle);
            }
        }
    }

    private class BroadcastTick extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().compareTo(Intent.ACTION_TIME_TICK) == 0) {
                if(mPlayerGlue != null && !mPlayerGlue.isPlaying()){
                    populateEndTime();
                }
            }
        }
    }

    private class VideoListener implements com.google.android.exoplayer2.video.VideoListener{
        @Override
        public void onRenderedFirstFrame() {
            populateEndTime();
        }
    }

    private class PlayerCallback extends PlaybackGlue.PlayerCallback {

        @Override
        public void onPlayStateChanged(PlaybackGlue glue) {
            super.onPlayStateChanged(glue);

            if (getSurfaceView() != null) {
                if (mPlayerGlue.isPlaying()) {
                    getSurfaceView().setKeepScreenOn(true);

                    populateEndTime();
                } else {
                    getSurfaceView().setKeepScreenOn(false);

                    if(mVideo != null) {
                        Putio.setResumeTime(getContext(), mVideo.getPutId(), mPlayerGlue.getCurrentPosition() / 1000, null);
                    }
                }
            }
        }

        @Override
        public void onPlayCompleted(PlaybackGlue glue) {
            super.onPlayCompleted(glue);

            if(mPlayerGlue.getDuration() < 0){
                mPlayer.retry();
            } else if(mListener != null){
                mListener.onPlayComplete(mVideo);
            }
        }
    }
}