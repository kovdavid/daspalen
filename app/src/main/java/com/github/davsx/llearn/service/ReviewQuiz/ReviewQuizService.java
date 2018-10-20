package com.github.davsx.llearn.service.ReviewQuiz;

import com.github.davsx.llearn.LLearnConstants;
import com.github.davsx.llearn.persistence.entity.CardEntity;
import com.github.davsx.llearn.persistence.repository.CardRepository;
import com.github.davsx.llearn.service.BaseQuiz.BaseQuizSchedule;
import com.github.davsx.llearn.service.BaseQuiz.CardQuizService;
import com.github.davsx.llearn.service.BaseQuiz.QuizData;

import java.util.*;

public class ReviewQuizService implements CardQuizService {

    public static final String ANSWER_WRONG = "WRONG";
    public static final String ANSWER_OK = "OK";
    public static final String ANSWER_GOOD = "GOOD";

    private CardRepository cardRepository;

    private List<ReviewQuizCard> cards;
    private BaseQuizSchedule<ReviewQuizCard> quizSchedule;
    private ReviewQuizCard currentCard;
    private boolean isFinished;

    public ReviewQuizService(CardRepository cardRepository) {
        this.cardRepository = cardRepository;
        this.isFinished = false;
    }

    @Override
    public boolean startSession() {
        this.cards = prepareCards();

        if (this.cards == null) {
            return false; // Nothing new to learn
        }

        this.quizSchedule = new BaseQuizSchedule<>(new ArrayList<>(cards));

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
        Integer completedRounds = 0;
        for (ReviewQuizCard card : cards) {
            if (card.isAnswered()) completedRounds++;
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

        return currentCard.buildQuizData();
    }

    @Override
    public Integer getTotalRounds() {
        return cards.size();
    }

    private void prepareNextCard() {
        currentCard = quizSchedule.nextElem();
    }

    private List<ReviewQuizCard> prepareCards() {
        List<ReviewQuizCard> cards = new ArrayList<>();
        List<CardEntity> candidateCards = cardRepository.getReviewCandidates();

        Collections.sort(candidateCards, new ReviewQuizCard.ReviewQuizCardComparator());

        while (cards.size() < LLearnConstants.REVIEW_SESSION_MAX_CARDS && candidateCards.size() > 0) {
            CardEntity card = candidateCards.remove(0);
            cards.add(ReviewQuizCard.createUpdatableCard(cardRepository, card));
        }

        Random rng = new Random(System.currentTimeMillis());

        if (candidateCards.size() < LLearnConstants.REVIEW_SESSION_MAX_CARDS) {
            List<CardEntity> fillCandidates = cardRepository.getReviewFillCandidates();
            int fillCount = LLearnConstants.REVIEW_SESSION_MAX_CARDS - candidateCards.size();

            if (fillCandidates.size() > fillCount) {
                Set<CardEntity> fillCards = new HashSet<>();
                while (fillCards.size() < fillCount) {
                    int index = rng.nextInt(fillCandidates.size());
                    fillCards.add(fillCandidates.get(index));
                }
                for (CardEntity card : fillCards) {
                    cards.add(ReviewQuizCard.createNonUpdatableCard(cardRepository, card));
                }
            } else {
                for (CardEntity card : fillCandidates) {
                    cards.add(ReviewQuizCard.createNonUpdatableCard(cardRepository, card));
                }
            }
        }

        return cards;
    }

}
