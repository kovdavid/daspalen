package com.github.davsx.llearn.service.FileService;

import android.content.ContentResolver;
import android.net.Uri;

public interface CardImageService {
    void removeCardImages(Long cardId);

    void setCardImageFromTemp(Long cardId);

    String saveTempImage(ContentResolver contentResolver, Uri uri);

    String getCardImagePath(Long cardId);

    boolean isTempImage(String path);
}
