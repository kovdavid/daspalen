package com.github.davsx.llearn.service.CardImage;

import android.content.ContentResolver;
import android.net.Uri;

import java.io.*;
import java.math.BigInteger;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.ZipInputStream;

public class CardImageService {

    private File imageDir;

    public CardImageService(File imageDir) {
        this.imageDir = imageDir;
    }

    public void removeCardImages(Long cardId) {
        File f = getCardImageFile(Long.toString(cardId));
        if (f.exists()) {
            f.delete();
        }
    }

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

    public String getCardImagePath(Long cardId) {
        File f = getCardImageFile(Long.toString(cardId));
        if (f.exists()) {
            return f.getAbsolutePath();
        }
        return null;
    }

    public String getImageHash(String path) {
        if (path == null) return null;

        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            return null;
        }

        InputStream is;
        try {
            is = new FileInputStream(new File(path));
        } catch (FileNotFoundException e) {
            return null;
        }

        byte[] buffer = new byte[8192];
        int read;
        try {
            while ((read = is.read(buffer)) > 0) {
                digest.update(buffer, 0, read);
            }
            byte[] md5sum = digest.digest();
            BigInteger bigInt = new BigInteger(1, md5sum);
            String output = bigInt.toString(16);
            output = String.format("%32s", output).replace(' ', '0');
            return output;
        } catch (IOException e) {
            return null;
        } finally {
            try {
                is.close();
            } catch (IOException ignored) {
            }
        }
    }

    public boolean isTempImage(String path) {
        if (path == null) {
            return false;
        }
        return getTempFile().getAbsolutePath().equals(path);
    }

    public void wipeData() {
        File[] files = imageDir.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.isFile();
            }
        });

        for (File file : files) {
            file.delete();
        }
    }

    public void saveImageFromStream(String name, ZipInputStream zipInputStream) throws IOException {
        File cardImageFile = getCardImageFile(name);
        FileOutputStream fOut = new FileOutputStream(cardImageFile);
        BufferedOutputStream out = new BufferedOutputStream(fOut);
        byte[] buffer = new byte[8096];
        int length;
        while ((length = zipInputStream.read(buffer)) != -1) {
            out.write(buffer, 0, length);
        }
        out.close();
        fOut.close();
    }

    private File getCardImageFile(String fileName) {
        return new File(imageDir + File.separator + fileName);
    }

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

    public List<File> getAllFiles() {
        File[] files = imageDir.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.isFile();
            }
        });

        return new ArrayList<>(Arrays.asList(files));
    }

    private File getTempFile() {
        return getCardImageFile("tmpImage");
    }

}
