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
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.leanback.app.VideoSupportFragment;
import androidx.leanback.app.VideoSupportFragmentGlueHost;
import androidx.leanback.media.PlaybackGlue;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.PlaybackException;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.TracksInfo;
import com.google.android.exoplayer2.audio.AudioSink;
import com.google.android.exoplayer2.ext.leanback.LeanbackPlayerAdapter;
import com.google.android.exoplayer2.source.TrackGroup;
import com.google.android.exoplayer2.trackselection.TrackSelectionOverrides;
import com.google.android.exoplayer2.ui.SubtitleView;
import com.google.android.exoplayer2.util.EventLogger;
import com.google.android.exoplayer2.util.MimeTypes;
import com.google.android.exoplayer2.util.Util;
import com.google.common.collect.ImmutableList;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;
import java.util.stream.Collectors;

import io.smileyjoe.putio.tv.R;
import io.smileyjoe.putio.tv.channel.ChannelType;
import io.smileyjoe.putio.tv.channel.Channels;
import io.smileyjoe.putio.tv.network.Putio;
import io.smileyjoe.putio.tv.object.MediaType;
import io.smileyjoe.putio.tv.object.Video;
import io.smileyjoe.putio.tv.object.VideoType;
import io.smileyjoe.putio.tv.ui.activity.PlaybackActivity;
import io.smileyjoe.putio.tv.util.VideoPlayerGlue;
import io.smileyjoe.putio.tv.util.YoutubeUtil;

/**
 * https://github.com/googlearchive/androidtv-Leanback/blob/master/app/src/main/java/com/example/android/tvleanback/ui/PlaybackFragment.java
 * <p>
 * Plays selected video, loads playlist and related videos, and delegates playback to {@link
 * VideoPlayerGlue}.
 */
public class PlaybackVideoFragment extends VideoSupportFragment implements VideoPlayerGlue.OnActionClickedListener, YoutubeUtil.Listener {

    public interface Listener {
        void onPlayComplete(Video video);
        void onControlsVisibilityChanged(boolean isShown);
        void onSubtitlesClicked();
        void showError();
        void onNextClicked(Video current);
        void onPreviousClicked(Video current);
        void onAudioTracksClicked(TracksInfo tracksInfo);
        void showConversion();
        SubtitleView getSubtitleView();
    }

    private static final int UPDATE_DELAY = 16;

    private VideoPlayerGlue mPlayerGlue;
    private LeanbackPlayerAdapter mPlayerAdapter;
    private ExoPlayer mPlayer;
    private Video mVideo;
    private boolean mShouldResume;
    private boolean mInitialized = false;
    private Optional<Listener> mListener = Optional.empty();
    private BroadcastTick mBroadcastTick;
    private YoutubeUtil mYoutube;
    private String mYoutubeUrl;
    private boolean mShowNextPrevious;
    private boolean mPlayMp4 = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mShouldResume = getActivity().getIntent().getBooleanExtra(PlaybackActivity.EXTRA_SHOULD_RESUME, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getActivity() instanceof Listener) {
            mListener = Optional.ofNullable((Listener) getActivity());
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

        mListener.ifPresent(listener -> listener.onControlsVisibilityChanged(true));
    }

    @Override
    public void hideControlsOverlay(boolean runAnimation) {
        super.hideControlsOverlay(runAnimation);

        mListener.ifPresent(listener -> listener.onControlsVisibilityChanged(false));
    }

    public void showNextPrevious() {
        mShowNextPrevious = true;
        if (mPlayerGlue != null) {
            mPlayerGlue.showNextPrevious();
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
        updateResume();
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
        mPlayer = new ExoPlayer.Builder(getContext())
                .build();

        mPlayer.setTrackSelectionParameters(
                mPlayer.getTrackSelectionParameters()
                        .buildUpon()
                        .setPreferredAudioLanguage("en")
                        .setMaxAudioChannelCount(6)
                        .build());

        mPlayer.addAnalyticsListener(new EventLogger(null));
        mPlayer.addListener(new PlayerListener());
        mListener.ifPresent(listener -> mPlayer.addListener(listener.getSubtitleView()));

        mPlayerAdapter = new LeanbackPlayerAdapter(getActivity(), mPlayer, UPDATE_DELAY);
        mPlayerGlue = new VideoPlayerGlue(getActivity(), mPlayerAdapter, this);
        mPlayerGlue.setHost(new VideoSupportFragmentGlueHost(this));
        if (mShowNextPrevious) {
            mPlayerGlue.showNextPrevious();
        }
        mPlayerGlue.playWhenPrepared();

        mInitialized = true;

        if (mVideo != null) {
            play(mVideo);
        }
    }

    private void releasePlayer() {
        if (mPlayer != null) {
            mPlayer.release();
            mPlayer = null;
            mPlayerGlue = null;
            mPlayerAdapter = null;
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if (mVideo != null) {
            play(mVideo);
        }

        mYoutube = new YoutubeUtil(context);
        mYoutube.setListener(this);
        if (!TextUtils.isEmpty(mYoutubeUrl)) {
            play(mYoutubeUrl);
            mYoutubeUrl = null;
        }
    }

    public void play(String youtubeUrl) {
        if (mYoutube != null) {
            mYoutube.extract(youtubeUrl);
        } else {
            mYoutubeUrl = youtubeUrl;
        }
    }

    private void play(Video video) {
        play(video, mPlayMp4);
    }

    public void play(Video video, boolean playMp4) {
        mPlayMp4 = playMp4;
        if (mPlayer != null) {
            play(video, null);
        } else {
            mVideo = video;
        }
    }

    private void play(Video video, Uri subtitleUri) {
        mVideo = video;

        if (mInitialized) {
            String title = video.getTitleFormatted(getContext(), true) + (mPlayMp4 ? " (MP4)" : "");
            mPlayerGlue.setTitle(title);
            mPlayerGlue.setMediaType(MediaType.VIDEO);

            if (mPlayMp4) {
                mPlayerGlue.removePlayMp4Action();
            } else {
                if (mVideo.getStreamMp4Uri() != null) {
                    mPlayerGlue.showPlayMp4Action();
                }
            }

            prepareMediaForPlaying(video.getStreamUri(mPlayMp4), subtitleUri);

            mPlayerGlue.addPlayerCallback(new PlayerCallback());

            if (mShouldResume) {
                mPlayerGlue.seekTo(mVideo.getResumeTime() * 1000);
            }

            mPlayerGlue.play();
            addToDefaultChannel();
        }
    }

    private void addToDefaultChannel() {
        if (mVideo.getVideoType() == VideoType.MOVIE) {
            Channels.addProgramme(getContext(), ChannelType.DEFAULT, mVideo);
        }
    }

    @Override
    public void onYoutubeExtracted(String title, String videoUrl) {
        play(title, videoUrl);
    }

    @Override
    public void onYoutubeFailed() {
        mListener.ifPresent(listener -> listener.showError());
    }

    private void play(String title, String videoUrl) {
        if (mInitialized) {
            mPlayerGlue.setTitle(title);
            mPlayerGlue.setMediaType(MediaType.YOUTUBE);

            prepareMediaForPlaying(videoUrl);

            mPlayerGlue.addPlayerCallback(new PlayerCallback());

            mPlayerGlue.play();
        }
    }

    @Override
    public void onPrevious() {
        mListener.ifPresent(listener -> listener.onPreviousClicked(mVideo));
    }

    @Override
    public void onNext() {
        mListener.ifPresent(listener -> listener.onNextClicked(mVideo));
    }

    @Override
    public void onSubtitles() {
        mListener.ifPresent(listener -> listener.onSubtitlesClicked());
    }

    @Override
    public void onAudioTrack() {
        mListener.ifPresent(listener -> listener.onAudioTracksClicked(mPlayer.getCurrentTracksInfo()));
    }

    @Override
    public void onPlayMp4() {
        play(mVideo, true);
    }

    public void showSubtitles(Uri uri) {
        updateResume();
        play(mVideo, uri);
    }

    public void loadTrack(TrackGroup trackGroup) {
        if (trackGroup != null) {
            TrackSelectionOverrides overrides =
                    new TrackSelectionOverrides.Builder()
                            .setOverrideForType(new TrackSelectionOverrides.TrackSelectionOverride(trackGroup))
                            .build();


            mPlayer.setTrackSelectionParameters(
                    mPlayer.getTrackSelectionParameters()
                            .buildUpon().setTrackSelectionOverrides(overrides).build());
        }
    }

    private void prepareMediaForPlaying(String youtubeVideoUrl) {
        mPlayer.setMediaItem(new MediaItem.Builder()
                .setUri(Uri.parse(youtubeVideoUrl))
                .build());

        mPlayer.prepare();
    }

    private void prepareMediaForPlaying(Uri mediaSourceUri, Uri subtitleUri) {
        mPlayerGlue.resetActions();

        MediaItem.Builder mediaBuilder = new MediaItem.Builder()
                .setUri(mediaSourceUri);

        if (subtitleUri != null) {
            MediaItem.SubtitleConfiguration subtitle =
                    new MediaItem.SubtitleConfiguration.Builder(subtitleUri)
                            .setMimeType(MimeTypes.APPLICATION_SUBRIP) // The correct MIME type (required).
                            .setSelectionFlags(C.SELECTION_FLAG_DEFAULT)
                            .setRoleFlags(C.ROLE_FLAG_SUBTITLE)
                            .build();
            mediaBuilder.setSubtitleConfigurations(ImmutableList.of(subtitle));
        }

        mPlayer.setMediaItem(mediaBuilder.build());

        mPlayer.prepare();
    }

    private void populateEndTime() {
        long current = mPlayerGlue.getCurrentPosition();
        long total = mPlayerGlue.getDuration();
        long left = total - current;
        Date now = new Date();
        now.setTime(now.getTime() + left);
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");

        mPlayerGlue.setSubtitle(getString(R.string.text_ends_at, dateFormat.format(now)));
    }

    private void updateResume() {
        if (mVideo != null) {
            int resumeTime = Math.toIntExact(mPlayerGlue.getCurrentPosition() / 1000);
            Putio.Resume.set(getContext(), mVideo.getPutId(), resumeTime, null);
            mShouldResume = true;
            mVideo.setResumeTime(resumeTime);
            addToDefaultChannel();
        }
    }

    private class BroadcastTick extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().compareTo(Intent.ACTION_TIME_TICK) == 0) {
                if (mPlayerGlue != null && !mPlayerGlue.isPlaying()) {
                    populateEndTime();
                }
            }
        }
    }

    private class PlayerListener implements Player.Listener {
        @Override
        public void onRenderedFirstFrame() {
            mPlayerGlue.showAudioTrackSelection();
            populateEndTime();
            // we only want to do this after the first load //
            mShouldResume = false;
        }

        @Override
        public void onPlayerError(PlaybackException error) {
            boolean tryingAgain = false;
            /**
             * Between exoplayer 2.16.1 and 2.17.0 they decided to change the way track selection
             * works:
             * <br/>
             * https://github.com/google/ExoPlayer/releases/tag/r2.17.0
             * Prefer audio content preferences (for example, the "default" audio track or a track
             * matching the system locale language) over technical track selection constraints
             * (for example, preferred MIME type, or maximum channel count).
             * <br/>
             * This meant that when we set
             * mPlayer.getTrackSelectionParameters().setMaxAudioChannelCount(6) it was just ignored,
             * even though it's still a valid function on the player. In testing, the player
             * was now trying to load "TrueHD Atmos 7.1" which has 8 channels, which it picked up
             * as being supported, however it would always throw a AudioSink.InitializationException.
             * <br/>
             * This would then cause the app the load the mp4 version, as that's the fail safe
             * for a video not working.
             * <br/>
             * What happens now is, if this exception is thrown, all the tracks are
             * checked, and then next lowest channel count track is attempted to be played.
             * If there are no lower tracks, the mp4 version is loaded.
             * <br/>
             * Seems like a very unnecessary change for the library to make, hopefully it is a bug
             * and will be reverted and all this can be removed.
             */
            if (error.getCause() instanceof AudioSink.InitializationException) {
                TracksInfo tracksInfo = mPlayer.getCurrentTracksInfo();

                // get all valid tracks //
                ArrayList<TracksInfo.TrackGroupInfo> validGroups = tracksInfo.getTrackGroupInfos().stream()
                        .filter(groupInfo -> groupInfo.getTrackType() == 1 && groupInfo.isSupported())
                        .filter(groupInfo -> {
                            TrackGroup group = groupInfo.getTrackGroup();
                            return group != null && group.length > 0;
                        })
                        .collect(Collectors.toCollection(ArrayList::new));

                // get the selected tracks channel count, or -1 //
                int selectedChannelCount = validGroups.stream()
                        .filter(TracksInfo.TrackGroupInfo::isSelected)
                        .findFirst()
                        .map(trackGroupInfo -> trackGroupInfo.getTrackGroup().getFormat(0).channelCount)
                        .orElse(-1);

                // if there is a selected track channel count //
                if (selectedChannelCount > 0) {
                    // get the next track down, filter for all tracks below the current, then get the max //
                    TracksInfo.TrackGroupInfo nextGroup = validGroups.stream()
                            .filter(trackGroupInfo -> trackGroupInfo.getTrackGroup().getFormat(0).channelCount < selectedChannelCount)
                            .max((l, r) -> l.getTrackGroup().getFormat(0).channelCount > r.getTrackGroup().getFormat(0).channelCount ? 1 : -1)
                            .orElse(null);

                    // load the track and play the video //
                    if (nextGroup != null) {
                        loadTrack(nextGroup.getTrackGroup());
                        mPlayerGlue.play();
                        tryingAgain = true;
                    }
                }
            }

            if (!tryingAgain) {
                if (mPlayMp4) {
                    mListener.ifPresent(listener -> listener.showError());
                } else if (mVideo.getStreamMp4Uri() == null) {
                    mListener.ifPresent(listener -> listener.showConversion());
                } else {
                    play(mVideo, true);
                }
            }
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

                    updateResume();
                }
            }
        }

        @Override
        public void onPlayCompleted(PlaybackGlue glue) {
            super.onPlayCompleted(glue);

            if (mPlayerGlue.getDuration() < 0) {
                mPlayer.retry();
            } else if (mListener.isPresent()) {
                mListener.get().onPlayComplete(mVideo);
            }
        }
    }
}