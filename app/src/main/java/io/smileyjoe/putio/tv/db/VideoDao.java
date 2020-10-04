package io.smileyjoe.putio.tv.db;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import io.smileyjoe.putio.tv.object.Video;

@Dao
public interface VideoDao {
    @Query("SELECT * FROM video WHERE id_put_io IS :id")
    Video getByPutId(long id);

    @Insert
    void insert(Video video);
}
