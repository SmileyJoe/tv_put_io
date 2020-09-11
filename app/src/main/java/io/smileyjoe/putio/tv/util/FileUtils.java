package io.smileyjoe.putio.tv.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import io.smileyjoe.putio.tv.putio.File;

public class FileUtils {

    private FileUtils() {
    }

    public static ArrayList<File> filter(ArrayList<File> files){
        ArrayList<File> filesFiltered = new ArrayList<>();

        for(File file:files){
            if(file.getFileType() != File.Type.UNKNOWN){
                filesFiltered.add(file);
            }
        }

        return filesFiltered;
    }

    public static void sort(ArrayList<File> files){
        Collections.sort(files, new FileComparator());
    }

    private static class FileComparator implements Comparator<File>{
        @Override
        public int compare(File fileOne, File fileTwo) {
            int result = Integer.compare(fileOne.getFileType().getOrder(), fileTwo.getFileType().getOrder());

            if(result != 0){
                return result;
            }

            return fileOne.getName().compareTo(fileTwo.getName());
        }
    }

}
