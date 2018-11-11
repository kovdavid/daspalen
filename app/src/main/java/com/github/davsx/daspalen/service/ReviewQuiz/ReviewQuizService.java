package com.github.davsx.daspalen.service.ReviewQuiz;

import com.github.davsx.daspalen.DaspalenConstants;
import com.github.davsx.daspalen.model.Card;
import com.github.davsx.daspalen.persistence.repository.DaspalenRepository;
import com.github.davsx.daspalen.service.BaseQuiz.BaseQuizSchedule;
import com.github.davsx.daspalen.service.BaseQuiz.CardQuizService;
import com.github.davsx.daspalen.service.BaseQuiz.QuizData;
import com.github.davsx.daspalen.service.CardImage.CardImageService;

import java.util.*;

public class ReviewQuizService implements CardQuizService {

    private DaspalenRepository repository;
    private CardImageService cardImageService;

    private List<ReviewQuizCard> cards;
    private BaseQuizSchedule<ReviewQuizCard> quizSchedule;
    private ReviewQuizCard currentCard;
    private boolean isFinished;

    public ReviewQuizService(DaspalenRepository repository, CardImageService cardImageService) {
        this.repository = repository;
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

    @Override
    public Integer getCompletedRounds() {
        Integer completedRounds = 0;
        for (ReviewQuizCard card : cards) {
            if (card.isAnsweredCorrectly()) completedRounds++;
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

    private List<ReviewQuizCard> prepareCards() {
        List<ReviewQuizCard> cards = new ArrayList<>();
        List<Card> candidateCards = repository.getReviewCandidateCards(DaspalenConstants.REVIEW_SESSION_CANDIDATE_CARDS);

        Collections.sort(candidateCards, new ReviewQuizCard.ReviewQuizCardComparator());

        while (cards.size() < DaspalenConstants.REVIEW_SESSION_MAX_CARDS && candidateCards.size() > 0) {
            Card card = candidateCards.remove(0);
            cards.add(ReviewQuizCard.createUpdatableCard(repository, cardImageService, card));
        }

        Random rng = new Random(System.currentTimeMillis());

        if (candidateCards.size() < DaspalenConstants.REVIEW_SESSION_MAX_CARDS) {
            List<Long> cardIds = new ArrayList<>();
            for (Card card : candidateCards) {
                cardIds.add(card.getCardId());
            }
            List<Card> fillCandidates = repository.getReviewFillCandidates(
                    DaspalenConstants.REVIEW_SESSION_CANDIDATE_CARDS, cardIds);
            int fillCount = DaspalenConstants.REVIEW_SESSION_MAX_CARDS - candidateCards.size();

            if (fillCandidates.size() > fillCount) {
                Set<Card> fillCards = new HashSet<>();
                while (fillCards.size() < fillCount) {
                    int index = rng.nextInt(fillCandidates.size());
                    fillCards.add(fillCandidates.get(index));
                }
                for (Card card : fillCards) {
                    cards.add(ReviewQuizCard.createNonUpdatableCard(repository, cardImageService, card));
                }
            } else {
                for (Card card : fillCandidates) {
                    cards.add(ReviewQuizCard.createNonUpdatableCard(repository, cardImageService, card));
                }
            }
        }

        return cards;
    }

    private void prepareNextCard() {
        currentCard = quizSchedule.nextElem();
    }

}
