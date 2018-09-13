package com.github.davsx.llearn.persistence.repository;

import android.util.Log;
import com.github.davsx.llearn.LLearnConstants;
import com.github.davsx.llearn.persistence.dao.CardDao;
import com.github.davsx.llearn.persistence.entity.CardEntity;

import javax.inject.Inject;
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
    public List<CardEntity> getAllCards() {
        return cardDao.getAllCards();
    }

    @Override
    public List<CardEntity> getAllValidCards() {
        return cardDao.getAllValidCards();
    }

    @Override
    public CardEntity getCardWithId(Long id_card) {
        return cardDao.getCardWithId(id_card);
    }

    @Override
    public CardEntity getCardWithFront(String front) {
        return cardDao.getCardWithFront(front);
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
    public List<CardEntity> getLearnCandidates() {
        return cardDao.getLearnCandidates(LLearnConstants.MAX_CARD_LEARN_SCORE, LLearnConstants.LEARN_SESSION_CANDIDATE_CARDS);
    }

    @Override
    public Integer cardCount() {
        return cardDao.cardCount();
    }

    @Override
    public void deleteCard(CardEntity card) {
        cardDao.delete(card);
    }

    @Override
    public List<CardEntity> getRandomCards(int limit) {
        return cardDao.getRandomCards(limit);
    }
}
