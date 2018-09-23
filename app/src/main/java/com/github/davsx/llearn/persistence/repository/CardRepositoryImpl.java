package com.github.davsx.llearn.persistence.repository;

import android.util.Log;
import com.github.davsx.llearn.LLearnConstants;
import com.github.davsx.llearn.persistence.dao.CardDao;
import com.github.davsx.llearn.persistence.entity.CardEntity;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CardRepositoryImpl implements CardRepository {

    private static final String TAG = "CardRepositoryImpl";

    private CardDao cardDao;

    @Inject
    public CardRepositoryImpl(CardDao cardDao) {
        Log.d(TAG, "new CardRepositoryImpl");
        this.cardDao = cardDao;
    }

    @Override
    public CardEntity getCardWithId(Long id_card) {
        return cardDao.getCardWithId(id_card);
    }

    @Override
    public CardEntity findDuplicateCard(String front, String back) {
        return cardDao.findDuplicateCard(front, back);
    }

    @Override
    public Long save(CardEntity card) {
        return cardDao.save(card);
    }

    @Override
    public void saveMany(List<CardEntity> cards) {
        cardDao.saveMany(cards);
    }

    @Override
    public Integer learnableCardCount() {
        return cardDao.learnableCardCount();
    }

    @Override
    public void deleteCard(CardEntity card) {
        cardDao.delete(card);
    }

    @Override
    public void deleteAllCards() {
        cardDao.deleteAllCards();
    }

    @Override
    public List<CardEntity> getRandomCards(int limit) {
        return cardDao.getRandomCards(limit);
    }

    @Override
    public List<CardEntity> getCardsChunked(Long id, boolean onlyIncomplete, int limit) {
        ArrayList<Integer> types = new ArrayList<Integer>(Arrays.asList(CardEntity.TYPE_INCOMPLETE));
        if (!onlyIncomplete) {
            types.add(CardEntity.TYPE_LEARN);
            types.add(CardEntity.TYPE_REVIEW);
        }
        return cardDao.getCardsChunked(id, types, limit);
    }

    @Override
    public List<CardEntity> searchCardsChunked(String query, Long id, boolean onlyIncomplete, int limit) {
        ArrayList<Integer> types = new ArrayList<>(Arrays.asList(CardEntity.TYPE_INCOMPLETE));
        if (!onlyIncomplete) {
            types.add(CardEntity.TYPE_LEARN);
            types.add(CardEntity.TYPE_REVIEW);
        }
        String queryStr = "%" + query + "%";
        return cardDao.searchCardsChunked(queryStr, id, types, limit);
    }

    @Override
    public List<CardEntity> getLearnCandidates() {
        return cardDao.getLearnCandidates(LLearnConstants.MAX_CARD_LEARN_SCORE, LLearnConstants.LEARN_SESSION_CANDIDATE_CARDS);
    }
}
