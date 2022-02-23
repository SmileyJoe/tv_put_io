package io.smileyjoe.putio.tv.object;

public enum FolderType {

    GROUP(1), DIRECTORY(3), VIRTUAL(2);

    private int mOrder;

    FolderType(int order) {
        mOrder = order;
    }

    public int getOrder() {
        return mOrder;
    }
}
