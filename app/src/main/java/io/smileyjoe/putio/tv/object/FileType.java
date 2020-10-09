package io.smileyjoe.putio.tv.object;

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

    public static FileType fromPut(String apiValue){
        for(FileType type:values()){
            if(type.getPutValue().equals(apiValue)){
                return type;
            }
        }
        return UNKNOWN;
    }
}
