package com.github.davsx.llearn.persistence.repository;

import android.util.LongSparseArray;
import com.github.davsx.llearn.LLearnConstants;
import com.github.davsx.llearn.model.Card;
import com.github.davsx.llearn.persistence.dao.LLearnDao;
import com.github.davsx.llearn.persistence.entity.CardEntity;
import com.github.davsx.llearn.persistence.entity.CardNotificationEntity;
import com.github.davsx.llearn.persistence.entity.CardQuizEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LLearnRepository {

    private LLearnDao dao;

    public LLearnRepository(LLearnDao dao) {
        this.dao = dao;
    }

    public void wipeData() {
        dao.wipeData();
    }

    public void createNewCards(ArrayList<Card> newCards) {
        List<CardEntity> cardEntities = new ArrayList<>();
        List<CardQuizEntity> cardQuizEntities = new ArrayList<>();
        List<CardNotificationEntity> cardNotificationEntities = new ArrayList<>();
        for (Card card : newCards) {
            cardEntities.add(card.getCardEntity());
            cardQuizEntities.add(card.getCardQuizEntity());
            cardNotificationEntities.add(card.getCardNotificationEntity());
        }

        long[] ids = dao.insertManyCardEntities(cardEntities);
        for (int i = 0; i < ids.length; i++) {
            long id = ids[i];
            cardEntities.get(i).setCardId(id);
            cardQuizEntities.get(i).setCardId(id);
            cardNotificationEntities.get(i).setCardId(id);
        }

        dao.insertManyCardQuizEntities(cardQuizEntities);
        dao.insertManyCardNotificationEntities(cardNotificationEntities);
    }

    public void updateCard(Card card) {
        CardEntity cardEntity = card.isCardEntityChanged() ? card.getCardEntity() : null;
        CardQuizEntity cardQuizEntity = card.isCardQuizEntityChanged() ? card.getCardQuizEntity() : null;
        CardNotificationEntity cardNotificationEntity = card.isCardNotificationEntityChanged() ? card.getCardNotificationEntity() : null;
        dao.updateCard(cardEntity, cardQuizEntity, cardNotificationEntity);
    }

    public Card getCardWithId(long cardId) {
        CardEntity cardEntity = dao.getCardEntity(cardId);
        CardQuizEntity cardQuizEntity = dao.getCardQuizEntity(cardId);
        CardNotificationEntity cardNotificationEntity = dao.getCardNotificationEntity(cardId);

        return new Card(cardEntity, cardQuizEntity, cardNotificationEntity);
    }

    public void deleteCard(Card card) {
        dao.deleteCard(
                card.getCardEntity(),
                card.getCardQuizEntity(),
                card.getCardNotificationEntity()
        );
    }

    public List<Card> getCardsChunked(Long cardId, List<Integer> quizTypes, int limit) {
        List<CardEntity> cardEntities = dao.getNextCardEntities(cardId, quizTypes, limit);
        return loadCards(cardEntities);
    }

    public List<Card> searchCardsChunked(String searchQuery, Long cardId, List<Integer> quizTypes, int limit) {
        String queryString = String.format("(%s)", searchQuery);
        List<CardEntity> cardEntities = dao.searchNextCardEntities(queryString, cardId, quizTypes, limit);
        return loadCards(cardEntities);
    }

    private List<Card> loadCards(List<CardEntity> cardEntities) {
        List<Long> ids = new ArrayList<>();
        for (CardEntity cardEntity : cardEntities) {
            ids.add(cardEntity.getCardId());
        }

        List<CardQuizEntity> cardQuizEntities = dao.getCardQuizEntitiesById(ids);
        List<CardNotificationEntity> cardNotificationEntities = dao.getCardNotificationEntitiesById(ids);

        LongSparseArray<CardQuizEntity> cardQuizEntityMap = new LongSparseArray<>();
        for (CardQuizEntity cardQuizEntity : cardQuizEntities) {
            cardQuizEntityMap.put(cardQuizEntity.getCardId(), cardQuizEntity);
        }

        LongSparseArray<CardNotificationEntity> cardNotificationEntityMap = new LongSparseArray<>();
        for (CardNotificationEntity cardNotificationEntity : cardNotificationEntities) {
            cardNotificationEntityMap.put(cardNotificationEntity.getCardId(), cardNotificationEntity);
        }

        List<Card> cards = new ArrayList<>();
        for (CardEntity cardEntity : cardEntities) {
            Long id = cardEntity.getCardId();
            CardQuizEntity cardQuizEntity = cardQuizEntityMap.get(id);
            CardNotificationEntity cardNotificationEntity = cardNotificationEntityMap.get(id);
            Card card = new Card(cardEntity, cardQuizEntity, cardNotificationEntity);
            cards.add(card);
        }

        return cards;
    }

    public int getAllCardCount() {
        return dao.getAllCardCount();
    }

    public int getLearnCardCount() {
        return dao.getCardCountByQuizType(LLearnConstants.CARD_TYPE_LEARN);
    }

    public int getOverdueReviewCardCount() {
        return dao.getOverdueReviewCardCount(LLearnConstants.CARD_TYPE_REVIEW, System.currentTimeMillis());
    }

    public int getReviewCardCount() {
        return dao.getCardCountByQuizType(LLearnConstants.CARD_TYPE_REVIEW);
    }

}
