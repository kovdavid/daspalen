package com.github.davsx.llearn.service.FileService;

import android.content.ContentResolver;
import android.net.Uri;

import java.io.*;
import java.nio.channels.FileChannel;

public class CardImageServiceImpl implements CardImageService {

    private File imageDir;

    public CardImageServiceImpl(File imageDir) {
        this.imageDir = imageDir;
    }

    @Override
    public void removeCardImages(Long cardId) {
        File f = getCardImageFile(Long.toString(cardId));
        if (f.exists()) {
            f.delete();
        }
    }

    @Override
    public void setCardImageFromTemp(Long cardId) {
        File tempFile = getTempFile();
        File imageFile = getCardImageFile(Long.toString(cardId));
        FileChannel outChannel;
        FileChannel inChannel;
        if (tempFile.exists()) {
            try {
                outChannel = new FileOutputStream(imageFile).getChannel();
                inChannel = new FileInputStream(tempFile).getChannel();
                inChannel.transferTo(0, inChannel.size(), outChannel);
                inChannel.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public String saveTempImage(ContentResolver contentResolver, Uri uri) {
        try {
            File outFile = getTempFile();
            InputStream inputStream = contentResolver.openInputStream(uri);
            OutputStream outputStream = new FileOutputStream(outFile);
            byte[] buf = new byte[4048];
            int len;
            while ((len = inputStream.read(buf)) > 0) {
                outputStream.write(buf, 0, len);
            }
            outputStream.close();
            inputStream.close();

            return outFile.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String getCardImagePath(Long cardId) {
        File f = getCardImageFile(Long.toString(cardId));
        if (f.exists()) {
            return f.getAbsolutePath();
        }
        return null;
    }

    private File getCardImageFile(String fileName) {
        return new File(imageDir + File.separator + fileName);
    }

    private File getTempFile() {
        return getCardImageFile("tmpImage");
    }

}
