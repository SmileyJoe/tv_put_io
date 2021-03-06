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
    }

    private final OnActionClickedListener mActionListener;

    private PlaybackControlsRow.RepeatAction mRepeatAction;
    private PlaybackControlsRow.ThumbsUpAction mThumbsUpAction;
    private PlaybackControlsRow.ThumbsDownAction mThumbsDownAction;
    private PlaybackControlsRow.SkipPreviousAction mSkipPreviousAction;
    private PlaybackControlsRow.SkipNextAction mSkipNextAction;
    private PlaybackControlsRow.FastForwardAction mFastForwardAction;
    private PlaybackControlsRow.RewindAction mRewindAction;
    private SubtitlesAction mSubtitlesAction;
    private ArrayObjectAdapter mPrimaryActionsAdapter;
    private ArrayObjectAdapter mSecondaryActionsAdapter;

    public VideoPlayerGlue(
            Context context,
            LeanbackPlayerAdapter playerAdapter,
            OnActionClickedListener actionListener) {
        super(context, playerAdapter);

        mActionListener = actionListener;

        mSkipPreviousAction = new PlaybackControlsRow.SkipPreviousAction(context);
        mSkipNextAction = new PlaybackControlsRow.SkipNextAction(context);
        mFastForwardAction = new PlaybackControlsRow.FastForwardAction(context);
        mRewindAction = new PlaybackControlsRow.RewindAction(context);
        mSubtitlesAction = new SubtitlesAction();

        mThumbsUpAction = new PlaybackControlsRow.ThumbsUpAction(context);
        mThumbsUpAction.setIndex(PlaybackControlsRow.ThumbsUpAction.INDEX_OUTLINE);
        mThumbsDownAction = new PlaybackControlsRow.ThumbsDownAction(context);
        mThumbsDownAction.setIndex(PlaybackControlsRow.ThumbsDownAction.INDEX_OUTLINE);
        mRepeatAction = new PlaybackControlsRow.RepeatAction(context);

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
//        adapter.add(mSkipPreviousAction);
//        adapter.add(mRewindAction);
//        adapter.add(mFastForwardAction);
//        adapter.add(mSkipNextAction);
    }

    @Override
    protected void onCreateSecondaryActions(ArrayObjectAdapter adapter) {
        super.onCreateSecondaryActions(adapter);
        mSecondaryActionsAdapter = adapter;
//        adapter.add(mThumbsDownAction);
//        adapter.add(mThumbsUpAction);
//        adapter.add(mRepeatAction);
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

    public void setMediaType(MediaType mediaType){
        mSecondaryActionsAdapter.clear();
        switch (mediaType){
            case VIDEO:
                mSecondaryActionsAdapter.add(mSubtitlesAction);
                break;
            case YOUTUBE:
                break;
        }
    }

    // Should dispatch actions that the super class does not supply callbacks for.
    private boolean shouldDispatchAction(Action action) {
        return action == mRewindAction
                || action == mFastForwardAction
                || action == mThumbsDownAction
                || action == mThumbsUpAction
                || action == mRepeatAction
                || action == mSubtitlesAction;
    }

    private void dispatchAction(Action action) {
        // Primary actions are handled manually.
        if (action == mRewindAction) {
            rewind();
        } else if (action == mFastForwardAction) {
            fastForward();
        } else if(action == mSubtitlesAction){
            if(mActionListener != null){
                mActionListener.onSubtitles();
            }
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

    private void notifyActionChanged(
            PlaybackControlsRow.MultiAction action, ArrayObjectAdapter adapter) {
        if (adapter != null) {
            int index = adapter.indexOf(action);
            if (index >= 0) {
                adapter.notifyArrayItemRangeChanged(index, 1);
            }
        }
    }

    @Override
    public void next() {
        mActionListener.onNext();
    }

    @Override
    public void previous() {
        mActionListener.onPrevious();
    }

    /**
     * Skips backwards 10 seconds.
     */
    public void rewind() {
        long newPosition = getCurrentPosition() - TEN_SECONDS;
        newPosition = (newPosition < 0) ? 0 : newPosition;
        getPlayerAdapter().seekTo(newPosition);
    }

    /**
     * Skips forward 10 seconds.
     */
    public void fastForward() {
        if (getDuration() > -1) {
            long newPosition = getCurrentPosition() + TEN_SECONDS;
            newPosition = (newPosition > getDuration()) ? getDuration() : newPosition;
            getPlayerAdapter().seekTo(newPosition);
        }
    }

    private class SubtitlesAction extends Action{
        public SubtitlesAction() {
            super(100);
            setLabel1(getContext().getString(R.string.action_subtitle));
            setIcon(ContextCompat.getDrawable(getContext(), R.drawable.ic_subtitles_24));
        }
    }
}