package com.github.davsx.llearn.persistence.dao;

import android.arch.persistence.room.*;
import com.github.davsx.llearn.persistence.entity.CardEntity;

import java.util.List;

@Dao
public interface CardDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long save(CardEntity card);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void saveMany(List<CardEntity> cards);

    @Delete()
    void delete(CardEntity card);

    @Query("SELECT * FROM cards WHERE back != '' AND front != '' ORDER BY id_card ASC")
    List<CardEntity> getAllValidCards();

    @Query("SELECT * FROM cards ORDER BY id_card ASC")
    List<CardEntity> getAllCards();

    @Query("SELECT * FROM cards WHERE back != '' AND front != '' ORDER BY RANDOM() LIMIT :limit")
    List<CardEntity> getRandomCards(int limit);

    @Query("SELECT * FROM cards WHERE back != '' AND front != '' AND learn_score < :learnScore" +
            " ORDER BY learn_score DESC, learn_update_at DESC LIMIT :limit")
    List<CardEntity> getLearnCandidates(Integer learnScore, Integer limit);

    @Query("SELECT * FROM cards WHERE id_card = :id_card")
    CardEntity getCardWithId(Long id_card);

    @Query("SELECT * FROM cards WHERE front = :front")
    CardEntity getCardWithFront(String front);

    @Query("SELECT count(*) FROM cards")
    Integer cardCount();

    @Query("SELECT * FROM cards WHERE front IN (:frontTexts)")
    List<CardEntity> getCardsWithFronts(List<String> frontTexts);
}