package com.github.davsx.llearn.persistence.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;
import com.github.davsx.llearn.LLearnConstants;

import java.util.Random;

@Entity(
        tableName = "cards",
        indices = {
                @Index(value = {"type"}),
                @Index(value = {"front"}),
                @Index(value = {"back"}),
                @Index(value = {"next_review_at"}),
        }
)
public class CardEntity {

    @PrimaryKey
    @ColumnInfo(name = "id_card")
    public Long id;

    @NonNull
    @ColumnInfo(name = "front")
    public String front = "";

    @NonNull
    @ColumnInfo(name = "back")
    public String back = "";

    @NonNull
    @ColumnInfo(name = "type")
    public Integer type = LLearnConstants.CARD_TYPE_INCOMPLETE;

    @ColumnInfo(name = "learn_score")
    public Integer learnScore = 0;

    @ColumnInfo(name = "learn_update_at")
    public Long learnUpdateAt = 0L;

    @ColumnInfo(name = "created_at")
    public Long createdAt;

    @ColumnInfo(name = "last_review_at")
    public Long lastReviewAt = 0L;

    @ColumnInfo(name = "next_review_at")
    public Long nextReviewAt = 0L;

    @ColumnInfo(name = "easiness_factor")
    public Double easinessFactor = 0.0;

    @ColumnInfo(name = "bad_review_answers")
    public Integer badReviewAnswers = 0;

    @ColumnInfo(name = "good_review_answers")
    public Integer goodReviewAnswers = 0;

    public CardEntity() {
    }

    public static CardEntity fromCsvDataV1(String[] data) {
        CardEntity card = new CardEntity();

        card.id = Long.valueOf(data[0]);
        card.front = data[1];
        card.back = data[2];
        card.type = Integer.valueOf(data[3]);
        card.learnScore = Integer.valueOf(data[4]);
        card.learnUpdateAt = Long.valueOf(data[5]);
        card.createdAt = Long.valueOf(data[6]);
        card.lastReviewAt = Long.valueOf(data[7]);
        card.nextReviewAt = Long.valueOf(data[8]);
        card.easinessFactor = Double.valueOf(data[9]);
        card.badReviewAnswers = Integer.valueOf(data[10]);
        card.goodReviewAnswers = Integer.valueOf(data[11]);

        return card;
    }

    public String[] toCsvDataV1() {
        return new String[]{
                Long.toString(id),
                front,
                back,
                Integer.toString(type),
                Integer.toString(learnScore),
                Long.toString(learnUpdateAt),
                Long.toString(createdAt),
                Long.toString(lastReviewAt),
                Long.toString(nextReviewAt),
                Double.toString(easinessFactor),
                Integer.toString(badReviewAnswers),
                Integer.toString(goodReviewAnswers)
        };
    }

    public void processCorrectLearnAnswer() {
        if (!type.equals(LLearnConstants.CARD_TYPE_LEARN)) return;

        this.learnScore = Math.min(this.learnScore + 1, LLearnConstants.MAX_CARD_LEARN_SCORE);
        this.learnUpdateAt = System.currentTimeMillis();
        if (this.learnScore >= LLearnConstants.MAX_CARD_LEARN_SCORE) {
            this.type = LLearnConstants.CARD_TYPE_REVIEW;
            this.easinessFactor = LLearnConstants.REVIEW_CARD_MIN_EASINESS_FACTOR;
            this.badReviewAnswers = 0;
            this.goodReviewAnswers = 0;
            this.lastReviewAt = System.currentTimeMillis();
            this.nextReviewAt = System.currentTimeMillis() + LLearnConstants.ONE_DAY_MILLIS;
        }
    }

    public void processGoodReviewAnswer() {
        this.goodReviewAnswers++;

        this.easinessFactor = changeEasinessFactoryBy(0.03);

        long currentInterval = this.nextReviewAt - this.lastReviewAt;
        Random rng = new Random(System.currentTimeMillis());

        long nextInterval = (long) (
                (currentInterval * this.easinessFactor) * (0.95 + rng.nextDouble() / 10) // ±5%
        );

        if (goodReviewAnswers < 10) {
            while (nextInterval > (goodReviewAnswers + 1) * LLearnConstants.ONE_DAY_MILLIS) {
                nextInterval -= 1;
            }
        }

        nextInterval = limitReviewInterval(nextInterval);

        this.lastReviewAt = System.currentTimeMillis();
        this.nextReviewAt = System.currentTimeMillis() + nextInterval;
    }

    public void processOkReviewAnswer() {
        this.easinessFactor = changeEasinessFactoryBy(-0.07);

        long currentInterval = this.nextReviewAt - this.lastReviewAt;
        Random rng = new Random(System.currentTimeMillis());

        long nextInterval = (long) (
                (currentInterval / 2) * (0.95 + rng.nextDouble() / 10) // ±5%
        );

        while (nextInterval > 7 * LLearnConstants.ONE_DAY_MILLIS) {
            nextInterval -= 1;
        }

        nextInterval = limitReviewInterval(nextInterval);

        this.lastReviewAt = System.currentTimeMillis();
        this.nextReviewAt = System.currentTimeMillis() + nextInterval;
    }

    public void processBadReviewAnswer() {
        this.easinessFactor = changeEasinessFactoryBy(-0.20);
        this.goodReviewAnswers = 0;
        this.badReviewAnswers++;

        if (badReviewAnswers >= LLearnConstants.REVIEW_CARD_MAX_BAD_ANSWERS) {
            this.type = LLearnConstants.CARD_TYPE_LEARN;
            this.learnScore = 0;
            this.learnUpdateAt = System.currentTimeMillis();
            return;
        }

        this.lastReviewAt = System.currentTimeMillis();
        this.nextReviewAt = System.currentTimeMillis() + LLearnConstants.ONE_DAY_MILLIS;
    }

    private double changeEasinessFactoryBy(double change) {
        return Math.min(
                LLearnConstants.REVIEW_CARD_MAX_EASINESS_FACTOR,
                Math.max(
                        LLearnConstants.REVIEW_CARD_MIN_EASINESS_FACTOR,
                        this.easinessFactor + change
                )
        );
    }

    private long limitReviewInterval(long interval) {
        if (interval < LLearnConstants.ONE_DAY_MILLIS) return LLearnConstants.ONE_DAY_MILLIS;
        if (interval > 60 * LLearnConstants.ONE_DAY_MILLIS)
            return 60 * LLearnConstants.ONE_DAY_MILLIS;
        return interval;
    }

    private Integer calculateType() {
        if (front.length() > 0 && back.length() > 0) {
            if (type.equals(LLearnConstants.CARD_TYPE_INCOMPLETE)) {
                return LLearnConstants.CARD_TYPE_LEARN;
            } else {
                return type;
            }
        } else {
            return LLearnConstants.CARD_TYPE_INCOMPLETE;
        }
    }

    public CardEntity setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    @NonNull
    public String getBack() {
        return back;
    }

    public CardEntity setBack(@NonNull String back) {
        this.back = back;
        this.type = calculateType();
        return this;
    }

    @NonNull
    public String getFront() {
        return front;
    }

    public CardEntity setFront(@NonNull String front) {
        this.front = front;
        this.type = calculateType();
        return this;
    }

    public Long getId() {
        return id;
    }

    public CardEntity setId(Long id) {
        this.id = id;
        return this;
    }

    public Long getLastReviewAt() {
        return lastReviewAt;
    }

    public Integer getLearnScore() {
        return learnScore;
    }

    public CardEntity setLearnScore(Integer learnScore) {
        this.learnScore = learnScore;
        return this;
    }

    public Long getNextReviewAt() {
        return nextReviewAt;
    }

    @NonNull
    public Integer getType() {
        return type;
    }

    public CardEntity setType(@NonNull Integer type) {
        this.type = type;
        return this;
    }
}

