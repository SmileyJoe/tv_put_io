package io.smileyjoe.putio.tv.comparator;

import java.util.Comparator;
import java.util.stream.Stream;

import io.smileyjoe.putio.tv.object.Filter;
import io.smileyjoe.putio.tv.object.Video;

public class VideoComparator implements Comparator<Video> {

    public enum Order {
        ALPHABETICAL(null),
        NEWEST_FIRST(Filter.SORT_CREATED),
        RELEASE_ASCENDING(Filter.SORT_RELEASED_ASCENDING),
        RELEASE_DESCENDING(Filter.SORT_RELEASED_DESCENDING);

        private Filter mFilter;

        Order(Filter filter) {
            mFilter = filter;
        }

        public Filter getFilter() {
            return mFilter;
        }

        public static Order fromFilter(Filter filter) {
            return Stream.of(values())
                    .filter(order -> order.getFilter() != null && order.getFilter() == filter)
                    .findFirst()
                    .orElse(ALPHABETICAL);
        }
    }

    private Order mOrder;

    public VideoComparator(Order order) {
        mOrder = order;
    }

    @Override
    public int compare(Video videoOne, Video videoTwo) {
        int result;

        switch (mOrder) {
            case NEWEST_FIRST:
                result = Long.compare(videoTwo.getCreatedAt(), videoOne.getCreatedAt());
                break;
            case RELEASE_ASCENDING:
                result = Long.compare(videoTwo.getReleaseDate(), videoOne.getReleaseDate());
                break;
            case RELEASE_DESCENDING:
                result = Long.compare(videoOne.getReleaseDate(), videoTwo.getReleaseDate());
                break;
            case ALPHABETICAL:
            default:
                result = videoOne.getPutTitle().compareToIgnoreCase(videoTwo.getPutTitle());
                break;
        }

        return result;
    }
}
