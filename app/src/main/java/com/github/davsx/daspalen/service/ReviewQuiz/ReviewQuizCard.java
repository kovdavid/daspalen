package com.github.davsx.daspalen.service.ReviewQuiz;

import android.util.Log;
import com.github.davsx.daspalen.DaspalenConstants;
import com.github.davsx.daspalen.model.Card;
import com.github.davsx.daspalen.persistence.entity.CardEntity;
import com.github.davsx.daspalen.persistence.repository.DaspalenRepository;
import com.github.davsx.daspalen.service.BaseQuiz.BaseQuizCard;
import com.github.davsx.daspalen.service.BaseQuiz.BaseQuizCardScheduler;
import com.github.davsx.daspalen.service.BaseQuiz.QuizData;
import com.github.davsx.daspalen.service.BaseQuiz.QuizTypeEnum;
import com.github.davsx.daspalen.service.CardImage.CardImageService;

import java.util.Comparator;
import java.util.List;

class ReviewQuizCard implements BaseQuizCard {

    private static final String TAG = "daspalen|ReviewQuizC";

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

        Log.i(TAG, String.format("init cardId:%d front:%s back:%s updateCardOnAnswer:%s",
                card.getCardId(),
                card.getFrontText(),
                card.getBackText(),
                updateCardOnAnswer));
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

    @Override
    public void handleAnswer(BaseQuizCardScheduler scheduler, String answer) {
        Log.i(TAG, String.format("handleAnswer cardId:%d answer:%s", card.getCardId(), answer));

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

    @Override
    public QuizData buildQuizData(List<CardEntity> randomCards) {
        return QuizData.build(QuizTypeEnum.REVIEW_CARD, cardImageService, card, randomCards);
    }

    @Override
    public long getCardId() {
        return card.getCardId();
    }

    @Override
    public int getCompletedRounds() {
        if (answeredCorrectly) {
            return 1;
        }
        return 0;
    }

    public static class ReviewQuizCardComparator implements Comparator<Card> {
        @Override
        public int compare(Card c1, Card c2) {
            return cardReviewDueFactor(c2).compareTo(cardReviewDueFactor(c1));
        }
    }

}
