package io.smileyjoe.putio.tv.comparator;

import java.util.Comparator;

import io.smileyjoe.putio.tv.interfaces.Folder;

public class FolderComparator implements Comparator<Folder> {

    @Override
    public int compare(Folder folderOne, Folder folderTwo) {
        int result = Integer.compare(folderOne.getFolderType().getOrder(), folderTwo.getFolderType().getOrder());

        if(result != 0){
            return result;
        }

        return folderOne.getTitle().compareTo(folderTwo.getTitle());
    }
}
