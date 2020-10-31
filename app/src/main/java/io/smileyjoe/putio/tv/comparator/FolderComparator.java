package io.smileyjoe.putio.tv.comparator;

import java.util.Comparator;

import io.smileyjoe.putio.tv.interfaces.Folder;
import io.smileyjoe.putio.tv.object.FileType;
import io.smileyjoe.putio.tv.object.FolderType;

public class FolderComparator implements Comparator<Folder> {

    @Override
    public int compare(Folder folderOne, Folder folderTwo) {
        int result = Integer.compare(folderOne.getType().getOrder(), folderTwo.getType().getOrder());

        if(result != 0){
            return result;
        }

        return folderOne.getTitle().compareTo(folderTwo.getTitle());
    }
}
