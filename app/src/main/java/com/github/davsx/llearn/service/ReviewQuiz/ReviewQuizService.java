package com.github.davsx.llearn.service.ReviewQuiz;

import com.github.davsx.llearn.LLearnConstants;
import com.github.davsx.llearn.persistence.entity.CardEntityOld;
import com.github.davsx.llearn.persistence.repository.CardRepositoryOld;
import com.github.davsx.llearn.persistence.repository.JournalRepository;
import com.github.davsx.llearn.service.BaseQuiz.BaseQuizSchedule;
import com.github.davsx.llearn.service.BaseQuiz.CardQuizService;
import com.github.davsx.llearn.service.BaseQuiz.QuizData;
import com.github.davsx.llearn.service.CardImage.CardImageService;

import java.util.*;

public class ReviewQuizService implements CardQuizService {

    private CardRepositoryOld cardRepository;
    private JournalRepository journalRepository;
    private CardImageService cardImageService;

    private List<ReviewQuizCard> cards;
    private BaseQuizSchedule<ReviewQuizCard> quizSchedule;
    private ReviewQuizCard currentCard;
    private boolean isFinished;

    public ReviewQuizService(CardRepositoryOld cardRepository, JournalRepository journalRepository,
                             CardImageService cardImageService) {
        this.cardRepository = cardRepository;
        this.journalRepository = journalRepository;
        this.cardImageService = cardImageService;
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

    private List<ReviewQuizCard> prepareCards() {
        List<ReviewQuizCard> cards = new ArrayList<>();
        List<CardEntityOld> candidateCards = cardRepository.getReviewCandidates();

        Collections.sort(candidateCards, new ReviewQuizCard.ReviewQuizCardComparator());

        while (cards.size() < LLearnConstants.REVIEW_SESSION_MAX_CARDS && candidateCards.size() > 0) {
            CardEntityOld card = candidateCards.remove(0);
            cards.add(ReviewQuizCard.createUpdatableCard(cardRepository, journalRepository, cardImageService, card));
        }

        Random rng = new Random(System.currentTimeMillis());

        if (candidateCards.size() < LLearnConstants.REVIEW_SESSION_MAX_CARDS) {
            List<CardEntityOld> fillCandidates = cardRepository.getReviewFillCandidates();
            int fillCount = LLearnConstants.REVIEW_SESSION_MAX_CARDS - candidateCards.size();

            if (fillCandidates.size() > fillCount) {
                Set<CardEntityOld> fillCards = new HashSet<>();
                while (fillCards.size() < fillCount) {
                    int index = rng.nextInt(fillCandidates.size());
                    fillCards.add(fillCandidates.get(index));
                }
                for (CardEntityOld card : fillCards) {
                    cards.add(ReviewQuizCard.createNonUpdatableCard(cardRepository, journalRepository,
                            cardImageService, card));
                }
            } else {
                for (CardEntityOld card : fillCandidates) {
                    cards.add(ReviewQuizCard.createNonUpdatableCard(cardRepository, journalRepository,
                            cardImageService, card));
                }
            }
        }

        return cards;
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

    @Override
    public Integer getCompletedRounds() {
        Integer completedRounds = 0;
        for (ReviewQuizCard card : cards) {
            if (card.isAnsweredCorrectly()) completedRounds++;
        }
        return completedRounds;
    }

}
