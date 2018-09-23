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

    @Query("DELETE FROM cards")
    void deleteAllCards();

    @Query("SELECT count(*) FROM cards WHERE type = 1")
    Integer learnableCardCount();

    @Query("SELECT * FROM cards WHERE id_card > :id AND type IN (:types) AND (front LIKE :query OR back LIKE :query)" +
            " ORDER BY id_card LIMIT :limit")
    List<CardEntity> searchCardsChunked(String query, Long id, List<Integer> types, int limit);

    @Query("SELECT * FROM cards WHERE id_card > :id AND type IN (:types) ORDER BY id_card ASC LIMIT :limit")
    List<CardEntity> getCardsChunked(long id, List<Integer> types, int limit);

    @Query("SELECT * FROM cards WHERE type != 0 ORDER BY RANDOM() LIMIT :limit")
    List<CardEntity> getRandomCards(int limit);

    @Query("SELECT * FROM cards WHERE type = 1 AND learn_score < :learnScore" +
            " ORDER BY learn_score DESC, learn_update_at DESC LIMIT :limit")
    List<CardEntity> getLearnCandidates(Integer learnScore, Integer limit);

    @Query("SELECT * FROM cards WHERE id_card = :id_card")
    CardEntity getCardWithId(Long id_card);

    @Query("SELECT * FROM cards WHERE front = :front OR back = :back LIMIT 1")
    CardEntity findDuplicateCard(String front, String back);
}