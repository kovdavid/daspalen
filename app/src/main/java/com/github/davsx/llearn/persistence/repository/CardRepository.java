package com.github.davsx.llearn.persistence.repository;

import android.util.Log;
import com.github.davsx.llearn.LLearnConstants;
import com.github.davsx.llearn.persistence.dao.CardDao;
import com.github.davsx.llearn.persistence.entity.CardEntity;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CardRepository {

    private static final String TAG = "CardRepositoryImpl";

    private CardDao cardDao;

    @Inject
    public CardRepository(CardDao cardDao) {
        Log.d(TAG, "new CardRepositoryImpl");
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

    public List<CardEntity> getCardsChunked(Long id, boolean onlyIncomplete, int limit) {
        ArrayList<Integer> types = new ArrayList<Integer>(Arrays.asList(CardEntity.TYPE_INCOMPLETE));
        if (!onlyIncomplete) {
            types.add(CardEntity.TYPE_LEARN);
            types.add(CardEntity.TYPE_REVIEW);
        }
        return cardDao.getCardsChunked(id, types, limit);
    }

    public List<CardEntity> searchCardsChunked(String query, Long id, boolean onlyIncomplete, int limit) {
        ArrayList<Integer> types = new ArrayList<>(Arrays.asList(CardEntity.TYPE_INCOMPLETE));
        if (!onlyIncomplete) {
            types.add(CardEntity.TYPE_LEARN);
            types.add(CardEntity.TYPE_REVIEW);
        }
        String queryStr = "%" + query + "%";
        return cardDao.searchCardsChunked(queryStr, id, types, limit);
    }

    public List<CardEntity> getLearnCandidates() {
        return cardDao.getLearnCandidates(LLearnConstants.MAX_CARD_LEARN_SCORE, LLearnConstants.LEARN_SESSION_CANDIDATE_CARDS);
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
