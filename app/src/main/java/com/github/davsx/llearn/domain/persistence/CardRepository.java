package com.github.davsx.llearn.domain.persistence;

import com.github.davsx.llearn.infrastructure.persistence.entity.CardEntity;
import dagger.Component;

import java.util.List;

@Component
public interface CardRepository {
    List<CardEntity> getAllCards();
    CardEntity getCardWithId(Long id_card);
    CardEntity getCardWithFront(String front);
    Long save(CardEntity card);
    Integer cardCount();
}
