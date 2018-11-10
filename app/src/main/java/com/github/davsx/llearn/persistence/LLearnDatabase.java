package com.github.davsx.llearn.persistence;

import android.arch.persistence.room.RoomDatabase;
import com.github.davsx.llearn.persistence.dao.LLearnDao;
import com.github.davsx.llearn.persistence.entity.CardEntity;
import com.github.davsx.llearn.persistence.entity.CardNotificationEntity;
import com.github.davsx.llearn.persistence.entity.CardQuizEntity;

@android.arch.persistence.room.Database(
        entities = {
                CardEntity.class,
                CardQuizEntity.class,
                CardNotificationEntity.class,
        },
        version = 1,
        exportSchema = false
)
public abstract class LLearnDatabase extends RoomDatabase {
    public abstract LLearnDao llearnDao();
}