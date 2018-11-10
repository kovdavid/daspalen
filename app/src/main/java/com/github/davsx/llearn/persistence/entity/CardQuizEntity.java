package com.github.davsx.llearn.persistence.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;
import com.github.davsx.llearn.LLearnConstants;
import com.opencsv.bean.CsvBindByPosition;

@Entity(
        tableName = "card_quiz",
        indices = {
                @Index(value = {"front_text"}),
                @Index(value = {"back_text"}),
                @Index(value = {"enabled"}),
        }
)
public class CardQuizEntity {

    @PrimaryKey
    @ColumnInfo(name = "id_card")
    @CsvBindByPosition(position = 0)
    public Long cardId;

    @NonNull
    @ColumnInfo(name = "quiz_type")
    @CsvBindByPosition(position = 1)
    public Integer quizType = LLearnConstants.CARD_TYPE_INCOMPLETE;

    @NonNull
    @ColumnInfo(name = "quiz_type_changes")
    @CsvBindByPosition(position = 2)
    public Integer quizTypeChanges = 0;

    @NonNull
    @ColumnInfo(name = "learn_score")
    @CsvBindByPosition(position = 3)
    public Integer learnScore = 0;

    @NonNull
    @ColumnInfo(name = "last_learn_quiz_at")
    @CsvBindByPosition(position = 4)
    public Long lastLearnQuizAt = System.currentTimeMillis();

    @NonNull
    @ColumnInfo(name = "last_review_at")
    @CsvBindByPosition(position = 5)
    public Long lastReviewAt = System.currentTimeMillis();

    @NonNull
    @ColumnInfo(name = "next_review_at")
    @CsvBindByPosition(position = 6)
    public Long nextReviewAt = System.currentTimeMillis();

    @NonNull
    @ColumnInfo(name = "review_interval_multiplier")
    @CsvBindByPosition(position = 7)
    public Double reviewIntervalMultiplier = 0.0;

    @NonNull
    @ColumnInfo(name = "bad_reviews")
    @CsvBindByPosition(position = 8)
    public Integer badReviews = 0;

    @NonNull
    @ColumnInfo(name = "good_reviews")
    @CsvBindByPosition(position = 9)
    public Integer goodReviews = 0;

    @NonNull
    @ColumnInfo(name = "local_version")
    @CsvBindByPosition(position = 10)
    public Integer localVersion = 0;

    @NonNull
    @ColumnInfo(name = "synced_version")
    @CsvBindByPosition(position = 11)
    public Integer syncedVersion = 0;

    @NonNull
    @ColumnInfo(name = "created_at")
    @CsvBindByPosition(position = 12)
    public Long createdAt = System.currentTimeMillis();

    @NonNull
    @ColumnInfo(name = "updated_at")
    @CsvBindByPosition(position = 13)
    public Long updatedAt = System.currentTimeMillis();

    public CardQuizEntity() {
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

    @NonNull
    public Double getReviewIntervalMultiplier() {
        return reviewIntervalMultiplier;
    }

    public CardQuizEntity setReviewIntervalMultiplier(@NonNull Double reviewIntervalMultiplier) {
        this.reviewIntervalMultiplier = reviewIntervalMultiplier;
        return this;
    }

    @NonNull
    public Integer getSyncedVersion() {
        return syncedVersion;
    }

    public CardQuizEntity setSyncedVersion(@NonNull Integer syncedVersion) {
        this.syncedVersion = syncedVersion;
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

