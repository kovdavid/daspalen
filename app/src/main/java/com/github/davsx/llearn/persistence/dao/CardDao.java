package com.github.davsx.llearn.persistence.dao;

import android.arch.persistence.room.*;
import com.github.davsx.llearn.persistence.entity.CardEntity;

import java.util.List;

@Dao
public interface CardDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long save(CardEntity card);

    @Delete()
    void delete(CardEntity card);

    @Query("SELECT * from cards WHERE back IS NOT NULL ORDER BY id_card ASC")
    List<CardEntity> getAllCards();

    @Query("SELECT * FROM cards WHERE id_card = :id_card")
    CardEntity getCardWithId(Long id_card);

    @Query("SELECT * FROM cards WHERE front = :front")
    CardEntity getCardWithFront(String front);

    @Query("SELECT count(*) FROM cards")
    Integer cardCount();

    @Query("SELECT * FROM cards WHERE front IN (:frontTexts)")
    List<CardEntity> getCardsWithFronts(List<String> frontTexts);
}
