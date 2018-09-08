package com.github.davsx.llearn.persistence;

import android.arch.persistence.room.RoomDatabase;
import com.github.davsx.llearn.persistence.dao.CardDao;
import com.github.davsx.llearn.persistence.entity.CardEntity;

@android.arch.persistence.room.Database(entities = {CardEntity.class}, version = 1, exportSchema = false)
public abstract class LLearnDatabase extends RoomDatabase {
    public abstract CardDao cardDao();
}