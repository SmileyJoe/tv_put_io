package io.smileyjoe.putio.tv.object;

import java.util.stream.Stream;

public enum FileType {

    FOLDER("FOLDER"),
    VIDEO("VIDEO"),
    UNKNOWN("");

    private String mPutValue;

    FileType(String putValue) {
        mPutValue = putValue;
    }

    public String getPutValue() {
        return mPutValue;
    }

    public static FileType fromPut(String apiValue) {
        return Stream.of(values())
                .filter(type -> type.getPutValue().equals(apiValue))
                .findFirst()
                .orElse(UNKNOWN);
    }
}
