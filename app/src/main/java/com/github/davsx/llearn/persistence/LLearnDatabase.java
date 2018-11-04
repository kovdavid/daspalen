package com.github.davsx.llearn.persistence;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.migration.Migration;
import android.support.annotation.NonNull;
import com.github.davsx.llearn.persistence.dao.CardDao;
import com.github.davsx.llearn.persistence.dao.JournalDao;
import com.github.davsx.llearn.persistence.entity.CardEntity;
import com.github.davsx.llearn.persistence.entity.JournalEntity;

@android.arch.persistence.room.Database(
        entities = {
                CardEntity.class,
                JournalEntity.class
        },
        version = 2,
        exportSchema = false
)
public abstract class LLearnDatabase extends RoomDatabase {
    public abstract CardDao cardDao();

    public abstract JournalDao journalDao();

    public static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE cards ADD COLUMN image_url TEXT NOT NULL DEFAULT ''");
        }
    };
}