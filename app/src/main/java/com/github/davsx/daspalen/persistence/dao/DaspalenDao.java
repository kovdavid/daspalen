package com.github.davsx.daspalen.persistence.dao;

import android.arch.persistence.room.*;
import com.github.davsx.daspalen.model.Card;
import com.github.davsx.daspalen.persistence.entity.CardEntity;
import com.github.davsx.daspalen.persistence.entity.CardNotificationEntity;
import com.github.davsx.daspalen.persistence.entity.CardQuizEntity;

import java.util.ArrayList;
import java.util.List;

@Dao
public abstract class DaspalenDao {

    @Query("SELECT * FROM card WHERE id_card = :cardId")
    public abstract CardEntity getCardEntity(long cardId);

    @Query("SELECT * FROM card_quiz WHERE id_card = :cardId")
    public abstract CardQuizEntity getCardQuizEntity(long cardId);

    @Query("SELECT * FROM card_notification WHERE id_card = :cardId")
    public abstract CardNotificationEntity getCardNotificationEntity(long cardId);

    @Insert
    abstract long[] insertManyCardEntities(List<CardEntity> cardEntities);

    @Insert
    abstract void insertManyCardQuizEntities(List<CardQuizEntity> cardQuizEntities);

    @Insert
    abstract void insertManyCardNotificationEntities(List<CardNotificationEntity> cardNotificationEntities);

    @Insert
    abstract long insertCardEntity(CardEntity cardEntity);

    @Insert
    abstract void insertCardQuizEntity(CardQuizEntity cardQuizEntity);

    @Insert
    abstract void insertCardNotificationEntity(CardNotificationEntity cardNotificationEntity);

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

    @Query("SELECT c.* FROM card c JOIN card_quiz cq USING(id_card)"
            + "WHERE c.id_card > :cardId AND cq.quiz_type IN (:types) ORDER BY c.id_card ASC LIMIT :limit")
    public abstract List<CardEntity> getNextCardEntities(long cardId, List<Integer> types, int limit);

    @Query("SELECT c.* FROM card c JOIN card_quiz cq USING(id_card)"
            + " WHERE c.id_card > :cardId AND (c.front_text LIKE :query OR c.back_text LIKE :query)"
            + " AND cq.quiz_type IN (:types) ORDER BY c.id_card ASC LIMIT :limit")
    public abstract List<CardEntity> searchNextCardEntities(String query, Long cardId, List<Integer> types, int limit);

    @Query("SELECT c.* FROM card c JOIN card_quiz cq USING(id_card)"
            + " WHERE c.enabled = 1 AND cq.quiz_type IN (:types) ORDER BY RANDOM() LIMIT :count")
    public abstract List<CardEntity> getRandomCardEntities(List<Integer> types, Integer count);

    @Query("SELECT * FROM card_quiz WHERE id_card IN (:cardIds)")
    public abstract List<CardQuizEntity> getCardQuizEntitiesById(List<Long> cardIds);

    @Query("SELECT * FROM card_notification WHERE id_card IN (:cardIds)")
    public abstract List<CardNotificationEntity> getCardNotificationEntitiesById(List<Long> cardIds);

    @Query("SELECT count(*) FROM card c JOIN card_quiz cq USING(id_card)"
            + " WHERE cq.quiz_type = :quizType AND c.enabled = 1")
    public abstract int getCardCountByQuizType(int quizType);

    @Query("SELECT count(*) FROM card c JOIN card_quiz cq USING(id_card)"
            + " WHERE cq.quiz_type = :quizType AND c.enabled = 1 AND cq.next_review_at < :now")
    public abstract int getOverdueReviewCardCount(Integer quizType, long now);

    @Query("SELECT c.* FROM card c JOIN card_quiz cq USING(id_card)"
            + " WHERE cq.quiz_type = :quizType AND c.enabled = 1"
            + " ORDER BY cq.learn_score DESC, cq.last_learn_quiz_at DESC LIMIT :count")
    public abstract List<CardEntity> getLearnCandidateCardEntities(Integer quizType, Integer count);

    @Query("SELECT c.* FROM card c JOIN card_quiz cq USING(id_card)"
            + " WHERE cq.quiz_type = :quizType AND c.enabled = 1 AND cq.next_review_at < :now"
            + " ORDER BY cq.next_review_at ASC LIMIT :count")
    public abstract List<CardEntity> getReviewCandidateCardEntities(Integer quizType, long now, Integer count);

    @Query("SELECT c.* FROM card c JOIN card_quiz cq USING(id_card)"
            + " WHERE cq.quiz_type = :quizType AND c.enabled = 1 AND c.id_card NOT IN (:cardIdBlackList)"
            + " ORDER BY RANDOM() LIMIT :count")
    public abstract List<CardEntity> getReviewFillCardEntities(Integer quizType, List<Long> cardIdBlackList, Integer count);

    @Query("SELECT c.* FROM card c JOIN card_notification cn USING(id_card)"
            + " WHERE c.enabled = 1 AND cn.enabled = 1"
            + " ORDER BY cn.last_notification_at ASC LIMIT :limit")
    public abstract List<CardEntity> getCardNotificationCandidateEntities(int limit);

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

    @Query("SELECT * FROM card WHERE front_text = :frontText OR back_text = :backText")
    public abstract CardEntity findDuplicateCardEntity(String frontText, String backText);

    @Transaction
    public long createNewCard(Card card) {
        CardEntity cardEntity = card.getCardEntity();
        CardQuizEntity cardQuizEntity = card.getCardQuizEntity();
        CardNotificationEntity cardNotificationEntity = card.getCardNotificationEntity();

        long id = insertCardEntity(cardEntity);
        insertCardQuizEntity(cardQuizEntity.setCardId(id));
        insertCardNotificationEntity(cardNotificationEntity.setCardId(id));

        return id;
    }

    @Transaction
    public void createNewCards(List<Card> newCards) {
        List<CardEntity> cardEntities = new ArrayList<>();
        List<CardQuizEntity> cardQuizEntities = new ArrayList<>();
        List<CardNotificationEntity> cardNotificationEntities = new ArrayList<>();
        for (Card card : newCards) {
            cardEntities.add(card.getCardEntity());
            cardQuizEntities.add(card.getCardQuizEntity());
            cardNotificationEntities.add(card.getCardNotificationEntity());
        }

        long[] ids = insertManyCardEntities(cardEntities);
        for (int i = 0; i < ids.length; i++) {
            long id = ids[i];
            cardEntities.get(i).setCardId(id);
            cardQuizEntities.get(i).setCardId(id);
            cardNotificationEntities.get(i).setCardId(id);
        }

        insertManyCardQuizEntities(cardQuizEntities);
        insertManyCardNotificationEntities(cardNotificationEntities);
    }

    @Query("SELECT count(*) FROM card")
    public abstract int getAllCardCount();

}