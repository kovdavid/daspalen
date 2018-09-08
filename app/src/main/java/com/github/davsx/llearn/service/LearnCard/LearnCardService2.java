package com.github.davsx.llearn.service.LearnCard;

import com.github.davsx.llearn.LLearnConstants;
import com.github.davsx.llearn.persistence.entity.CardEntity;
import com.github.davsx.llearn.persistence.repository.CardRepository;

import java.util.*;

public class LearnCardService2 {
    private CardRepository cardRepository;
    private CardSchedule cardSchedule;
    private List<SchedulableCard> cards;
    private List<CardEntity> randomCards;

    public LearnCardService2(CardRepository cardRepository) {
        this.cardRepository = cardRepository;
    }

    public boolean startSession() {
        this.cardSchedule = new CardSchedule();

        this.cards = prepareCards();

        if (this.cards == null) {
            return false;
        }

        this.randomCards = cardRepository.getRandomCards(LLearnConstants.LEARN_SESSION_RANDOM_CARDS_COUNT);

        setUpNextCard();

        return true;
    }

    private void setUpNextCard() {
        SchedulableCard schedulableCard = cardSchedule.nextCard();
    }

    private List<SchedulableCard> prepareCards() {
        List<CardEntity> learnCandidates = cardRepository.getLearnCandidates();
        if (learnCandidates.isEmpty()) {
            return null;
        }

        Collections.shuffle(learnCandidates);

        List<SchedulableCard> chosenCards = new ArrayList<>();

        int roundCounter = 0;
        int newCardCounter = 0;
        for (CardEntity card : learnCandidates) {
            if (card.getLearnScore() == 0) {
                if (newCardCounter >= LLearnConstants.LEARN_SESSION_MAX_NEW_CARDS) {
                    continue;
                }
                newCardCounter++;
            }

            SchedulableCard schedulableCard = new SchedulableCard(card);

            chosenCards.add(new SchedulableCard(card));
            roundCounter += schedulableCard.getPlannedRounds();

            if (roundCounter >= LLearnConstants.LEARN_SESSION_MAX_ROUNDS) {
                break;
            }
            if (chosenCards.size() >= LLearnConstants.LEARN_SESSION_MAX_CARDS) {
                break;
            }
        }

        Collections.sort(chosenCards, new SchedulableCard.SchedulableCardComparator());

        return chosenCards;
    }
}
