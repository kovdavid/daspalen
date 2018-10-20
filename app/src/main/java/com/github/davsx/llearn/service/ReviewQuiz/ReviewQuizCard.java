package com.github.davsx.llearn.service.ReviewQuiz;

import com.github.davsx.llearn.persistence.entity.CardEntity;
import com.github.davsx.llearn.persistence.repository.CardRepository;
import com.github.davsx.llearn.service.BaseQuiz.BaseQuizCardScheduler;
import com.github.davsx.llearn.service.BaseQuiz.QuizData;
import com.github.davsx.llearn.service.BaseQuiz.QuizTypeEnum;
import com.github.davsx.llearn.service.CardImage.CardImageService;

import java.util.Comparator;

class ReviewQuizCard {

    private CardRepository cardRepository;
    private CardImageService cardImageService;

    private CardEntity cardEntity;
    private boolean updateCardOnAnswer;
    private boolean answered = false;

    private ReviewQuizCard(CardRepository cardRepository, CardImageService cardImageService, CardEntity cardEntity, boolean updateCardOnAnswer) {
        this.cardRepository = cardRepository;
        this.cardImageService = cardImageService;
        this.cardEntity = cardEntity;
        this.updateCardOnAnswer = updateCardOnAnswer;
    }

    static ReviewQuizCard createUpdatableCard(CardRepository cardRepository, CardImageService cardImageService, CardEntity cardEntity) {
        return new ReviewQuizCard(cardRepository, cardImageService, cardEntity, true);
    }

    static ReviewQuizCard createNonUpdatableCard(CardRepository cardRepository, CardImageService cardImageService, CardEntity cardEntity) {
        return new ReviewQuizCard(cardRepository, cardImageService, cardEntity, false);
    }

    private static Double cardReviewDueFactor(CardEntity card) {
        long reviewInterval = card.getNextReviewAt() - card.getLastReviewAt();
        long overdueInterval = System.currentTimeMillis() - card.getNextReviewAt();
        return (double) (overdueInterval / reviewInterval);
    }

    void handleAnswer(BaseQuizCardScheduler<ReviewQuizCard> scheduler, String answer) {
        if (answer.equals(ReviewQuizService.ANSWER_GOOD)) {
            if (updateCardOnAnswer && !answered) {
                cardEntity.processGoodReviewAnswer();
                cardRepository.save(cardEntity);
            }
        } else {
            if (updateCardOnAnswer && !answered) {
                if (answer.equals(ReviewQuizService.ANSWER_OK)) {
                    cardEntity.processOkReviewAnswer();
                } else {
                    cardEntity.processBadReviewAnswer();
                }
                cardRepository.save(cardEntity);
            }
            scheduler.scheduleToEnd(this);
        }
        answered = true;
    }

    QuizData buildQuizData() {
        return QuizData.build(QuizTypeEnum.REVIEW_CARD, cardImageService, cardEntity, null);
    }

    boolean isAnswered() {
        return this.answered;
    }

    public static class ReviewQuizCardComparator implements Comparator<CardEntity> {
        @Override
        public int compare(CardEntity c1, CardEntity c2) {
            return cardReviewDueFactor(c2).compareTo(cardReviewDueFactor(c1));
        }
    }

}
