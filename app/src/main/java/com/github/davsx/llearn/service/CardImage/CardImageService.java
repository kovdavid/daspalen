package com.github.davsx.llearn.service.CardImage;

import android.content.ContentResolver;
import android.net.Uri;

import java.io.File;
import java.util.List;

public interface CardImageService {
    void removeCardImages(Long cardId);

    void setCardImageFromTemp(Long cardId);

    String saveTempImage(ContentResolver contentResolver, Uri uri);

    String getCardImagePath(Long cardId);

    boolean isTempImage(String path);

    List<File> getAllFiles();
}
