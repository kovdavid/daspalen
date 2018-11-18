package com.github.davsx.daspalen.persistence.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;
import com.github.davsx.daspalen.DaspalenConstants;

@Entity(
        tableName = "card_quiz",
        indices = {
                @Index(value = {"quiz_type"}),
                @Index(value = {"next_review_at"}),
        }
)
public class CardQuizEntity {

    @PrimaryKey
    @ColumnInfo(name = "id_card")
    public Long cardId;

    @NonNull
    @ColumnInfo(name = "quiz_type")
    public Integer quizType = DaspalenConstants.CARD_TYPE_INCOMPLETE;

    @NonNull
    @ColumnInfo(name = "quiz_type_changes")
    public Integer quizTypeChanges = 0;

    @NonNull
    @ColumnInfo(name = "learn_score")
    public Integer learnScore = 0;

    @NonNull
    @ColumnInfo(name = "last_learn_quiz_at")
    public Long lastLearnQuizAt = System.currentTimeMillis();

    @NonNull
    @ColumnInfo(name = "last_review_at")
    public Long lastReviewAt = System.currentTimeMillis();

    @NonNull
    @ColumnInfo(name = "next_review_at")
    public Long nextReviewAt = System.currentTimeMillis();

    @NonNull
    @ColumnInfo(name = "review_interval_multiplier")
    public Double reviewIntervalMultiplier = 0.0;

    @NonNull
    @ColumnInfo(name = "bad_reviews")
    public Integer badReviews = 0;

    @NonNull
    @ColumnInfo(name = "good_reviews")
    public Integer goodReviews = 0;

    @NonNull
    @ColumnInfo(name = "local_version")
    public Integer localVersion = 0;

    @NonNull
    @ColumnInfo(name = "server_version")
    public Integer serverVersion = 0;

    @NonNull
    @ColumnInfo(name = "created_at")
    public Long createdAt = System.currentTimeMillis();

    @NonNull
    @ColumnInfo(name = "updated_at")
    public Long updatedAt = System.currentTimeMillis();

    public CardQuizEntity() {
    }

    public CardQuizEntity incrementGoodReviews() {
        goodReviews++;
        return this;
    }

    public CardQuizEntity incrementBadReviews() {
        badReviews++;
        return this;
    }

    public CardQuizEntity incrementLocalVersion() {
        localVersion++;
        return this;
    }

    public CardQuizEntity incrementLearnScore() {
        learnScore++;
        return this;
    }

    public CardQuizEntity incrementQuizTypeChanges() {
        quizTypeChanges++;
        return this;
    }

    @NonNull
    public Integer getBadReviews() {
        return badReviews;
    }

    public CardQuizEntity setBadReviews(@NonNull Integer badReviews) {
        this.badReviews = badReviews;
        return this;
    }

    public Long getCardId() {
        return cardId;
    }

    public CardQuizEntity setCardId(Long cardId) {
        this.cardId = cardId;
        return this;
    }

    @NonNull
    public Long getCreatedAt() {
        return createdAt;
    }

    public CardQuizEntity setCreatedAt(@NonNull Long createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    @NonNull
    public Integer getGoodReviews() {
        return goodReviews;
    }

    public CardQuizEntity setGoodReviews(@NonNull Integer goodReviews) {
        this.goodReviews = goodReviews;
        return this;
    }

    @NonNull
    public Long getLastLearnQuizAt() {
        return lastLearnQuizAt;
    }

    public CardQuizEntity setLastLearnQuizAt(@NonNull Long lastLearnQuizAt) {
        this.lastLearnQuizAt = lastLearnQuizAt;
        return this;
    }

    @NonNull
    public Long getLastReviewAt() {
        return lastReviewAt;
    }

    public CardQuizEntity setLastReviewAt(@NonNull Long lastReviewAt) {
        this.lastReviewAt = lastReviewAt;
        return this;
    }

    @NonNull
    public Integer getLearnScore() {
        return learnScore;
    }

    public CardQuizEntity setLearnScore(@NonNull Integer learnScore) {
        this.learnScore = learnScore;
        return this;
    }

    @NonNull
    public Integer getLocalVersion() {
        return localVersion;
    }

    public CardQuizEntity setLocalVersion(@NonNull Integer localVersion) {
        this.localVersion = localVersion;
        return this;
    }

    @NonNull
    public Long getNextReviewAt() {
        return nextReviewAt;
    }

    public CardQuizEntity setNextReviewAt(@NonNull Long nextReviewAt) {
        this.nextReviewAt = nextReviewAt;
        return this;
    }

    @NonNull
    public Integer getQuizType() {
        return quizType;
    }

    public CardQuizEntity setQuizType(@NonNull Integer quizType) {
        this.quizType = quizType;
        return this;
    }

    @NonNull
    public Integer getQuizTypeChanges() {
        return quizTypeChanges;
    }

    public CardQuizEntity setQuizTypeChanges(@NonNull Integer quizTypeChanges) {
        this.quizTypeChanges = quizTypeChanges;
        return this;
    }

    public String getQuizTypeString() {
        if (quizType.equals(DaspalenConstants.CARD_TYPE_INCOMPLETE)) {
            return "INCOMPLETE";
        } else if (quizType.equals(DaspalenConstants.CARD_TYPE_LEARN)) {
            return "LEARN";
        } else if (quizType.equals(DaspalenConstants.CARD_TYPE_REVIEW)) {
            return "REVIEW";
        } else {
            return "";
        }
    }

    @NonNull
    public Double getReviewIntervalMultiplier() {
        return reviewIntervalMultiplier;
    }

    public CardQuizEntity setReviewIntervalMultiplier(@NonNull Double reviewIntervalMultiplier) {
        this.reviewIntervalMultiplier = reviewIntervalMultiplier;
        return this;
    }

    @NonNull
    public Integer getServerVersion() {
        return serverVersion;
    }

    public CardQuizEntity setServerVersion(@NonNull Integer serverVersion) {
        this.serverVersion = serverVersion;
        return this;
    }

    @NonNull
    public Long getUpdatedAt() {
        return updatedAt;
    }

    public CardQuizEntity setUpdatedAt(@NonNull Long updatedAt) {
        this.updatedAt = updatedAt;
        return this;
    }
}

