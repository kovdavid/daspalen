package com.github.davsx.llearn.persistence.repository;

import com.github.davsx.llearn.persistence.entity.CardEntity;

import java.util.List;

public interface CardRepository {
    CardEntity getCardWithId(Long id_card);

    CardEntity getCardWithFront(String front);

    Long save(CardEntity card);

    void saveMany(List<CardEntity> cards);

    Integer cardCount();

    Long getMaxCardId();

    Integer learnableCardCount();

    void deleteCard(CardEntity card);

    void deleteAllCards();

    List<CardEntity> getRandomCards(int limit);

    List<CardEntity> getCardsChunked(Long id, boolean onlyIcomplete, int limit);

    List<CardEntity> searchCardsChunked(String query, Long id, boolean onlyIncomplete, int limit);

    List<CardEntity> getAllCards();

    List<CardEntity> getAllValidCards();

    List<CardEntity> getLearnCandidates();
}
