package com.github.davsx.llearn.service.CardImage;

import android.content.ContentResolver;
import android.net.Uri;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.zip.ZipInputStream;

public interface CardImageService {
    void removeCardImages(Long cardId);

    void deleteAllImages();

    void saveImageFromStream(String name, ZipInputStream zipInputStream) throws IOException;

    void setCardImageFromTemp(Long cardId);

    String saveTempImage(ContentResolver contentResolver, Uri uri);

    String getCardImagePath(Long cardId);

    boolean isTempImage(String path);

    List<File> getAllFiles();
}
