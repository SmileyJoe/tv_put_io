package io.smileyjoe.putio.tv.db.converter;

import androidx.room.TypeConverter;

import io.smileyjoe.putio.tv.object.VideoType;

public class VideoTypeConverter {

    @TypeConverter
    public VideoType toVideoType(String videoType){
        return VideoType.valueOf(videoType);
    }

    @TypeConverter
    public String fromVideoType(VideoType videoType){
        return videoType.name();
    }

}
