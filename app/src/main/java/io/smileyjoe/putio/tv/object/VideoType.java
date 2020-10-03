package io.smileyjoe.putio.tv.object;

public enum VideoType {

    FOLDER(1),
    VIDEO(2),
    MOVIE(2),
    EPISODE(2),
    UNKNOWN(0);

    private int mOrder;

    VideoType(int order) {
        mOrder = order;
    }

    public int getOrder() {
        return mOrder;
    }

}
