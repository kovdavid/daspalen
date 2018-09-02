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
    public static final Integer TYPE_LEARN= 0;
    public static final Integer TYPE_REVIEW = 1;

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
    public Integer type = TYPE_LEARN;

    @ColumnInfo(name = "back_word_count")
    public Integer backWordCount;

    @ColumnInfo(name = "back_length")
    public Integer backLength;

    @ColumnInfo(name = "learn_score")
    public Integer learnScore = 0;

    @ColumnInfo(name = "learn_update_at")
    public Long learnUpdateAt;

    @ColumnInfo(name = "created_at")
    public Long createdAt;

    public CardEntity() {}

    public Long getId() {
        return id;
    }

    public CardEntity setId(Long id) {
        this.id = id;
        return this;
    }

    @NonNull
    public String getFront() {
        return front;
    }

    public CardEntity setFront(@NonNull String front) {
        this.front = front;
        return this;
    }

    @NonNull
    public String getBack() {
        return back;
    }

    public CardEntity setBack(@NonNull String back) {
        this.back = back;
        this.backLength = back.length();
        this.backWordCount = back.split("\\s+").length;
        return this;
    }

    @NonNull
    public Integer getType() {
        return type;
    }

    public CardEntity setType(@NonNull Integer type) {
        this.type = type;
        return this;
    }

    public Integer getBackWordCount() {
        return backWordCount;
    }

    public Integer getBackLength() {
        return backLength;
    }

    public Integer getLearnScore() {
        return learnScore;
    }

    public CardEntity setLearnScore(Integer learnScore) {
        this.learnScore = learnScore;
        return this;
    }

    public Long getLearnUpdateAt() {
        return learnUpdateAt;
    }

    public CardEntity setLearnUpdateAt(Long learnUpdateAt) {
        this.learnUpdateAt = learnUpdateAt;
        return this;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public CardEntity setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
        return this;
    }
}

