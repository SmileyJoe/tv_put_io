package io.smileyjoe.putio.tv.object;

public enum GroupType {

    UNKNOWN(0), DIRECTORY(1), VIDEO(2), DIRECTORY_VIDEO(3);

    private int mId;

    GroupType(int id) {
        mId = id;
    }

    public int getId() {
        return mId;
    }

    public static GroupType fromId(int id){
        for(GroupType type:values()){
            if(type.getId() == id){
                return type;
            }
        }

        return UNKNOWN;
    }

}
