package com.github.davsx.daspalen.model;

import com.github.davsx.daspalen.DaspalenConstants;
import com.github.davsx.daspalen.persistence.entity.CardEntity;
import com.github.davsx.daspalen.persistence.entity.CardNotificationEntity;
import com.github.davsx.daspalen.persistence.entity.CardQuizEntity;
import com.google.gson.annotations.Expose;

import java.util.Random;

public class Card {

    private boolean cardEntityChanged = false;
    private boolean cardQuizEntityChanged = false;
    private boolean cardNotificationEntityChanged = false;

    @Expose
    private CardEntity cardEntity;
    @Expose
    private CardQuizEntity cardQuizEntity;
    @Expose
    private CardNotificationEntity cardNotificationEntity;

    public Card(CardEntity cardEntity, CardQuizEntity cardQuizEntity, CardNotificationEntity cardNotificationEntity) {
        this.cardEntity = cardEntity;
        this.cardQuizEntity = cardQuizEntity;
        this.cardNotificationEntity = cardNotificationEntity;
    }

    public static Card createNew(String frontText, String backText) {
        CardEntity cardEntity = new CardEntity()
                .setFrontText(frontText)
                .setBackText(backText)
                .setCreatedAt(System.currentTimeMillis())
                .setUpdatedAt(System.currentTimeMillis())
                .setLocalVersion(1)
                .setSyncedVersion(0);
        CardQuizEntity cardQuizEntity = new CardQuizEntity()
                .setQuizType(DaspalenConstants.CARD_TYPE_LEARN)
                .setLearnScore(0)
                .setCreatedAt(System.currentTimeMillis())
                .setUpdatedAt(System.currentTimeMillis())
                .setLocalVersion(1)
                .setSyncedVersion(0);
        CardNotificationEntity cardNotificationEntity = new CardNotificationEntity()
                .setCreatedAt(System.currentTimeMillis())
                .setUpdatedAt(System.currentTimeMillis())
                .setLocalVersion(1)
                .setSyncedVersion(0);

        Card c = new Card(cardEntity, cardQuizEntity, cardNotificationEntity);

        c.cardEntityChanged = true;
        c.cardQuizEntityChanged = true;
        c.cardNotificationEntityChanged = true;

        return c;
    }

    public void updateTexts(String newFrontText, String newBackText) {
        cardEntity.setFrontText(newFrontText)
                .setBackText(newBackText)
                .incrementLocalVersion()
                .setUpdatedAt(System.currentTimeMillis());
        cardEntityChanged = true;

        if (newFrontText.equals("") || newBackText.equals("")) {
            if (!cardQuizEntity.getQuizType().equals(DaspalenConstants.CARD_TYPE_INCOMPLETE)) {
                cardQuizEntity.setQuizType(DaspalenConstants.CARD_TYPE_INCOMPLETE);
            } else {
                if (cardQuizEntity.getQuizType().equals(DaspalenConstants.CARD_TYPE_REVIEW)) {
                    cardQuizEntity.setLearnScore(1);
                } else {
                    cardQuizEntity.setLearnScore(0);
                }
                cardQuizEntity.setQuizType(DaspalenConstants.CARD_TYPE_LEARN);
            }
            cardQuizEntity.incrementQuizTypeChanges()
                    .incrementLocalVersion()
                    .setUpdatedAt(System.currentTimeMillis());
            cardQuizEntityChanged = true;
        }
    }

    public void updateImageHash(String imageHash) {
        if ((imageHash == null && cardEntity.getImageHash() != null)
                || (imageHash != null && cardEntity.getImageHash() == null)
                || (imageHash != null && !imageHash.equals(cardEntity.getImageHash()))) {
            cardEntity.setImageHash(imageHash)
                    .incrementLocalVersion()
                    .setUpdatedAt(System.currentTimeMillis());
            cardEntityChanged = true;
        }
    }

    public void processCorrectLearnAnswer() {
        if (!cardQuizEntity.getQuizType().equals(DaspalenConstants.CARD_TYPE_LEARN)) return;

        cardQuizEntity.incrementLearnScore()
                .setLastLearnQuizAt(System.currentTimeMillis())
                .incrementLocalVersion()
                .setUpdatedAt(System.currentTimeMillis());

        if (cardQuizEntity.getLearnScore() >= DaspalenConstants.MAX_CARD_LEARN_SCORE) {
            setQuizTypeReview();
        }

        cardQuizEntityChanged = true;
    }

    private void setQuizTypeReview() {
        cardQuizEntity.setQuizType(DaspalenConstants.CARD_TYPE_REVIEW)
                .incrementQuizTypeChanges()
                .setReviewIntervalMultiplier(DaspalenConstants.REVIEW_CARD_MIN_EASINESS_FACTOR)
                .setBadReviews(0)
                .setGoodReviews(0)
                .setLastReviewAt(System.currentTimeMillis())
                .setNextReviewAt(System.currentTimeMillis() + DaspalenConstants.ONE_DAY_MILLIS);
    }

    public void processGoodReviewAnswer() {
        double multiplier = changeReviewIntervalMultiplierBy(0.05);

        long nextInterval = getNextInterval(multiplier);

        int goodReviews = cardQuizEntity.getGoodReviews();

        if (goodReviews < 10) {
            while (nextInterval > (goodReviews + 1) * DaspalenConstants.ONE_DAY_MILLIS) {
                nextInterval -= DaspalenConstants.ONE_DAY_MILLIS;
            }
        }

        nextInterval = limitReviewInterval(nextInterval);

        cardQuizEntity.incrementGoodReviews()
                .setReviewIntervalMultiplier(multiplier)
                .setLastReviewAt(System.currentTimeMillis())
                .setNextReviewAt(System.currentTimeMillis() + nextInterval)
                .incrementLocalVersion()
                .setUpdatedAt(System.currentTimeMillis());

        cardQuizEntityChanged = true;
    }

    public void processOkReviewAnswer() {
        double multiplier = changeReviewIntervalMultiplierBy(-0.07);

        long nextInterval = getNextInterval(multiplier);

        while (nextInterval > 7 * DaspalenConstants.ONE_DAY_MILLIS) {
            nextInterval -= DaspalenConstants.ONE_DAY_MILLIS;
        }

        nextInterval = limitReviewInterval(nextInterval);

        cardQuizEntity.setReviewIntervalMultiplier(multiplier)
                .setLastReviewAt(System.currentTimeMillis())
                .setNextReviewAt(System.currentTimeMillis() + nextInterval)
                .incrementLocalVersion()
                .setUpdatedAt(System.currentTimeMillis());

        cardQuizEntityChanged = true;
    }

    public void processBadReviewAnswer() {
        double multiplier = changeReviewIntervalMultiplierBy(-0.20);

        long nextInterval = getNextInterval(multiplier);

        int badReviews = cardQuizEntity.getBadReviews();
        if (badReviews + 1 >= DaspalenConstants.REVIEW_CARD_MAX_BAD_ANSWERS) {
            cardQuizEntity.incrementBadReviews()
                    .incrementQuizTypeChanges()
                    .setQuizType(DaspalenConstants.CARD_TYPE_LEARN)
                    .setLearnScore(1)
                    .incrementLocalVersion()
                    .setUpdatedAt(System.currentTimeMillis());
            cardQuizEntityChanged = true;
            return;
        }

        nextInterval = limitReviewInterval(nextInterval);

        cardQuizEntity.incrementBadReviews()
                .setReviewIntervalMultiplier(multiplier)
                .setLastReviewAt(System.currentTimeMillis())
                .setNextReviewAt(System.currentTimeMillis() + nextInterval)
                .incrementLocalVersion()
                .setUpdatedAt(System.currentTimeMillis());

        cardQuizEntityChanged = true;
    }

    private long getNextInterval(Double multiplier) {
        Random rng = new Random(System.currentTimeMillis());

        return (long) (
                (getCurrentReviewInterval() * multiplier) * (0.95 + rng.nextDouble() / 10) // ±5%
        );
    }

    private long limitReviewInterval(long interval) {
        if (interval < DaspalenConstants.ONE_DAY_MILLIS) {
            return DaspalenConstants.ONE_DAY_MILLIS;
        }
        if (interval > 60 * DaspalenConstants.ONE_DAY_MILLIS) {
            return 60 * DaspalenConstants.ONE_DAY_MILLIS;
        }
        return interval;
    }

    private double changeReviewIntervalMultiplierBy(double change) {
        double multiplier = Math.min(
                DaspalenConstants.REVIEW_CARD_MAX_EASINESS_FACTOR,
                Math.max(
                        DaspalenConstants.REVIEW_CARD_MIN_EASINESS_FACTOR,
                        cardQuizEntity.getReviewIntervalMultiplier() + change
                )
        );
        cardQuizEntity.setReviewIntervalMultiplier(multiplier);
        return multiplier;
    }

    public String getBackText() {
        return cardEntity.getBackText();
    }

    public CardEntity getCardEntity() {
        return cardEntity;
    }

    public long getCardId() {
        return cardEntity.getCardId();
    }

    public void setCardId(long cardId) {
        if (cardEntity.getCardId() > 0) {
            throw new AssertionError("ID already set for Card");
        }

        cardEntity.setCardId(cardId);
        cardQuizEntity.setCardId(cardId);
        cardNotificationEntity.setCardId(cardId);
    }

    public CardNotificationEntity getCardNotificationEntity() {
        return cardNotificationEntity;
    }

    public CardQuizEntity getCardQuizEntity() {
        return cardQuizEntity;
    }

    private long getCurrentReviewInterval() {
        return cardQuizEntity.getNextReviewAt() - cardQuizEntity.getLastReviewAt();
    }

    public boolean getEnabled() {
        return cardEntity.getEnabled();
    }

    public void setEnabled(boolean enabled) {
        cardEntity.setEnabled(enabled);
        cardEntityChanged = true;
    }

    public String getFrontText() {
        return cardEntity.getFrontText();
    }

    public long getLastReviewAt() {
        return cardQuizEntity.getLastReviewAt();
    }

    public int getLearnScore() {
        return cardQuizEntity.getLearnScore();
    }

    public long getNextReviewAt() {
        return cardQuizEntity.getNextReviewAt();
    }

    public boolean isCardEntityChanged() {
        return cardEntityChanged;
    }

    public boolean isCardNotificationEntityChanged() {
        return cardNotificationEntityChanged;
    }

    public boolean isCardQuizEntityChanged() {
        return cardQuizEntityChanged;
    }
}
