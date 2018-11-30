package com.github.davsx.daspalen.service.ReviewQuiz;

import android.util.Log;
import com.github.davsx.daspalen.DaspalenConstants;
import com.github.davsx.daspalen.model.Card;
import com.github.davsx.daspalen.persistence.repository.DaspalenRepository;
import com.github.davsx.daspalen.service.BaseQuiz.BaseQuizCard;
import com.github.davsx.daspalen.service.BaseQuiz.BaseQuizSchedule;
import com.github.davsx.daspalen.service.BaseQuiz.CardQuizService;
import com.github.davsx.daspalen.service.BaseQuiz.QuizData;
import com.github.davsx.daspalen.service.CardImage.CardImageService;

import java.util.*;

public class ReviewQuizService implements CardQuizService {

    private static final String TAG = "daspalen|ReviewQuizS";

    private DaspalenRepository repository;
    private CardImageService cardImageService;

    private List<BaseQuizCard> cards;
    private BaseQuizSchedule quizSchedule;
    private BaseQuizCard currentCard;
    private boolean isFinished;

    public ReviewQuizService(DaspalenRepository repository, CardImageService cardImageService) {
        this.repository = repository;
        this.cardImageService = cardImageService;
        this.isFinished = false;

        Log.i(TAG, "ReviewQuizService:init");
    }

    @Override
    public boolean startSession() {
        this.cards = prepareCards();

        if (this.cards == null) {
            return false; // Nothing new to learn
        }

        this.quizSchedule = new BaseQuizSchedule(new ArrayList<>(cards));

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

        return currentCard.buildQuizData(null);
    }

    @Override
    public Integer getTotalRounds() {
        return cards.size();
    }

    private void prepareNextCard() {
        currentCard = quizSchedule.nextCard();
    }

    private List<BaseQuizCard> prepareCards() {
        List<BaseQuizCard> cards = new ArrayList<>();
        List<Card> candidateCards =
                repository.getReviewCandidateCards(DaspalenConstants.REVIEW_SESSION_CANDIDATE_CARDS);

        Log.i(TAG, String.format("prepareCards candidateCards.size:%d", candidateCards.size()));

        Collections.sort(candidateCards, new ReviewQuizCard.ReviewQuizCardComparator());

        while (cards.size() < DaspalenConstants.REVIEW_SESSION_MAX_CARDS && candidateCards.size() > 0) {
            Card card = candidateCards.remove(0);
            cards.add(ReviewQuizCard.createUpdatableCard(repository, cardImageService, card));
            Log.i(TAG, String.format("prepareCards cardId:%d", card.getCardId()));
        }

        Random rng = new Random(System.currentTimeMillis());

        if (cards.size() < DaspalenConstants.REVIEW_SESSION_MAX_CARDS) {
            List<Long> cardIds = new ArrayList<>();
            for (BaseQuizCard card : cards) {
                cardIds.add(card.getCardId());
            }
            List<Card> fillCandidates = repository.getReviewFillCandidates(
                    DaspalenConstants.REVIEW_SESSION_CANDIDATE_CARDS, cardIds);
            int fillCount = DaspalenConstants.REVIEW_SESSION_MAX_CARDS - candidateCards.size();

            Log.i(TAG, String.format("prepareCards adding fill cards fillCandidates.size:%d fillCount:%d", fillCandidates.size(), fillCount));

            if (fillCandidates.size() > fillCount) {
                Set<Card> fillCards = new HashSet<>();
                while (fillCards.size() < fillCount) {
                    int index = rng.nextInt(fillCandidates.size());
                    fillCards.add(fillCandidates.get(index));
                }
                for (Card card : fillCards) {
                    cards.add(ReviewQuizCard.createNonUpdatableCard(repository, cardImageService, card));
                    Log.i(TAG, String.format("prepareCards fillCard cardId:%d", card.getCardId()));
                }
            } else {
                for (Card card : fillCandidates) {
                    cards.add(ReviewQuizCard.createNonUpdatableCard(repository, cardImageService, card));
                    Log.i(TAG, String.format("prepareCards fillCard cardId:%d", card.getCardId()));
                }
            }
        }

        Log.i(TAG, String.format("prepareCards cards.size:%d", cards.size()));

        return cards;
    }

}
