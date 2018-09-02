package com.github.davsx.llearn.data.LearnCard;

import com.github.davsx.llearn.persistence.entity.CardEntity;
import com.github.davsx.llearn.persistence.repository.CardRepository;

import java.util.ArrayList;

public class LearnCardSelector {
    private CardRepository cardRepository;

    public LearnCardSelector(CardRepository cardRepository) {
        this.cardRepository = cardRepository;
    }

    public ArrayList<CardEntity> selectCards() {
        ArrayList<CardEntity> candidateCards = (ArrayList<CardEntity>) cardRepository.getLearnCandidates();
        return new ArrayList<>();
    }
}
