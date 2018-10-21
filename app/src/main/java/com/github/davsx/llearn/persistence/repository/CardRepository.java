package com.github.davsx.llearn.persistence.repository;

import com.github.davsx.llearn.LLearnConstants;
import com.github.davsx.llearn.persistence.dao.CardDao;
import com.github.davsx.llearn.persistence.entity.CardEntity;

import javax.inject.Inject;
import java.util.List;

public class CardRepository {

    private CardDao cardDao;

    @Inject
    public CardRepository(CardDao cardDao) {
        this.cardDao = cardDao;
    }

    public CardEntity getCardWithId(Long id_card) {
        return cardDao.getCardWithId(id_card);
    }

    public CardEntity findDuplicateCard(String front, String back) {
        return cardDao.findDuplicateCard(front, back);
    }

    public Long save(CardEntity card) {
        return cardDao.save(card);
    }

    public void saveMany(List<CardEntity> cards) {
        cardDao.saveMany(cards);
    }

    public Integer allCardsCount() {
        return cardDao.allCardsCount();
    }

    public Integer learnableCardCount() {
        return cardDao.learnableCardCount();
    }

    public Integer reviewableCardCount() {
        return cardDao.reviewableCardCount(System.currentTimeMillis());
    }

    public void deleteCard(CardEntity card) {
        cardDao.delete(card);
    }

    public void deleteAllCards() {
        cardDao.deleteAllCards();
    }

    public List<CardEntity> getRandomCards(int limit) {
        return cardDao.getRandomCards(limit);
    }

    public List<CardEntity> getCardsChunked(Long id, List<Integer> types, int limit) {
        return cardDao.getCardsChunked(id, types, limit);
    }

    public List<CardEntity> searchCardsChunked(String query,
                                               Long id,
                                               List<Integer> types,
                                               int limit) {
        String queryStr = "%" + query + "%";
        return cardDao.searchCardsChunked(queryStr, id, types, limit);
    }

    public List<CardEntity> getLearnCandidates() {
        return cardDao.getLearnCandidates(
                LLearnConstants.MAX_CARD_LEARN_SCORE,
                LLearnConstants.LEARN_SESSION_CANDIDATE_CARDS
        );
    }

    public List<CardEntity> getReviewCandidates() {
        return cardDao.getReviewCandidates(
                System.currentTimeMillis(),
                LLearnConstants.REVIEW_SESSION_CANDIDATE_CARDS
        );
    }

    public List<CardEntity> getReviewFillCandidates() {
        return cardDao.getReviewFillCandidates(
                System.currentTimeMillis(),
                LLearnConstants.REVIEW_SESSION_CANDIDATE_CARDS
        );
    }
}
