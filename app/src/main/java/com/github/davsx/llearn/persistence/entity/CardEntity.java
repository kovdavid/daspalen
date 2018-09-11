package com.github.davsx.llearn.persistence.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;
import com.github.davsx.llearn.LLearnConstants;

@Entity(
        tableName = "cards",
        indices = {
                @Index(value = {"type"})
        }
)
public class CardEntity {
    public static final Integer TYPE_INCOMPLETE = 0;
    public static final Integer TYPE_LEARN = 1;
    public static final Integer TYPE_REVIEW = 2;

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
    public Integer type = TYPE_INCOMPLETE;

    @ColumnInfo(name = "learn_score")
    public Integer learnScore = 0;

    @ColumnInfo(name = "learn_update_at")
    public Long learnUpdateAt;

    @ColumnInfo(name = "created_at")
    public Long createdAt;

    public CardEntity() {
    }

    public void handleCorrectLearnQuizAnswer() {
        this.learnScore = Math.max(this.learnScore + 1, LLearnConstants.MAX_CARD_LEARN_SCORE);
        this.learnUpdateAt = System.currentTimeMillis();
        if (this.learnScore >= LLearnConstants.MAX_CARD_LEARN_SCORE) {
            this.type = TYPE_REVIEW;
        }
    }

    @NonNull
    public String getBack() {
        return back;
    }

    public CardEntity setBack(@NonNull String back) {
        this.back = back;
        if (this.type.equals(TYPE_INCOMPLETE) && this.front.length() > 0 && this.back.length() > 0) {
            this.type = TYPE_LEARN;
        }
        return this;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public CardEntity setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    @NonNull
    public String getFront() {
        return front;
    }

    public CardEntity setFront(@NonNull String front) {
        this.front = front;
        if (this.type.equals(TYPE_INCOMPLETE) && this.front.length() > 0 && this.back.length() > 0) {
            this.type = TYPE_LEARN;
        }
        return this;
    }

    public Long getId() {
        return id;
    }

    public CardEntity setId(Long id) {
        this.id = id;
        return this;
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

    @NonNull
    public Integer getType() {
        return type;
    }

    public CardEntity setType(@NonNull Integer type) {
        this.type = type;
        return this;
    }
}

