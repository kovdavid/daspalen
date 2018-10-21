package com.github.davsx.llearn.persistence.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import com.github.davsx.llearn.persistence.entity.JournalEntity;

import java.util.List;

@Dao
public interface JournalDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long save(JournalEntity journal);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void saveMany(List<JournalEntity> journals);

    @Query("DELETE FROM journal")
    void deleteAllJournals();

    @Query("SELECT count(*) FROM journal")
    Integer allJournalsCount();

    @Query("SELECT * FROM journal WHERE id_journal > :journalId LIMIT :limit")
    List<JournalEntity> getJournalsChunked(long journalId, int limit);

    @Query("SELECT * FROM journal WHERE id_card = :cardId ORDER BY timestamp ASC")
    List<JournalEntity> getJournalsForCard(long cardId);

}