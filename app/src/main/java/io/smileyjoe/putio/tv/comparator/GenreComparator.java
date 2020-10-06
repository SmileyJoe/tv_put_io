package io.smileyjoe.putio.tv.comparator;

import java.util.Comparator;

import io.smileyjoe.putio.tv.object.Genre;

public class GenreComparator implements Comparator<Genre> {

    @Override
    public int compare(Genre genreOne, Genre genreTwo) {
        return genreOne.getTitle().compareTo(genreTwo.getTitle());
    }
}
