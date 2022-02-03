package io.smileyjoe.putio.tv.interfaces;

import android.content.Context;
import android.text.TextUtils;

import io.smileyjoe.putio.tv.network.Tmdb;
import io.smileyjoe.putio.tv.object.Video;
import io.smileyjoe.putio.tv.object.VideoType;
import io.smileyjoe.putio.tv.util.TmdbUtil;

public interface VideoDetails extends TmdbUtil.Listener {

    Context getBaseContext();

    Video getVideo();

    default void getData() {
        Video video = getVideo();
        if (video.getVideoType() == VideoType.MOVIE && video.isTmdbFound() && TextUtils.isEmpty(video.getTagLine())) {
            TmdbUtil.OnTmdbResponse response = new TmdbUtil.OnTmdbResponse(getBaseContext(), video);
            response.setListener(this);
            Tmdb.Movie.get(getBaseContext(), video.getTmdbId(), response);
        }
    }

}
