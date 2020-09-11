package io.smileyjoe.putio.tv.ui.fragment;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.leanback.app.VideoSupportFragment;
import androidx.leanback.app.VideoSupportFragmentGlueHost;
import androidx.leanback.media.MediaPlayerAdapter;
import androidx.leanback.media.PlaybackTransportControlGlue;
import androidx.leanback.widget.PlaybackControlsRow;

import io.smileyjoe.putio.tv.putio.File;
import io.smileyjoe.putio.tv.ui.activity.PlaybackActivity;

/**
 * Handles video playback with media controls.
 */
public class PlaybackVideoFragment extends VideoSupportFragment {

    private PlaybackTransportControlGlue<MediaPlayerAdapter> mTransportControlGlue;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final File file =
                (File) getActivity().getIntent().getParcelableExtra(PlaybackActivity.EXTRA_FILE);

        VideoSupportFragmentGlueHost glueHost =
                new VideoSupportFragmentGlueHost(PlaybackVideoFragment.this);

        MediaPlayerAdapter playerAdapter = new MediaPlayerAdapter(getContext());
        playerAdapter.setRepeatAction(PlaybackControlsRow.RepeatAction.INDEX_NONE);

        mTransportControlGlue = new PlaybackTransportControlGlue<>(getContext(), playerAdapter);
        mTransportControlGlue.setHost(glueHost);
        mTransportControlGlue.setTitle(file.getName());
        mTransportControlGlue.setSubtitle(file.getName());
        mTransportControlGlue.playWhenPrepared();
        mTransportControlGlue.setSeekEnabled(true);
        playerAdapter.setDataSource(file.getStreamUri());
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        getSurfaceView().setKeepScreenOn(true);
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onPause() {
        super.onPause();
        getSurfaceView().setKeepScreenOn(false);
        if (mTransportControlGlue != null) {
            mTransportControlGlue.pause();
        }
    }
}
