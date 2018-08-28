package com.github.davsx.llearn.infrastructure.persistence;

import android.arch.persistence.room.RoomDatabase;
import com.github.davsx.llearn.infrastructure.persistence.dao.CardDao;
import com.github.davsx.llearn.infrastructure.persistence.entity.CardEntity;
import dagger.Component;

@android.arch.persistence.room.Database(entities = {CardEntity.class}, version = 1, exportSchema = false)
public abstract class Database extends RoomDatabase {
    public abstract CardDao cardDao();
}