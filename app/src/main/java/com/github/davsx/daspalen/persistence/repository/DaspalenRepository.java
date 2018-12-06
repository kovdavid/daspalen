package com.github.davsx.daspalen.persistence.repository;

import android.util.LongSparseArray;
import com.github.davsx.daspalen.DaspalenConstants;
import com.github.davsx.daspalen.model.Card;
import com.github.davsx.daspalen.persistence.dao.DaspalenDao;
import com.github.davsx.daspalen.persistence.entity.CardEntity;
import com.github.davsx.daspalen.persistence.entity.CardNotificationEntity;
import com.github.davsx.daspalen.persistence.entity.CardQuizEntity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DaspalenRepository {

    private DaspalenDao dao;

    public DaspalenRepository(DaspalenDao dao) {
        this.dao = dao;
    }

    public void wipeData() {
        dao.wipeData();
    }

    public void createNewCards(List<Card> newCards) {
        dao.createNewCards(newCards);
    }

    public long createNewCard(Card card) {
        return dao.createNewCard(card);
    }

    public void updateCard(Card card) {
        CardEntity cardEntity = card.isCardEntityChanged() ? card.getCardEntity() : null;
        CardQuizEntity cardQuizEntity = card.isCardQuizEntityChanged() ? card.getCardQuizEntity() : null;
        CardNotificationEntity cardNotificationEntity = card.isCardNotificationEntityChanged() ?
                card.getCardNotificationEntity() : null;
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
        String queryString = "%" + searchQuery + "%";
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

    public CardEntity findDuplicateCardEntity(String frontText, String backText) {
        return dao.findDuplicateCardEntity(frontText, backText);
    }

    public List<CardEntity> getRandomCardEntities(Integer count) {
        List<Integer> types = new ArrayList<>(
                Arrays.asList(DaspalenConstants.CARD_TYPE_LEARN, DaspalenConstants.CARD_TYPE_REVIEW));

        return dao.getRandomCardEntities(types, count);
    }

    public List<Card> getLearnCandidateCards(Integer count) {
        List<CardEntity> cardEntities = dao.getLearnCandidateCardEntities(DaspalenConstants.CARD_TYPE_LEARN, count);
        return loadCards(cardEntities);
    }

    public List<Card> getReviewCandidateCards(Integer count) {
        List<CardEntity> cardEntities = dao.getReviewCandidateCardEntities(DaspalenConstants.CARD_TYPE_REVIEW,
                System.currentTimeMillis(), count);
        return loadCards(cardEntities);
    }

    public List<Card> getReviewFillCandidates(Integer count, List<Long> cardIdBlacklist) {
        List<CardEntity> cardEntities = dao.getReviewFillCardEntities(
                DaspalenConstants.CARD_TYPE_REVIEW, cardIdBlacklist, count);
        return loadCards(cardEntities);
    }

    public List<Card> getCardNotificationCandidates(int limit) {
        int typeBlacklist = DaspalenConstants.CARD_TYPE_INCOMPLETE;
        List<CardEntity> cardEntities = dao.getCardNotificationCandidateEntities(typeBlacklist, limit);
        return loadCards(cardEntities);
    }

    public int getAllCardCount() {
        return dao.getAllCardCount();
    }

    public int getLearnCardCount() {
        return dao.getCardCountByQuizType(DaspalenConstants.CARD_TYPE_LEARN);
    }

    public int getOverdueReviewCardCount() {
        return dao.getOverdueReviewCardCount(DaspalenConstants.CARD_TYPE_REVIEW, System.currentTimeMillis());
    }

    public int getReviewCardCount() {
        return dao.getCardCountByQuizType(DaspalenConstants.CARD_TYPE_REVIEW);
    }

}
