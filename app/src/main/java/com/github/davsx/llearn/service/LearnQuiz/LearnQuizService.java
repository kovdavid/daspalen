package com.github.davsx.llearn.service.LearnQuiz;

import com.github.davsx.llearn.LLearnConstants;
import com.github.davsx.llearn.persistence.entity.CardEntity;
import com.github.davsx.llearn.persistence.repository.CardRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// Rules:
// * Max 5 new cards
// * Use 1 new card if possible
// * Max score is 8
// * Cards with score 0-3 use 3x
// * Cards with score 4-5 use 2x
// * Cards with score 6-7 use 1x
// * Max 20 rounds (calculated from card usage above)
// * For cards with !score 0! || !lastLearned > 1 week! use "show card" first
// *** That said, some cards will be blocked by "show card" before first real use

// Question is: how to schedule cards with score: 0 0 2 4 6 7
// For 0 - do the first real card 1-2 cards after the show card - space out the rest

// Do a weight system, where we would use the cards with the lowest weight
// After using a card, it's weight could be recalculated, so that it would be used before or after a card with score 7

public class LearnQuizService {
    private CardRepository cardRepository;
    private LearnQuizSchedule learnQuizSchedule;
    private List<LearnQuizCard> cards;
    private List<CardEntity> randomCards;
    private Integer totalRounds;
    private LearnQuizCard currentCard;
    private Boolean isFinished;

    public LearnQuizService(CardRepository cardRepository) {
        this.cardRepository = cardRepository;
        this.totalRounds = 0;
        this.isFinished = false;
    }

    public boolean startSession() {
        this.cards = prepareCards();

        if (this.cards == null) {
            return false; // Nothing new to learn
        }

        ArrayList<LearnQuizCard> cardQueue = new ArrayList<>(this.cards);
        this.learnQuizSchedule = new LearnQuizSchedule<LearnQuizCard>(cardQueue);

        this.randomCards = cardRepository.getRandomCards(LLearnConstants.LEARN_SESSION_RANDOM_CARDS_COUNT);

        prepareNextCard();

        return true;
    }

    private void prepareNextCard() {
        currentCard = (LearnQuizCard) learnQuizSchedule.nextElem();
    }

    public LearnQuizData getNextCardData() {
        if (currentCard == null) {
            if (isFinished) {
                return null;
            } else {
                isFinished = true;
                return LearnQuizData.buildFinishData();
            }
        }

        return currentCard.buildQuizData(randomCards);
    }

    private List<LearnQuizCard> prepareCards() {
        List<CardEntity> learnCandidates = cardRepository.getLearnCandidates();
        if (learnCandidates.isEmpty()) {
            return null;
        }

        Collections.shuffle(learnCandidates);

        List<LearnQuizCard> chosenCards = new ArrayList<>();

        int newCardCounter = 0;
        for (CardEntity card : learnCandidates) {
            if (card.getLearnScore() == 0) {
                if (newCardCounter >= LLearnConstants.LEARN_SESSION_MAX_NEW_CARDS) {
                    continue;
                }
                newCardCounter++;
            }

            LearnQuizCard learnQuizCard = new LearnQuizCard(cardRepository, card);
            chosenCards.add(learnQuizCard);
            totalRounds += learnQuizCard.getPlannedRounds();

            if (totalRounds >= LLearnConstants.LEARN_SESSION_MAX_ROUNDS) {
                break;
            }
            if (chosenCards.size() >= LLearnConstants.LEARN_SESSION_MAX_CARDS) {
                break;
            }
        }

        Collections.sort(chosenCards, new LearnQuizCard.LearnQuizCardComparator());

        return chosenCards;
    }

    public void processAnswer(String answer) {
        if (!isFinished) {
            currentCard.handleAnswer(learnQuizSchedule, answer);
        }
        prepareNextCard();
    }

    public Integer getCompletedRounds() {
        Integer completedRounds = 0;
        for (LearnQuizCard card : cards) {
            completedRounds += card.getCompletedRounds();
        }
        return completedRounds;
    }

    public Integer getTotalRounds() {
        return totalRounds;
    }
}
