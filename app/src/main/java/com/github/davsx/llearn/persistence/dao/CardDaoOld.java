package com.github.davsx.llearn.persistence.dao;

import android.arch.persistence.room.*;
import com.github.davsx.llearn.persistence.entity.CardEntityOld;

import java.util.List;

@Dao
public interface CardDaoOld {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long save(CardEntityOld card);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void saveMany(List<CardEntityOld> cards);

    @Delete()
    void delete(CardEntityOld card);

    @Query("DELETE FROM cards")
    void deleteAllCards();

    @Query("SELECT count(*) FROM cards")
    Integer allCardsCount();

    @Query("SELECT count(*) FROM cards WHERE type = 1 AND enabled = 1")
    Integer learnableCardCount();

    @Query("SELECT count(*) FROM cards WHERE type = 2 AND enabled = 1")
    Integer reviewableCardCount();

    @Query("SELECT count(*) FROM cards WHERE type = 2 AND enabled = 1 AND next_review_at < :timestamp")
    Integer reviewableOverdueCardCount(long timestamp);

    @Query("SELECT * FROM cards WHERE id_card > :id AND type IN (:types) AND (front LIKE :query OR back LIKE :query) " +
            "ORDER BY id_card LIMIT :limit")
    List<CardEntityOld> searchCardsChunked(String query, Long id, List<Integer> types, int limit);

    @Query("SELECT * FROM cards WHERE id_card > :id AND type IN (:types) ORDER BY id_card ASC LIMIT :limit")
    List<CardEntityOld> getCardsChunked(long id, List<Integer> types, int limit);

    @Query("SELECT * FROM cards WHERE type != 0 AND enabled = 1 ORDER BY RANDOM() LIMIT :limit")
    List<CardEntityOld> getRandomCards(int limit);

    @Query("SELECT * FROM cards WHERE type = 1 AND enabled = 1 ORDER BY learn_score DESC, learn_update_at DESC LIMIT :limit")
    List<CardEntityOld> getLearnCandidates(Integer limit);

    @Query("SELECT * FROM cards WHERE type = 2 AND next_review_at < :timestamp AND enabled = 1 ORDER BY next_review_at ASC LIMIT :limit")
    List<CardEntityOld> getReviewCandidates(long timestamp, int limit);

    @Query("SELECT * FROM cards WHERE type = 2 AND next_review_at > :timestamp AND enabled = 1 ORDER BY RANDOM() LIMIT :limit")
    List<CardEntityOld> getReviewFillCandidates(long timestamp, int limit);

    @Query("SELECT * FROM cards WHERE id_card = :id_card")
    CardEntityOld getCardWithId(Long id_card);

    @Query("SELECT * FROM cards WHERE front = :front OR back = :back LIMIT 1")
    CardEntityOld findDuplicateCard(String front, String back);

}