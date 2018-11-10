package com.github.davsx.llearn.persistence.dao;

import android.arch.persistence.room.*;
import com.github.davsx.llearn.persistence.entity.CardEntity;
import com.github.davsx.llearn.persistence.entity.CardNotificationEntity;
import com.github.davsx.llearn.persistence.entity.CardQuizEntity;

import java.util.List;

@Dao
public abstract class LLearnDao {

    @Query("SELECT * FROM card WHERE id_card = :cardId")
    public abstract CardEntity getCardEntity(long cardId);

    @Query("SELECT * FROM card_quiz WHERE id_card = :cardId")
    public abstract CardQuizEntity getCardQuizEntity(long cardId);

    @Query("SELECT * FROM card_notification WHERE id_card = :cardId")
    public abstract CardNotificationEntity getCardNotificationEntity(long cardId);

    @Insert
    public abstract long[] insertManyCardEntities(List<CardEntity> cardEntities);

    @Insert
    public abstract void insertManyCardQuizEntities(List<CardQuizEntity> cardQuizEntities);

    @Insert
    public abstract void insertManyCardNotificationEntities(List<CardNotificationEntity> cardNotificationEntities);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract void updateCardEntity(CardEntity cardEntity);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract void updateCardQuizEntity(CardQuizEntity cardQuizEntity);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract void updateCardNotificationEntity(CardNotificationEntity cardNotificationEntity);

    @Delete
    abstract void deleteCardEntity(CardEntity cardEntity);

    @Delete
    abstract void deleteCardQuizEntity(CardQuizEntity cardQuizEntity);

    @Delete
    abstract void deleteCardNotificationEntity(CardNotificationEntity cardNotificationEntity);

    @Query("SELECT * FROM card c JOIN card_quiz cq USING(id_card)"
            + "WHERE c.id_card > :cardId AND cq.quiz_type IN (:types) ORDER BY c.id_card ASC LIMIT :limit")
    public abstract List<CardEntity> getNextCardEntities(long cardId, List<Integer> types, int limit);

    @Query("SELECT * FROM card c JOIN card_quiz cq USING(id_card)"
            + " WHERE c.id_card > :cardId AND (c.front_text LIKE :query OR c.back_text LIKE :query)"
            + " AND cq.quiz_type IN (:types) ORDER BY c.id_card ASC LIMIT :limit")
    public abstract List<CardEntity> searchNextCardEntities(String query, Long cardId, List<Integer> types, int limit);

    @Query("SELECT * FROM card_quiz WHERE id_card IN (:cardIds)")
    public abstract List<CardQuizEntity> getCardQuizEntitiesById(List<Long> cardIds);

    @Query("SELECT * FROM card_notification WHERE id_card IN (:cardIds)")
    public abstract List<CardNotificationEntity> getCardNotificationEntitiesById(List<Long> cardIds);

    @Query("SELECT count(*) FROM card c JOIN card_quiz cq USING(id_card)"
            + " WHERE cq.quiz_type = :quizType AND c.enabled = 1")
    public abstract int getCardCountByQuizType(int quizType);

    @Query("SELECT count(*) FROM card c JOIN card_quiz cq USING(id_card)"
            + " WHERE cq.quiz_type = :quizType AND c.enabled = 1 AND c.next_review_at < :now")
    public abstract int getOverdueReviewCardCount(Integer quizType, long now);

    @Query("DELETE FROM card")
    abstract void deleteAllCards();

    @Query("DELETE FROM card_quiz")
    abstract void deleteAllCardQuizes();

    @Query("DELETE FROM card_notification")
    abstract void deleteAllCardNotifications();

    @Transaction
    public void wipeData() {
        deleteAllCards();
        deleteAllCardQuizes();
        deleteAllCardNotifications();
    }

    @Transaction
    public void updateCard(CardEntity cardEntity,
                           CardQuizEntity cardQuizEntity,
                           CardNotificationEntity cardNotificationEntity) {
        if (cardEntity != null) updateCardEntity(cardEntity);
        if (cardQuizEntity != null) updateCardQuizEntity(cardQuizEntity);
        if (cardNotificationEntity != null) updateCardNotificationEntity(cardNotificationEntity);
    }

    @Transaction
    public void deleteCard(CardEntity cardEntity,
                           CardQuizEntity cardQuizEntity,
                           CardNotificationEntity cardNotificationEntity) {
        if (cardEntity != null) deleteCardEntity(cardEntity);
        if (cardQuizEntity != null) deleteCardQuizEntity(cardQuizEntity);
        if (cardNotificationEntity != null) deleteCardNotificationEntity(cardNotificationEntity);
    }

    @Query("SELECT count(*) FROM card")
    public abstract int getAllCardCount();

}