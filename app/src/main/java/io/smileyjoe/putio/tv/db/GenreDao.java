package io.smileyjoe.putio.tv.db;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.ArrayList;
import java.util.List;

import io.smileyjoe.putio.tv.object.Genre;

@Dao
public interface GenreDao {
    @Query("SELECT * FROM genre")
    List<Genre> getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(List<Genre> genres);
}
