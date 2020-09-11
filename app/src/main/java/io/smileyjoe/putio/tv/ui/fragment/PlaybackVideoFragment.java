package io.smileyjoe.putio.tv.ui.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.leanback.app.VideoSupportFragment;
import androidx.leanback.app.VideoSupportFragmentGlueHost;
import androidx.leanback.media.MediaPlayerAdapter;
import androidx.leanback.media.PlaybackGlue;
import androidx.leanback.media.PlaybackTransportControlGlue;
import androidx.leanback.widget.PlaybackControlsRow;

import io.smileyjoe.putio.tv.putio.File;
import io.smileyjoe.putio.tv.putio.Putio;
import io.smileyjoe.putio.tv.ui.activity.PlaybackActivity;

/**
 * Handles video playback with media controls.
 */
public class PlaybackVideoFragment extends VideoSupportFragment {

    private PlaybackTransportControlGlue<MediaPlayerAdapter> mTransportControlGlue;
    private File mFile;
    private boolean mShouldResume;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mFile = getActivity().getIntent().getParcelableExtra(PlaybackActivity.EXTRA_FILE);
        mShouldResume = getActivity().getIntent().getBooleanExtra(PlaybackActivity.EXTRA_SHOULD_RESUME, false);

        VideoSupportFragmentGlueHost glueHost = new VideoSupportFragmentGlueHost(PlaybackVideoFragment.this);

        MediaPlayerAdapter playerAdapter = new MediaPlayerAdapter(getContext());
        playerAdapter.setRepeatAction(PlaybackControlsRow.RepeatAction.INDEX_NONE);

        mTransportControlGlue = new PlaybackTransportControlGlue<>(getContext(), playerAdapter);
        mTransportControlGlue.setHost(glueHost);
        mTransportControlGlue.setTitle(mFile.getName());
        mTransportControlGlue.setSubtitle(mFile.getName());
        mTransportControlGlue.playWhenPrepared();
        mTransportControlGlue.setSeekEnabled(true);
        mTransportControlGlue.addPlayerCallback(new PlayerCallback());

        if(mFile.getStreamUriMp4() != null){
            playerAdapter.setDataSource(mFile.getStreamUriMp4());
        } else {
            playerAdapter.setDataSource(mFile.getStreamUri());
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mTransportControlGlue != null) {
            mTransportControlGlue.pause();
        }
    }

    private class PlayerCallback extends PlaybackGlue.PlayerCallback{
        @Override
        public void onPreparedStateChanged(PlaybackGlue glue) {
            super.onPreparedStateChanged(glue);

            if(mShouldResume){
                mTransportControlGlue.seekTo(mFile.getResumeTime()*1000);
                // we only want to do this after the first load //
                mShouldResume = false;
            }
        }

        @Override
        public void onPlayStateChanged(PlaybackGlue glue) {
            super.onPlayStateChanged(glue);

            if(getSurfaceView() != null) {
                if (mTransportControlGlue.isPlaying()) {
                    getSurfaceView().setKeepScreenOn(true);
                } else {
                    getSurfaceView().setKeepScreenOn(false);

                    Putio.setResumeTime(getContext(), mFile.getId(), mTransportControlGlue.getCurrentPosition()/1000, null);
                }
            }
        }
    }
}
