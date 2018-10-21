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
    @ColumnInfo(name = "quiz_session")
    public Long quizSession = 0L;

    @NonNull
    @ColumnInfo(name = "quiz_type")
    public Integer type = 0;

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
        journal.quizSession = Long.valueOf(data[1]);
        journal.type = Integer.valueOf(data[2]);
        journal.cardId = Long.valueOf(data[3]);
        journal.answer = Integer.valueOf(data[4]);
        journal.timestamp = Long.valueOf(data[5]);

        return journal;
    }

    public String[] toCsvDataV1() {
        return new String[]{
                Long.toString(id),
                Long.toString(quizSession),
                Integer.toString(type),
                Long.toString(cardId),
                Integer.toString(answer),
                Long.toString(timestamp)
        };
    }

    public Long getId() {
        return id;
    }
}

