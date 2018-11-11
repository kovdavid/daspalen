package com.github.davsx.llearn.persistence.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(
        tableName = "card",
        indices = {
                @Index(value = {"front_text"}),
                @Index(value = {"back_text"}),
                @Index(value = {"enabled"}),
        }
)
public class CardEntity {

    @PrimaryKey
    @ColumnInfo(name = "id_card")
    public Long cardId;

    @NonNull
    @ColumnInfo(name = "front_text")
    public String frontText = "";

    @NonNull
    @ColumnInfo(name = "back_text")
    public String backText = "";

    @ColumnInfo(name = "image_hash")
    public String imageHash = null;

    @NonNull
    @ColumnInfo(name = "enabled")
    public Boolean enabled = true;

    @NonNull
    @ColumnInfo(name = "local_version")
    public Integer localVersion = 0;

    @NonNull
    @ColumnInfo(name = "synced_version")
    public Integer syncedVersion = 0;

    @NonNull
    @ColumnInfo(name = "created_at")
    public Long createdAt = System.currentTimeMillis();

    @NonNull
    @ColumnInfo(name = "updated_at")
    public Long updatedAt = System.currentTimeMillis();

    public CardEntity() {
    }

    public CardEntity incrementLocalVersion() {
        localVersion++;
        return this;
    }

    @NonNull
    public String getBackText() {
        return backText;
    }

    public CardEntity setBackText(@NonNull String backText) {
        this.backText = backText;
        return this;
    }

    public Long getCardId() {
        return cardId;
    }

    public CardEntity setCardId(Long cardId) {
        this.cardId = cardId;
        return this;
    }

    @NonNull
    public Long getCreatedAt() {
        return createdAt;
    }

    public CardEntity setCreatedAt(@NonNull Long createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    @NonNull
    public Boolean getEnabled() {
        return enabled;
    }

    public CardEntity setEnabled(@NonNull Boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    @NonNull
    public String getFrontText() {
        return frontText;
    }

    public CardEntity setFrontText(@NonNull String frontText) {
        this.frontText = frontText;
        return this;
    }

    public String getImageHash() {
        return imageHash;
    }

    public CardEntity setImageHash(String imageHash) {
        this.imageHash = imageHash;
        return this;
    }

    @NonNull
    public Integer getLocalVersion() {
        return localVersion;
    }

    public CardEntity setLocalVersion(@NonNull Integer localVersion) {
        this.localVersion = localVersion;
        return this;
    }

    @NonNull
    public Integer getSyncedVersion() {
        return syncedVersion;
    }

    public CardEntity setSyncedVersion(@NonNull Integer syncedVersion) {
        this.syncedVersion = syncedVersion;
        return this;
    }

    @NonNull
    public Long getUpdatedAt() {
        return updatedAt;
    }

    public CardEntity setUpdatedAt(@NonNull Long updatedAt) {
        this.updatedAt = updatedAt;
        return this;
    }

}

