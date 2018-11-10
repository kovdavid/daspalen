package com.github.davsx.llearn.persistence.repository;

import com.github.davsx.llearn.LLearnConstants;
import com.github.davsx.llearn.persistence.dao.CardDaoOld;
import com.github.davsx.llearn.persistence.entity.CardEntityOld;

import javax.inject.Inject;
import java.util.List;

public class CardRepositoryOld {

    private CardDaoOld cardDao;

    @Inject
    public CardRepositoryOld(CardDaoOld cardDao) {
        this.cardDao = cardDao;
    }

    public CardEntityOld getCardWithId(Long id_card) {
        return cardDao.getCardWithId(id_card);
    }

    public CardEntityOld findDuplicateCard(String front, String back) {
        return cardDao.findDuplicateCard(front, back);
    }

    public Long save(CardEntityOld card) {
        return cardDao.save(card);
    }

    public void saveMany(List<CardEntityOld> cards) {
        cardDao.saveMany(cards);
    }

    public Integer allCardsCount() {
        return cardDao.allCardsCount();
    }

    public Integer learnableCardCount() {
        return cardDao.learnableCardCount();
    }

    public Integer reviewableCardCount() {
        return cardDao.reviewableCardCount();
    }

    public Integer reviewableOverdueCardCount() {
        return cardDao.reviewableOverdueCardCount(System.currentTimeMillis());
    }


    public void deleteCard(CardEntityOld card) {
        cardDao.delete(card);
    }

    public void deleteAllCards() {
        cardDao.deleteAllCards();
    }

    public List<CardEntityOld> getRandomCards(int limit) {
        return cardDao.getRandomCards(limit);
    }

    public List<CardEntityOld> getCardsChunked(Long id, List<Integer> types, int limit) {
        return cardDao.getCardsChunked(id, types, limit);
    }

    public List<CardEntityOld> searchCardsChunked(String query,
                                                  Long id,
                                                  List<Integer> types,
                                                  int limit) {
        String queryStr = "%" + query + "%";
        return cardDao.searchCardsChunked(queryStr, id, types, limit);
    }

    public List<CardEntityOld> getLearnCandidates() {
        return cardDao.getLearnCandidates(LLearnConstants.LEARN_SESSION_CANDIDATE_CARDS);
    }

    public List<CardEntityOld> getReviewCandidates() {
        return cardDao.getReviewCandidates(
                System.currentTimeMillis(),
                LLearnConstants.REVIEW_SESSION_CANDIDATE_CARDS
        );
    }

    public List<CardEntityOld> getReviewFillCandidates() {
        return cardDao.getReviewFillCandidates(
                System.currentTimeMillis(),
                LLearnConstants.REVIEW_SESSION_CANDIDATE_CARDS
        );
    }
}
