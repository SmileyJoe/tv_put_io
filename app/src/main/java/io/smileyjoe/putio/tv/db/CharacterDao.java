package io.smileyjoe.putio.tv.db;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import io.smileyjoe.putio.tv.object.Character;

@Dao
public interface CharacterDao {
    @Query("SELECT * FROM character")
    List<Character> getAll();

    @Query("SELECT * FROM character WHERE video_tmdb_id IS :id")
    List<Character> getByTmdbId(long id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(List<Character> characters);
}
