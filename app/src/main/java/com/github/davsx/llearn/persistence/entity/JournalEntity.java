package com.github.davsx.llearn.persistence.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(
        tableName = "journal",
        indices = {
                @Index(value = {"id_card"}),
        }
)
public class JournalEntity {

    @PrimaryKey
    @ColumnInfo(name = "id_journal")
    public Long id;

    @NonNull
    @ColumnInfo(name = "card_type")
    public Integer cardType = 0;

    @NonNull
    @ColumnInfo(name = "id_card")
    public Long cardId = 0L;

    @NonNull
    @ColumnInfo(name = "answer")
    public Integer answer = 0;

    @NonNull
    @ColumnInfo(name = "timestamp")
    public Long timestamp = System.currentTimeMillis();

    public JournalEntity() {
    }

    public static JournalEntity fromCsvDataV1(String[] data) {
        JournalEntity journal = new JournalEntity();

        journal.id = Long.valueOf(data[0]);
        journal.cardId = Long.valueOf(data[1]);
        journal.cardType = Integer.valueOf(data[2]);
        journal.answer = Integer.valueOf(data[3]);
        journal.timestamp = Long.valueOf(data[4]);

        return journal;
    }

    public String[] toCsvDataV1() {
        return new String[]{
                Long.toString(id),
                Long.toString(cardId),
                Integer.toString(cardType),
                Integer.toString(answer),
                Long.toString(timestamp)
        };
    }

    public void setCardId(@NonNull Long cardId) {
        this.cardId = cardId;
    }

    @NonNull
    public Integer getAnswer() {
        return answer;
    }

    public void setAnswer(@NonNull Integer answer) {
        this.answer = answer;
    }

    @NonNull
    public Integer getCardType() {
        return cardType;
    }

    public void setCardType(@NonNull Integer cardType) {
        this.cardType = cardType;
    }

    public void setTimestamp(@NonNull Long timestamp) {
        this.timestamp = timestamp;
    }

    @NonNull
    public Long getTimestamp() {
        return timestamp;
    }

    public Long getId() {
        return id;
    }
}

