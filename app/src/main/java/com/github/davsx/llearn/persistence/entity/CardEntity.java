package com.github.davsx.llearn.persistence.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(
        tableName = "cards",
        indices = {
                @Index(value = {"front"}),
                @Index(value = {"back"}),
        }
)
public class CardEntity {
    @PrimaryKey
    @ColumnInfo(name = "id_card")
    public Long id;

    @ColumnInfo(name = "front")
    public String front;

    @ColumnInfo(name = "back")
    public String back;

    @ColumnInfo(name = "back_word_count")
    public Integer backWordCount;

    @ColumnInfo(name = "back_length")
    public Integer backLength;

    @ColumnInfo(name = "learn_score")
    public Integer learnScore = 0;

    @ColumnInfo(name = "created_timestamp")
    public Long createdTimestamp;

    @ColumnInfo(name = "last_review_timestamp")
    public Long lastReviewTimestamp;

    public CardEntity(Long id, String front, String back, Integer backWordCount, Integer backLength, Integer learnScore, Long createdTimestamp, Long lastReviewTimestamp) {
        this.id = id;
        this.front = front;
        this.back = back;
        this.backWordCount = backWordCount;
        this.backLength = backLength;
        this.learnScore = learnScore;
        this.createdTimestamp = createdTimestamp;
        this.lastReviewTimestamp = lastReviewTimestamp;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFront() {
        return front;
    }

    public void setFront(String front) {
        this.front = front;
    }

    public String getBack() {
        return back;
    }

    public void setBack(String back) {
        this.back = back;
    }

    public void updateBack(String back) {
        this.back = back;
        this.backLength = back.length();
        this.backWordCount = back.split("\\s+").length;
    }

    public Integer getBackWordCount() {
        return backWordCount;
    }

    public void setBackWordCount(Integer backWordCount) {
        this.backWordCount = backWordCount;
    }

    public Integer getBackLength() {
        return backLength;
    }

    public void setBackLength(Integer backLength) {
        this.backLength = backLength;
    }

    public Integer getLearnScore() {
        return learnScore;
    }

    public void setLearnScore(Integer learnScore) {
        this.learnScore = learnScore;
    }

    public Long getCreatedTimestamp() {
        return createdTimestamp;
    }

    public void setCreatedTimestamp(Long createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    public Long getLastReviewTimestamp() {
        return lastReviewTimestamp;
    }

    public void setLastReviewTimestamp(Long lastReviewTimestamp) {
        this.lastReviewTimestamp = lastReviewTimestamp;
    }
}

