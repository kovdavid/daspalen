package com.github.davsx.llearn.infrastructure.persistence.repository;

import com.github.davsx.llearn.domain.persistence.CardRepository;
import com.github.davsx.llearn.infrastructure.persistence.dao.CardDao;
import com.github.davsx.llearn.infrastructure.persistence.entity.CardEntity;
import dagger.Module;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;
import java.util.concurrent.Executor;

@Module @Singleton
public class CardRepositoryImpl implements CardRepository {

    private final CardDao cardDao;
    private final Executor executor;

    @Inject
    public CardRepositoryImpl(CardDao cardDao, Executor executor) {
        this.cardDao = cardDao;
        this.executor = executor;
    }

    @Override
    public List<CardEntity> getAllCards() {
        return cardDao.getAllCards();
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
    public Integer cardCount() {
        return cardDao.cardCount();
    }
}
