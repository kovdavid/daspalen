package com.github.davsx.daspalen.service.ReviewQuiz;

import android.util.Log;
import com.github.davsx.daspalen.DaspalenConstants;
import com.github.davsx.daspalen.model.Card;
import com.github.davsx.daspalen.persistence.repository.DaspalenRepository;
import com.github.davsx.daspalen.service.BaseQuiz.BaseQuizCardScheduler;
import com.github.davsx.daspalen.service.BaseQuiz.QuizData;
import com.github.davsx.daspalen.service.BaseQuiz.QuizTypeEnum;
import com.github.davsx.daspalen.service.CardImage.CardImageService;

import java.util.Comparator;

class ReviewQuizCard {

    private static final String TAG = "ReviewQuizCard";

    private DaspalenRepository repository;
    private CardImageService cardImageService;

    private Card card;
    private boolean updateCardOnAnswer;
    private boolean answered = false;
    private boolean answeredCorrectly = false;

    private ReviewQuizCard(DaspalenRepository repository,
                           CardImageService cardImageService,
                           Card card,
                           boolean updateCardOnAnswer) {
        this.repository = repository;
        this.cardImageService = cardImageService;
        this.card = card;
        this.updateCardOnAnswer = updateCardOnAnswer;

        logCard("init");
    }

    static ReviewQuizCard createUpdatableCard(DaspalenRepository repository,
                                              CardImageService cardImageService,
                                              Card card) {
        return new ReviewQuizCard(repository, cardImageService, card, true);
    }

    static ReviewQuizCard createNonUpdatableCard(DaspalenRepository repository,
                                                 CardImageService cardImageService,
                                                 Card cardEntity) {
        return new ReviewQuizCard(repository, cardImageService, cardEntity, false);
    }

    private static Double cardReviewDueFactor(Card card) {
        long reviewInterval = card.getNextReviewAt() - card.getLastReviewAt();
        long overdueInterval = System.currentTimeMillis() - card.getNextReviewAt();
        return (double) (overdueInterval / reviewInterval);
    }

    private void logCard(String prefix) {
        Log.d(TAG, String.format("%s cardId:%d front:%s answeredCorrectly:%s fillCard:%s",
                prefix, card.getCardId(), card.getFrontText(), answeredCorrectly,
                !updateCardOnAnswer));
    }

    void handleAnswer(BaseQuizCardScheduler<ReviewQuizCard> scheduler, String answer) {
        switch (answer) {
            case DaspalenConstants.REVIEW_ANSWER_GOOD:
                logCard("handleAnswer GOOD");
                break;
            case DaspalenConstants.REVIEW_ANSWER_OK:
                logCard("handleAnswer OK");
                break;
            default:
                logCard("handleAnswer BAD");
                break;
        }

        if (answer.equals(DaspalenConstants.REVIEW_ANSWER_GOOD)) {
            if (updateCardOnAnswer && !answered) {
                card.processGoodReviewAnswer();
                repository.updateCard(card);
            }
            answeredCorrectly = true;
        } else {
            if (updateCardOnAnswer && !answered) {
                if (answer.equals(DaspalenConstants.REVIEW_ANSWER_OK)) {
                    card.processOkReviewAnswer();
                } else {
                    card.processBadReviewAnswer();
                }
                repository.updateCard(card);
            }
            scheduler.scheduleToEnd(this);
        }

        answered = true;
    }

    QuizData buildQuizData() {
        return QuizData.build(QuizTypeEnum.REVIEW_CARD, cardImageService, card, null);
    }

    boolean isAnsweredCorrectly() {
        return this.answeredCorrectly;
    }

    public static class ReviewQuizCardComparator implements Comparator<Card> {
        @Override
        public int compare(Card c1, Card c2) {
            return cardReviewDueFactor(c2).compareTo(cardReviewDueFactor(c1));
        }
    }

}
