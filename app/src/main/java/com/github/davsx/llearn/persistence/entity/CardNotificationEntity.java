package com.github.davsx.llearn.persistence.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(
        tableName = "card_notification",
        indices = {
                @Index(value = {"last_notification_at", "enabled"}),
        }
)
public class CardNotificationEntity {

    @PrimaryKey
    @ColumnInfo(name = "id_card")
    public Long cardId;

    @NonNull
    @ColumnInfo(name = "last_notification_at")
    public Long lastNotificationAt = System.currentTimeMillis();

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

    public CardNotificationEntity() {
    }

    public Long getCardId() {
        return cardId;
    }

    public CardNotificationEntity setCardId(Long cardId) {
        this.cardId = cardId;
        return this;
    }

    @NonNull
    public Long getCreatedAt() {
        return createdAt;
    }

    public CardNotificationEntity setCreatedAt(@NonNull Long createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    @NonNull
    public Boolean getEnabled() {
        return enabled;
    }

    public CardNotificationEntity setEnabled(@NonNull Boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    @NonNull
    public Long getLastNotificationAt() {
        return lastNotificationAt;
    }

    public CardNotificationEntity setLastNotificationAt(@NonNull Long lastNotificationAt) {
        this.lastNotificationAt = lastNotificationAt;
        return this;
    }

    @NonNull
    public Integer getLocalVersion() {
        return localVersion;
    }

    public CardNotificationEntity setLocalVersion(@NonNull Integer localVersion) {
        this.localVersion = localVersion;
        return this;
    }

    @NonNull
    public Integer getSyncedVersion() {
        return syncedVersion;
    }

    public CardNotificationEntity setSyncedVersion(@NonNull Integer syncedVersion) {
        this.syncedVersion = syncedVersion;
        return this;
    }

    @NonNull
    public Long getUpdatedAt() {
        return updatedAt;
    }

    public CardNotificationEntity setUpdatedAt(@NonNull Long updatedAt) {
        this.updatedAt = updatedAt;
        return this;
    }
}

