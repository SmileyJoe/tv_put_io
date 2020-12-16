package io.smileyjoe.putio.tv.util;

import android.content.Context;
import android.net.Uri;

import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.FileWriter;

public class FileUtil {

    private static final String DIRECTORY_SUBTITLES = "subtitles";

    private FileUtil(){

    }

    public static Uri saveSubtitle(Context context, long putId, String subtitles){
        return save(context, DIRECTORY_SUBTITLES, Long.toString(putId) + ".srt", subtitles);
    }

    private static Uri save(Context context, String directory, String fileName, String content){
        File dir = new File(context.getFilesDir(), directory);
        if(!dir.exists()){
            dir.mkdir();
        }

        try {
            File file = new File(dir, fileName);
            FileWriter writer = new FileWriter(file);
            writer.append(content);
            writer.flush();
            writer.close();
            return Uri.fromFile(file);
        } catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }

}
