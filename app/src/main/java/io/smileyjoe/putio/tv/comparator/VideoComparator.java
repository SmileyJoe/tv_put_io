package io.smileyjoe.putio.tv.comparator;

import java.util.Comparator;

import io.smileyjoe.putio.tv.object.Video;
import io.smileyjoe.putio.tv.object.VideoType;

public class VideoComparator implements Comparator<Video> {
    @Override
    public int compare(Video videoOne, Video videoTwo) {
        int result = videoOne.getTitle().compareTo(videoTwo.getTitle());

        if(videoOne.getVideoType() == VideoType.EPISODE){
            result = Integer.compare(videoOne.getSeason(), videoTwo.getSeason());

            if(result != 0){
                return result;
            }

            return Integer.compare(videoOne.getEpisode(), videoTwo.getEpisode());
        } else {
            return result;
        }
    }
}
