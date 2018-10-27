package com.github.davsx.llearn.service.ReviewQuiz;

import android.util.Log;
import com.github.davsx.llearn.LLearnConstants;
import com.github.davsx.llearn.persistence.entity.CardEntity;
import com.github.davsx.llearn.persistence.entity.JournalEntity;
import com.github.davsx.llearn.persistence.repository.CardRepository;
import com.github.davsx.llearn.persistence.repository.JournalRepository;
import com.github.davsx.llearn.service.BaseQuiz.BaseQuizCardScheduler;
import com.github.davsx.llearn.service.BaseQuiz.QuizData;
import com.github.davsx.llearn.service.BaseQuiz.QuizTypeEnum;
import com.github.davsx.llearn.service.CardImage.CardImageService;

import java.util.Comparator;

class ReviewQuizCard {

    private static final String TAG = "ReviewQuizCard";

    private CardRepository cardRepository;
    private JournalRepository journalRepository;
    private CardImageService cardImageService;

    private CardEntity cardEntity;
    private boolean updateCardOnAnswer;
    private boolean answered = false;
    private boolean answeredCorrectly = false;

    private ReviewQuizCard(CardRepository cardRepository, JournalRepository journalRepository,
                           CardImageService cardImageService, CardEntity cardEntity, boolean updateCardOnAnswer) {
        this.cardRepository = cardRepository;
        this.journalRepository = journalRepository;
        this.cardImageService = cardImageService;
        this.cardEntity = cardEntity;
        this.updateCardOnAnswer = updateCardOnAnswer;

        logCard("init");
    }

    static ReviewQuizCard createUpdatableCard(CardRepository cardRepository,
                                              JournalRepository journalRepository,
                                              CardImageService cardImageService,
                                              CardEntity cardEntity) {
        return new ReviewQuizCard(cardRepository, journalRepository, cardImageService, cardEntity, true);
    }

    static ReviewQuizCard createNonUpdatableCard(CardRepository cardRepository,
                                                 JournalRepository journalRepository,
                                                 CardImageService cardImageService,
                                                 CardEntity cardEntity) {
        return new ReviewQuizCard(cardRepository, journalRepository, cardImageService, cardEntity, false);
    }

    private static Double cardReviewDueFactor(CardEntity card) {
        long reviewInterval = card.getNextReviewAt() - card.getLastReviewAt();
        long overdueInterval = System.currentTimeMillis() - card.getNextReviewAt();
        return (double) (overdueInterval / reviewInterval);
    }

    private void logCard(String prefix) {
        Log.d(TAG, String.format("%s cardId:%d front:%s back:%s answered:%s answeredCorrectly:%s updateCardOnAnswer:%s",
                prefix, cardEntity.getId(), cardEntity.getFront(), cardEntity.getBack(), answered, answeredCorrectly,
                updateCardOnAnswer));
    }

    void handleAnswer(BaseQuizCardScheduler<ReviewQuizCard> scheduler, String answer) {

        JournalEntity journal = new JournalEntity();
        journal.setTimestamp(System.currentTimeMillis());
        journal.setCardType(LLearnConstants.CARD_TYPE_REVIEW);
        journal.setCardId(cardEntity.getId());

        switch (answer) {
            case LLearnConstants.REVIEW_ANSWER_GOOD:
                logCard("handleAnswer GOOD");
                journal.setAnswer(LLearnConstants.JOURNAL_ANSWER_GOOD);
                break;
            case LLearnConstants.REVIEW_ANSWER_OK:
                logCard("handleAnswer OK");
                journal.setAnswer(LLearnConstants.JOURNAL_ANSWER_OK);
                break;
            default:
                journal.setAnswer(LLearnConstants.JOURNAL_ANSWER_BAD);
                logCard("handleAnswer BAD");
                break;
        }

        if (answer.equals(LLearnConstants.REVIEW_ANSWER_GOOD)) {
            if (updateCardOnAnswer && !answered) {
                cardEntity.processGoodReviewAnswer();
                cardRepository.save(cardEntity);
            }
            answeredCorrectly = true;
        } else {
            if (updateCardOnAnswer && !answered) {
                if (answer.equals(LLearnConstants.REVIEW_ANSWER_OK)) {
                    cardEntity.processOkReviewAnswer();
                } else {
                    cardEntity.processBadReviewAnswer();
                }
                cardRepository.save(cardEntity);
            }
            scheduler.scheduleToEnd(this);
        }

        answered = true;
        journalRepository.save(journal);
    }

    QuizData buildQuizData() {
        return QuizData.build(QuizTypeEnum.REVIEW_CARD, cardImageService, cardEntity, null);
    }

    boolean isAnsweredCorrectly() {
        return this.answeredCorrectly;
    }

    public static class ReviewQuizCardComparator implements Comparator<CardEntity> {
        @Override
        public int compare(CardEntity c1, CardEntity c2) {
            return cardReviewDueFactor(c2).compareTo(cardReviewDueFactor(c1));
        }
    }

}
