package com.github.davsx.llearn.model;

import com.github.davsx.llearn.LLearnConstants;
import com.github.davsx.llearn.persistence.entity.CardEntity;
import com.github.davsx.llearn.persistence.entity.CardNotificationEntity;
import com.github.davsx.llearn.persistence.entity.CardQuizEntity;
import com.google.gson.annotations.Expose;

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
                .setQuizType(LLearnConstants.CARD_TYPE_LEARN)
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
        cardEntity.setFrontText(newFrontText);
        cardEntity.setBackText(newBackText);
        cardEntity.setLocalVersion(cardEntity.getLocalVersion() + 1);
        cardEntity.setUpdatedAt(System.currentTimeMillis());
        cardEntityChanged = true;

        if (newFrontText.equals("") || newBackText.equals("")) {
            if (!cardQuizEntity.getQuizType().equals(LLearnConstants.CARD_TYPE_INCOMPLETE)) {
                cardQuizEntity.setQuizType(LLearnConstants.CARD_TYPE_INCOMPLETE);
            } else {
                if (cardQuizEntity.getQuizType().equals(LLearnConstants.CARD_TYPE_REVIEW)) {
                    cardQuizEntity.setLearnScore(1);
                } else {
                    cardQuizEntity.setLearnScore(0);
                }
                cardQuizEntity.setQuizType(LLearnConstants.CARD_TYPE_LEARN);
            }
            cardQuizEntity.setQuizTypeChanges(cardQuizEntity.getQuizTypeChanges() + 1);
            cardQuizEntity.setLocalVersion(cardQuizEntity.getLocalVersion() + 1);
            cardQuizEntity.setUpdatedAt(System.currentTimeMillis());
            cardQuizEntityChanged = true;
        }
    }

    public void updateImageHash(String imageHash) {
        if ((imageHash == null && cardEntity.getImageHash() != null)
                || (imageHash != null && cardEntity.getImageHash() == null)
                || (imageHash != null && !imageHash.equals(cardEntity.getImageHash()))) {
            cardEntity.setImageHash(imageHash);
            cardEntity.setLocalVersion(cardEntity.getLocalVersion() + 1);
            cardEntity.setUpdatedAt(System.currentTimeMillis());
            cardEntityChanged = true;
        }
    }

    public void processCorrectLearnAnswer() {
        if (!cardQuizEntity.getQuizType().equals(LLearnConstants.CARD_TYPE_LEARN)) return;

        cardQuizEntity.setLearnScore(cardQuizEntity.getLearnScore() + 1);
        cardQuizEntity.setLastLearnQuizAt(System.currentTimeMillis());
        cardQuizEntity.setLocalVersion(cardEntity.getLocalVersion() + 1);
        cardQuizEntity.setUpdatedAt(System.currentTimeMillis());

        if (cardQuizEntity.getLearnScore() >= LLearnConstants.MAX_CARD_LEARN_SCORE) {
            setQuizTypeReview();
        }

        cardQuizEntityChanged = true;
    }

    private void setQuizTypeReview() {
        cardQuizEntity.setQuizType(LLearnConstants.CARD_TYPE_REVIEW);
        cardQuizEntity.setQuizTypeChanges(cardQuizEntity.getQuizTypeChanges() + 1);
        cardQuizEntity.setReviewIntervalMultiplier(LLearnConstants.REVIEW_CARD_MIN_EASINESS_FACTOR);
        cardQuizEntity.setBadReviews(0);
        cardQuizEntity.setGoodReviews(0);
        cardQuizEntity.setLastLearnQuizAt(System.currentTimeMillis());
        cardQuizEntity.setNextReviewAt(System.currentTimeMillis() + LLearnConstants.ONE_DAY_MILLIS);
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
