package com.github.davsx.daspalen.persistence;

import android.arch.persistence.room.RoomDatabase;
import com.github.davsx.daspalen.persistence.dao.DaspalenDao;
import com.github.davsx.daspalen.persistence.entity.CardEntity;
import com.github.davsx.daspalen.persistence.entity.CardNotificationEntity;
import com.github.davsx.daspalen.persistence.entity.CardQuizEntity;

@android.arch.persistence.room.Database(
        entities = {
                CardEntity.class,
                CardQuizEntity.class,
                CardNotificationEntity.class,
        },
        version = 1,
        exportSchema = false
)
public abstract class DaspalenDatabase extends RoomDatabase {
    public abstract DaspalenDao daspalenDao();
}