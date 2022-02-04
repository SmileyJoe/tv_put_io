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

import io.smileyjoe.putio.tv.object.Character;
import io.smileyjoe.putio.tv.object.Genre;
import io.smileyjoe.putio.tv.object.Group;
import io.smileyjoe.putio.tv.object.Video;

@Database(entities = {Video.class, Genre.class, Group.class, Character.class}, version = 8)
public abstract class AppDatabase extends RoomDatabase {
    public abstract VideoDao videoDao();

    public abstract GenreDao genreDao();

    public abstract GroupDao groupDao();

    public abstract CharacterDao characterDao();

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

    static final Migration MIGRATION_5_6 = new Migration(5, 6) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            resetVideo(database);
            database.execSQL("CREATE TABLE `character` (`id` INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, "
                    + "`video_tmdb_id` INTEGER NOT NULL,"
                    + "`cast_member_tmdb_id` INTEGER NOT NULL,"
                    + "`name` TEXT,"
                    + "`profile_image` TEXT,"
                    + "`order` INTEGER NOT NULL,"
                    + "`cast_member_name` TEXT)");
        }
    };

    static final Migration MIGRATION_6_7 = new Migration(6, 7) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE `group` ADD COLUMN use_parent INTEGER NOT NULL DEFAULT 0");
            database.execSQL("UPDATE `group` SET use_parent = 0 WHERE id = 1");
            database.execSQL("UPDATE `group` SET use_parent = 0 WHERE id = 2");
            database.execSQL("UPDATE `group` SET use_parent = 1 WHERE id = 3");
            database.execSQL("UPDATE `group` SET group_type_id = 3 WHERE id = 3");
            database.execSQL("INSERT INTO `group` (id, title, put_ids_json, group_type_id, use_parent) VALUES (4, 'Favourites', '[]', 3, 1)");
        }
    };

    private static void resetVideo(SupportSQLiteDatabase database) {
        database.execSQL("DELETE FROM video");
        database.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE name='video'");
    }

    static final Migration MIGRATION_7_8 = new Migration(7, 8) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("DELETE FROM video");
            database.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE name='video'");
            database.execSQL("ALTER TABLE video ADD COLUMN youtube_trailer_key TEXT");
        }
    };

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
                            .addMigrations(MIGRATION_5_6)
                            .addMigrations(MIGRATION_6_7)
                            .addMigrations(MIGRATION_7_8)
                            .addCallback(new RoomCallback())
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    private static class RoomCallback extends RoomDatabase.Callback {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase database) {
            super.onCreate(database);

            database.execSQL("INSERT INTO `group` (id, title, put_ids_json, group_type_id, use_parent) VALUES (1, 'Movies', '[]', 1, 0)");
            database.execSQL("INSERT INTO `group` (id, title, put_ids_json, group_type_id, use_parent) VALUES (2, 'Series', '[]', 1, 0)");
            database.execSQL("INSERT INTO `group` (id, title, put_ids_json, group_type_id, use_parent) VALUES (3, 'Watch Later', '[]', 3, 1)");
            database.execSQL("INSERT INTO `group` (id, title, put_ids_json, group_type_id, use_parent) VALUES (4, 'Favourites', '[]', 3, 1)");
        }
    }
}
