package com.github.davsx.llearn.persistence.repository;

import com.github.davsx.llearn.persistence.entity.CardEntity;

import java.util.List;

public interface CardRepository {
    List<CardEntity> getAllCards();

    List<CardEntity> getAllValidCards();

    CardEntity getCardWithId(Long id_card);

    CardEntity getCardWithFront(String front);

    Long save(CardEntity card);

    void saveMany(List<CardEntity> cards);

    List<CardEntity> getLearnCandidates();

    Integer cardCount();

    void deleteCard(CardEntity card);

    List<CardEntity> getRandomCards(int limit);
}
