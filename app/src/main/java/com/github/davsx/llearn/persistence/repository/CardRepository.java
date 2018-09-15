package com.github.davsx.llearn.persistence.repository;

import com.github.davsx.llearn.persistence.entity.CardEntity;

import java.util.List;

public interface CardRepository {
    CardEntity getCardWithId(Long id_card);

    CardEntity getCardWithFront(String front);

    Long save(CardEntity card);

    void saveMany(List<CardEntity> cards);

    Integer cardCount();

    Integer learnableCardCount();

    void deleteCard(CardEntity card);

    void deleteAllCards();

    List<CardEntity> getRandomCards(int limit);

    List<CardEntity> getAllCards();

    List<CardEntity> getAllValidCards();

    List<CardEntity> getLearnCandidates();
}
