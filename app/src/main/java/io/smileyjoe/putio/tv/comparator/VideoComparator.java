package io.smileyjoe.putio.tv.comparator;

import android.util.Log;

import java.util.Comparator;

import io.smileyjoe.putio.tv.object.FileType;
import io.smileyjoe.putio.tv.object.Filter;
import io.smileyjoe.putio.tv.object.Video;
import io.smileyjoe.putio.tv.object.VideoType;

public class VideoComparator implements Comparator<Video> {

    public enum Order{
        ALPHABETICAL(null), NEWEST_FIRST(Filter.SORT_CREATED);

        private Filter mFilter;

        Order(Filter filter) {
            mFilter = filter;
        }

        public Filter getFilter() {
            return mFilter;
        }

        public static Order fromFilter(Filter filter){
            for(Order order:values()){
                if(order.getFilter() != null && order.getFilter() == filter){
                    return order;
                }
            }

            return ALPHABETICAL;
        }
    }

    private Order mOrder;

    public VideoComparator(Order order) {
        mOrder = order;
    }

    @Override
    public int compare(Video videoOne, Video videoTwo) {
        int result;

        if(videoOne.getFileType() == FileType.GROUP){

            if(videoTwo.getFileType() == FileType.GROUP){
                return videoOne.getTitle().compareTo(videoTwo.getTitle());
            } else {
                return -1;
            }
        } else if(videoOne.getVideoType() == VideoType.EPISODE){
            result = Integer.compare(videoOne.getSeason(), videoTwo.getSeason());

            if(result != 0){
                return result;
            }

            return Integer.compare(videoOne.getEpisode(), videoTwo.getEpisode());
        } else {
            switch (mOrder){
                case NEWEST_FIRST:
                    result = Long.compare(videoTwo.getCreatedAt(), videoOne.getCreatedAt());
                    break;
                case ALPHABETICAL:
                default:
                    result = videoOne.getTitle().compareTo(videoTwo.getTitle());
                    break;
            }

            return result;
        }
    }
}
