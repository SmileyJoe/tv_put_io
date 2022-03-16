package io.smileyjoe.putio.tv.db;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import io.smileyjoe.putio.tv.object.Video;

@Dao
public interface VideoDao {
    @Query("SELECT * FROM video WHERE id_put_io IS :id")
    Video getByPutId(long id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Video video);

    @Query("DELETE FROM video WHERE id_put_io IS :id")
    void delete(long id);
}
