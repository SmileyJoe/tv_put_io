package io.smileyjoe.putio.tv.db;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import io.smileyjoe.putio.tv.object.Group;

@Dao
public interface GroupDao {

    @Query("SELECT * FROM `group`")
    List<Group> getAll();

    @Query("SELECT * FROM `group` WHERE id IS :id")
    Group get(Long id);

    @Query("SELECT * FROM `group` WHERE enabled IS 1")
    List<Group> getEnabled();

    @Query("SELECT * FROM `group` WHERE group_type_id IN (3, :typeId)")
    List<Group> getByType(int typeId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Group group);

    @Query("UPDATE `group` SET `enabled` = :enabled WHERE id IS :id")
    void enabled(Long id, boolean enabled);

    @Query("UPDATE `group` SET `put_ids_json` = :putIdsJson WHERE id IS :id")
    void updatePutIds(int id, String putIdsJson);
}
