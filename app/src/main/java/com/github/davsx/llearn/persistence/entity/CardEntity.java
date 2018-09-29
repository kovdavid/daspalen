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
                @Index(value = {"type"}),
                @Index(value = {"front"}),
                @Index(value = {"back"}),
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

    public String[] toCsvDataV1() {
        String leanUpdateAtString = "0";
        if (learnUpdateAt != null) {
            leanUpdateAtString = Long.toString(learnUpdateAt);
        }
        return new String[]{
                Long.toString(id),
                front,
                back,
                Integer.toString(type),
                Integer.toString(learnScore),
                leanUpdateAtString,
                Long.toString(createdAt)
        };
    }

    public static CardEntity fromCsvDataV1(String[] data) {
        CardEntity card = new CardEntity();

        card.id = Long.valueOf(data[0]);
        card.front = data[1];
        card.back = data[2];
        card.type = Integer.valueOf(data[3]);
        card.learnScore = Integer.valueOf(data[4]);
        if (data[5].equals("0")) {
            card.learnUpdateAt = null;
        } else {
            card.learnUpdateAt = Long.valueOf(data[5]);
        }
        card.createdAt = Long.valueOf(data[6]);

        return card;
    }

    public void incrementLearnScore() {
        this.learnScore = Math.min(this.learnScore + 1, LLearnConstants.MAX_CARD_LEARN_SCORE);
        this.learnUpdateAt = System.currentTimeMillis();
        if (this.learnScore >= LLearnConstants.MAX_CARD_LEARN_SCORE) {
            this.type = TYPE_REVIEW;
        }
    }

    private Integer calculateType() {
        if (front.length() > 0 && back.length() > 0) {
            if (type.equals(TYPE_INCOMPLETE)) {
                return TYPE_LEARN;
            } else {
                return type;
            }
        } else {
            return TYPE_INCOMPLETE;
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

    public Integer getLearnScore() {
        return learnScore;
    }

    public CardEntity setLearnScore(Integer learnScore) {
        this.learnScore = learnScore;
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

