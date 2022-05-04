package io.smileyjoe.putio.tv.interfaces;

import android.content.Context;
import android.text.TextUtils;

import io.smileyjoe.putio.tv.broadcast.UpdateVideoReceiver;
import io.smileyjoe.putio.tv.network.Tmdb;
import io.smileyjoe.putio.tv.object.Video;
import io.smileyjoe.putio.tv.object.VideoType;
import io.smileyjoe.putio.tv.util.TmdbUtil;

public interface VideoDetails extends UpdateVideoReceiver {

    Context getContext();
    Video getVideo();

    default void getData() {
        Video video = getVideo();
        if (video.getVideoType() == VideoType.MOVIE && video.isTmdbFound() && TextUtils.isEmpty(video.getTagLine())) {
            TmdbUtil.OnTmdbResponse response = new TmdbUtil.OnTmdbResponse(getContext(), video);
            Tmdb.Movie.get(getContext(), video.getTmdbId(), response);
        }
    }

}
