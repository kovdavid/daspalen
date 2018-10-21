package com.github.davsx.llearn.persistence;

import android.arch.persistence.room.RoomDatabase;
import com.github.davsx.llearn.persistence.dao.CardDao;
import com.github.davsx.llearn.persistence.dao.JournalDao;
import com.github.davsx.llearn.persistence.entity.CardEntity;
import com.github.davsx.llearn.persistence.entity.JournalEntity;

@android.arch.persistence.room.Database(
        entities = {
                CardEntity.class,
                JournalEntity.class
        },
        version = 1,
        exportSchema = false
)
public abstract class LLearnDatabase extends RoomDatabase {
    public abstract CardDao cardDao();

    public abstract JournalDao journalDao();
}