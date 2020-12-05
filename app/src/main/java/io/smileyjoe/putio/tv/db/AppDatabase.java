package io.smileyjoe.putio.tv.db;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.smileyjoe.putio.tv.object.Genre;
import io.smileyjoe.putio.tv.object.Group;
import io.smileyjoe.putio.tv.object.Video;

@Database(entities = {Video.class, Genre.class, Group.class}, version = 5)
public abstract class AppDatabase extends RoomDatabase {
    public abstract VideoDao videoDao();
    public abstract GenreDao genreDao();
    public abstract GroupDao groupDao();

    private static volatile AppDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE `group` (`id` INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, "
                    + "`title` TEXT,"
                    + "`put_ids_json` TEXT)");
            database.execSQL("INSERT INTO `group` (id, title, put_ids_json) VALUES (1, 'Movies', '[]')");
            database.execSQL("INSERT INTO `group` (id, title, put_ids_json) VALUES (2, 'Series', '[]')");
            database.execSQL("INSERT INTO genre(id, title) VALUES (1, 'test 1')");
        }
    };

    static final Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("DELETE FROM video");
            database.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE name='video'");
            database.execSQL("ALTER TABLE video ADD COLUMN release_date INTEGER NOT NULL DEFAULT 0");
        }
    };

    static final Migration MIGRATION_3_4 = new Migration(3, 4) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            resetVideo(database);
            database.execSQL("ALTER TABLE video ADD COLUMN tagline TEXT");
            database.execSQL("ALTER TABLE video ADD COLUMN runtime INTEGER NOT NULL DEFAULT 0");
        }
    };

    static final Migration MIGRATION_4_5 = new Migration(4, 5) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE `group` ADD COLUMN group_type_id INTEGER NOT NULL DEFAULT 0");
            database.execSQL("UPDATE `group` SET group_type_id = 1 WHERE id = 1");
            database.execSQL("UPDATE `group` SET group_type_id = 1 WHERE id = 2");
            database.execSQL("INSERT INTO `group` (id, title, put_ids_json, group_type_id) VALUES (3, 'Watch Later', '[]', 2)");
        }
    };

    private static void resetVideo(SupportSQLiteDatabase database){
        database.execSQL("DELETE FROM video");
        database.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE name='video'");
    }

    public static AppDatabase getInstance(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "main_database")
                            .addMigrations(MIGRATION_1_2)
                            .addMigrations(MIGRATION_2_3)
                            .addMigrations(MIGRATION_3_4)
                            .addMigrations(MIGRATION_4_5)
                            .addCallback(new RoomCallback())
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    private static class RoomCallback extends RoomDatabase.Callback{
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase database) {
            super.onCreate(database);

            database.execSQL("INSERT INTO `group` (id, title, put_ids_json, group_type_id) VALUES (1, 'Movies', '[]', 1)");
            database.execSQL("INSERT INTO `group` (id, title, put_ids_json, group_type_id) VALUES (2, 'Series', '[]', 1)");
            database.execSQL("INSERT INTO `group` (id, title, put_ids_json, group_type_id) VALUES (3, 'Watch Later', '[]', 2)");
        }
    }
}
