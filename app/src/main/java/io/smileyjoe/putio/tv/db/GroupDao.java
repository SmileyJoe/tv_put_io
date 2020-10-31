package io.smileyjoe.putio.tv.db;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import io.smileyjoe.putio.tv.object.Group;
import io.smileyjoe.putio.tv.object.Video;

@Dao
public interface GroupDao {

    @Query("SELECT * FROM `group`")
    List<Group> getAll();

    @Query("SELECT * FROM `group` WHERE id IS :id")
    Group get(Long id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Group group);
}
