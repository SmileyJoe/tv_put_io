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

package io.smileyjoe.putio.tv.util;

import android.content.Context;

import androidx.core.content.ContextCompat;
import androidx.leanback.media.PlaybackTransportControlGlue;
import androidx.leanback.widget.Action;
import androidx.leanback.widget.ArrayObjectAdapter;
import androidx.leanback.widget.PlaybackControlsRow;

import com.google.android.exoplayer2.ext.leanback.LeanbackPlayerAdapter;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import io.smileyjoe.putio.tv.R;
import io.smileyjoe.putio.tv.object.MediaType;

/**
 * https://github.com/googlearchive/androidtv-Leanback/blob/master/app/src/main/java/com/example/android/tvleanback/player/VideoPlayerGlue.java
 * <p>
 * Manages customizing the actions in the {@link PlaybackControlsRow}. Adds and manages the
 * following actions to the primary and secondary controls:
 *
 * <ul>
 *   <li>{@link androidx.leanback.widget.PlaybackControlsRow.RepeatAction}
 *   <li>{@link androidx.leanback.widget.PlaybackControlsRow.ThumbsDownAction}
 *   <li>{@link androidx.leanback.widget.PlaybackControlsRow.ThumbsUpAction}
 *   <li>{@link androidx.leanback.widget.PlaybackControlsRow.SkipPreviousAction}
 *   <li>{@link androidx.leanback.widget.PlaybackControlsRow.SkipNextAction}
 *   <li>{@link androidx.leanback.widget.PlaybackControlsRow.FastForwardAction}
 *   <li>{@link androidx.leanback.widget.PlaybackControlsRow.RewindAction}
 * </ul>
 * <p>
 * Note that the superclass, {@link PlaybackTransportControlGlue}, manages the playback controls
 * row.
 */
public class VideoPlayerGlue extends PlaybackTransportControlGlue<LeanbackPlayerAdapter> {

    private static final long TEN_SECONDS = TimeUnit.SECONDS.toMillis(10);

    /**
     * Listens for when skip to next and previous actions have been dispatched.
     */
    public interface OnActionClickedListener {

        /**
         * Skip to the previous item in the queue.
         */
        void onPrevious();

        /**
         * Skip to the next item in the queue.
         */
        void onNext();
        void onSubtitles();
        void onAudioTrack();
        void onPlayMp4();
    }

    private final Optional<OnActionClickedListener> mActionListener;

    private PlaybackControlsRow.SkipPreviousAction mSkipPreviousAction;
    private PlaybackControlsRow.SkipNextAction mSkipNextAction;
    private SubtitlesAction mSubtitlesAction;
    private ReplayAction mReplayAction;
    private AudioTrackAction mAudioTrackAction;
    private PlayMp4Action mPlayMp4Action;
    private ArrayObjectAdapter mPrimaryActionsAdapter;
    private ArrayObjectAdapter mSecondaryActionsAdapter;

    public VideoPlayerGlue(
            Context context,
            LeanbackPlayerAdapter playerAdapter,
            OnActionClickedListener actionListener) {
        super(context, playerAdapter);

        mActionListener = Optional.ofNullable(actionListener);

        mSkipPreviousAction = new PlaybackControlsRow.SkipPreviousAction(context);
        mSkipNextAction = new PlaybackControlsRow.SkipNextAction(context);
        mSubtitlesAction = new SubtitlesAction();
        mReplayAction = new ReplayAction();
        mAudioTrackAction = new AudioTrackAction();
        mPlayMp4Action = new PlayMp4Action();

        setSeekEnabled(true);
        setControlsOverlayAutoHideEnabled(true);
    }

    @Override
    protected void onCreatePrimaryActions(ArrayObjectAdapter adapter) {
        // Order matters, super.onCreatePrimaryActions() will create the play / pause action.
        // Will display as follows:
        // play/pause, previous, rewind, fast forward, next
        //   > /||      |<        <<        >>         >|
        super.onCreatePrimaryActions(adapter);
        mPrimaryActionsAdapter = adapter;
    }

    @Override
    protected void onCreateSecondaryActions(ArrayObjectAdapter adapter) {
        super.onCreateSecondaryActions(adapter);
        mSecondaryActionsAdapter = adapter;
    }

    @Override
    public void onActionClicked(Action action) {
        if (shouldDispatchAction(action)) {
            dispatchAction(action);
            return;
        }
        // Super class handles play/pause and delegates to abstract methods next()/previous().
        super.onActionClicked(action);
    }

    public void showNextPrevious() {
        mPrimaryActionsAdapter.add(mSkipPreviousAction);
        mPrimaryActionsAdapter.add(mSkipNextAction);
    }

    public void showAudioTrackSelection() {
        mAudioTrackAction.show();
    }

    public void showPlayMp4Action() {
        mPlayMp4Action.show();
    }

    public void removePlayMp4Action() {
        mPlayMp4Action.hide();
    }

    public void resetActions() {
        mAudioTrackAction.reset();
        mPlayMp4Action.reset();
    }

    public void setMediaType(MediaType mediaType) {
        mSecondaryActionsAdapter.clear();
        switch (mediaType) {
            case VIDEO:
                mSecondaryActionsAdapter.add(mSubtitlesAction);
                mSecondaryActionsAdapter.add(mReplayAction);
                break;
            case YOUTUBE:
                break;
        }
    }

    // Should dispatch actions that the super class does not supply callbacks for.
    private boolean shouldDispatchAction(Action action) {
        return action == mSkipNextAction
                || action == mSkipPreviousAction
                || action == mSubtitlesAction
                || action == mReplayAction
                || action == mAudioTrackAction
                || action == mPlayMp4Action;
    }

    private void dispatchAction(Action action) {
        // Primary actions are handled manually.
        if (action == mSkipNextAction) {
            next();
        } else if (action == mSkipPreviousAction) {
            previous();
        } else if (action == mSubtitlesAction) {
            mActionListener.ifPresent(listener -> listener.onSubtitles());
        } else if (action == mReplayAction) {
            replay();
        } else if (action == mAudioTrackAction) {
            mActionListener.ifPresent(listener -> listener.onAudioTrack());
        } else if (action == mPlayMp4Action) {
            mActionListener.ifPresent(listener -> listener.onPlayMp4());
        } else if (action instanceof PlaybackControlsRow.MultiAction) {
            PlaybackControlsRow.MultiAction multiAction = (PlaybackControlsRow.MultiAction) action;
            multiAction.nextIndex();
            // Notify adapter of action changes to handle secondary actions, such as, thumbs up/down
            // and repeat.
            notifyActionChanged(
                    multiAction,
                    (ArrayObjectAdapter) getControlsRow().getSecondaryActionsAdapter());
        }
    }

    private void notifyActionChanged(PlaybackControlsRow.MultiAction action, ArrayObjectAdapter adapter) {
        if (adapter != null) {
            int index = adapter.indexOf(action);
            if (index >= 0) {
                adapter.notifyArrayItemRangeChanged(index, 1);
            }
        }
    }

    @Override
    public void next() {
        mActionListener.ifPresent(listener -> listener.onNext());
    }

    @Override
    public void previous() {
        mActionListener.ifPresent(listener -> listener.onPrevious());
    }

    public void replay() {
        getPlayerAdapter().seekTo(0);
    }

    private class SubtitlesAction extends Action {
        public SubtitlesAction() {
            super(100);
            setLabel1(getContext().getString(R.string.action_subtitle));
            setIcon(ContextCompat.getDrawable(getContext(), R.drawable.ic_subtitles_24));
        }
    }

    private class ReplayAction extends Action {
        public ReplayAction() {
            super(101);
            setLabel1(getContext().getString(R.string.action_replay));
            setIcon(ContextCompat.getDrawable(getContext(), R.drawable.ic_baseline_replay_24));
        }
    }

    private abstract class OptionalAction extends Action {
        public OptionalAction(long id) {
            super(id);
        }

        private boolean mIsAdded = false;

        private void setAdded(boolean added) {
            mIsAdded = added;
        }

        private boolean isAdded() {
            return mIsAdded;
        }

        public void reset() {
            setAdded(false);
        }

        public void show() {
            if (!isAdded()) {
                setAdded(true);
                mSecondaryActionsAdapter.add(this);
            }
        }

        public void hide() {
            if (isAdded()) {
                setAdded(false);
                mSecondaryActionsAdapter.remove(this);
            }
        }
    }

    private class AudioTrackAction extends OptionalAction {

        public AudioTrackAction() {
            super(102);
            setLabel1(getContext().getString(R.string.action_audio_tracks));
            setIcon(ContextCompat.getDrawable(getContext(), R.drawable.ic_audiotrack_24));
        }
    }

    private class PlayMp4Action extends OptionalAction {
        public PlayMp4Action() {
            super(103);
            setLabel1(getContext().getString(R.string.action_play_mp4));
            setIcon(ContextCompat.getDrawable(getContext(), R.drawable.ic_mp4_24));
        }
    }
}