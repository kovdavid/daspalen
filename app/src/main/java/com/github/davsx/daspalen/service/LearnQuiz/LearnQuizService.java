package com.github.davsx.daspalen.service.LearnQuiz;

import android.util.Log;
import com.github.davsx.daspalen.DaspalenConstants;
import com.github.davsx.daspalen.model.Card;
import com.github.davsx.daspalen.persistence.entity.CardEntity;
import com.github.davsx.daspalen.persistence.repository.DaspalenRepository;
import com.github.davsx.daspalen.service.BaseQuiz.BaseQuizCard;
import com.github.davsx.daspalen.service.BaseQuiz.BaseQuizSchedule;
import com.github.davsx.daspalen.service.BaseQuiz.CardQuizService;
import com.github.davsx.daspalen.service.BaseQuiz.QuizData;
import com.github.davsx.daspalen.service.CardImage.CardImageService;

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
// After using a card, it's weight could be recalculated, so that it would be used before or
// after a card with score 7

public class LearnQuizService implements CardQuizService {

    private static final String TAG = "daspalen|LearnQuizS";

    private DaspalenRepository repository;
    private CardImageService cardImageService;

    private BaseQuizSchedule quizSchedule;
    private List<BaseQuizCard> cards;
    private List<CardEntity> randomCards;
    private Integer totalRounds;
    private LearnQuizCard currentCard;
    private Boolean isFinished;

    public LearnQuizService(DaspalenRepository repository, CardImageService cardImageService) {
        this.repository = repository;
        this.cardImageService = cardImageService;
        this.totalRounds = 0;
        this.isFinished = false;

        Log.i(TAG, "LearnQuizService:init");
    }

    @Override
    public boolean startSession() {
        this.cards = prepareCards();

        if (this.cards == null) {
            return false; // Nothing new to learn
        }

        this.quizSchedule = new BaseQuizSchedule(new ArrayList<>(this.cards));

        this.randomCards = repository.getRandomCardEntities(DaspalenConstants.LEARN_SESSION_RANDOM_CARDS_COUNT);

        prepareNextCard();

        return true;
    }

    @Override
    public void processAnswer(String answer) {
        if (!isFinished) {
            currentCard.handleAnswer(quizSchedule, answer);
        }
        prepareNextCard();
    }

    @Override
    public Integer getCompletedRounds() {
        int completedRounds = 0;
        for (BaseQuizCard card : cards) {
            completedRounds += card.getCompletedRounds();
        }
        return completedRounds;
    }

    @Override
    public QuizData getNextCardData() {
        if (currentCard == null) {
            if (isFinished) {
                return null;
            } else {
                isFinished = true;
                return QuizData.buildFinishData();
            }
        }

        return currentCard.buildQuizData(randomCards);
    }

    @Override
    public Integer getTotalRounds() {
        return totalRounds;
    }

    private void prepareNextCard() {
        currentCard = (LearnQuizCard) quizSchedule.nextCard();
    }

    private List<BaseQuizCard> prepareCards() {
        List<Card> learnCandidates = repository.getLearnCandidateCards(DaspalenConstants.LEARN_SESSION_CANDIDATE_CARDS);
        if (learnCandidates.isEmpty()) {
            return null;
        }

        Collections.shuffle(learnCandidates);

        List<LearnQuizCard> chosenCards = new ArrayList<>();

        int newCardCounter = 0;
        for (Card card : learnCandidates) {
            if (card.getLearnScore() == 0) {
                if (newCardCounter >= DaspalenConstants.LEARN_SESSION_MAX_NEW_CARDS) {
                    continue;
                }
                newCardCounter++;
            }

            LearnQuizCard learnQuizCard = new LearnQuizCard(repository, cardImageService, card);
            chosenCards.add(learnQuizCard);
            totalRounds += learnQuizCard.getPlannedRounds();

            if (totalRounds >= DaspalenConstants.LEARN_SESSION_MAX_ROUNDS) {
                break;
            }
            if (chosenCards.size() >= DaspalenConstants.LEARN_SESSION_MAX_CARDS) {
                break;
            }
        }

        Collections.sort(chosenCards, new LearnQuizCard.LearnQuizCardComparator());

        Log.i(TAG, String.format("prepareCards size:%d", chosenCards.size()));
        for (LearnQuizCard card : chosenCards) {
            Log.i(TAG, String.format("prepareCards cardId:%d", card.getCardId()));
        }

        return new ArrayList<BaseQuizCard>(chosenCards);
    }
}
