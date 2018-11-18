package com.github.davsx.daspalen.service.BackupCreate;

import com.github.davsx.daspalen.DaspalenConstants;
import com.github.davsx.daspalen.model.Card;
import com.github.davsx.daspalen.persistence.repository.DaspalenRepository;
import com.github.davsx.daspalen.service.CardImage.CardImageService;
import com.github.davsx.daspalen.service.Settings.SettingsService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class BackupCreateService {

    private StringWriter jsonStringWriter;
    private ZipOutputStream zipOutputStream;
    private Gson gson;

    private DaspalenRepository repository;
    private CardImageService cardImageService;
    private SettingsService settingsService;

    private int maxProgress = 0;
    private int currentProgress = 0;
    private String status;
    private Long maxCardId;
    private List<File> imageFiles;

    private BackupStatus backupStatus = BackupStatus.BACKUP_NOT_RUNNING;

    public BackupCreateService(DaspalenRepository repository,
                               CardImageService cardImageService,
                               SettingsService settingsService) {
        this.repository = repository;
        this.cardImageService = cardImageService;
        this.settingsService = settingsService;
        this.gson = new GsonBuilder().create();
    }

    public void startBackup(OutputStream outputStream) {
        status = "Starting task...";
        imageFiles = cardImageService.getAllFiles();

        jsonStringWriter = new StringWriter();
        zipOutputStream = new ZipOutputStream(outputStream);
        maxCardId = 0L;

        maxProgress = repository.getAllCardCount() + imageFiles.size() + 1;
        currentProgress = 0;

        backupStatus = BackupStatus.EXPORTING_SETTINGS;
    }

    public void cancelBackup() {
        if (jsonStringWriter != null) {
            try {
                jsonStringWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                jsonStringWriter = null;
            }
        }
        if (zipOutputStream != null) {
            try {
                zipOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                zipOutputStream = null;
            }
        }
        backupStatus = BackupStatus.BACKUP_NOT_RUNNING;
        status = "Backup cancelled";
        maxCardId = 0L;
    }

    public boolean doNextChunk() {
        if (backupStatus.equals(BackupStatus.EXPORTING_SETTINGS)) {
            return doChunkExportSettings();
        } else if (backupStatus.equals(BackupStatus.SAVE_SETTINGS_TO_ZIP)) {
            return doChunkSaveSettingsToZip();
        } else if (backupStatus.equals(BackupStatus.EXPORTING_CARDS)) {
            return doChunkExportCards();
        } else if (backupStatus.equals(BackupStatus.SAVE_CARDS_TO_ZIP)) {
            return doChunkSaveCardsToZip();
        } else if (backupStatus.equals(BackupStatus.EXPORTING_IMAGES)) {
            return doChunkExportImages();
        } else if (backupStatus.equals(BackupStatus.FINISHED)) {
            return doChunkFinished();
        }
        return false;
    }

    private boolean doChunkExportSettings() {
        status = "Exporting settings";

        jsonStringWriter.write(settingsService.toJson());
        jsonStringWriter.flush();

        backupStatus = BackupStatus.SAVE_SETTINGS_TO_ZIP;

        return true;
    }

    private boolean doChunkSaveSettingsToZip() {
        status = "Saving settings to ZIP";

        try {
            String fileName = "settings_V1.json";
            byte[] bytes = jsonStringWriter.toString().getBytes(StandardCharsets.UTF_8);
            zipOutputStream.putNextEntry(new ZipEntry(fileName));
            zipOutputStream.write(bytes);
            jsonStringWriter.close();

            jsonStringWriter = new StringWriter();
            backupStatus = BackupStatus.EXPORTING_CARDS;
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            backupStatus = BackupStatus.ERROR;
            status = "Could not save settings json to zip";
            return false;
        }
    }

    private boolean doChunkExportCards() {
        status = "Exporting cards";

        int chunkSize = 100;
        List<Card> cards = repository.getCardsChunked(maxCardId,
                DaspalenConstants.CARD_TYPES_ALL, chunkSize);
        for (Card card : cards) {
            currentProgress++;
            jsonStringWriter.write(gson.toJson(card, Card.class));
            maxCardId = card.getCardId();
        }

        if (cards.size() == 0) {
            backupStatus = BackupStatus.SAVE_CARDS_TO_ZIP;
            jsonStringWriter.flush();
        }
        return true;
    }

    private boolean doChunkSaveCardsToZip() {
        status = "Saving cards to ZIP";

        try {
            String fileName = "cards_V1.json";
            byte[] bytes = jsonStringWriter.toString().getBytes(StandardCharsets.UTF_8);
            zipOutputStream.putNextEntry(new ZipEntry(fileName));
            zipOutputStream.write(bytes);
            jsonStringWriter.close();

            jsonStringWriter = new StringWriter();
            backupStatus = BackupStatus.EXPORTING_IMAGES;
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            backupStatus = BackupStatus.ERROR;
            status = "Could not save cards json to zip";
            return false;
        }
    }

    private boolean doChunkFinished() {
        status = "Export finished";

        try {
            zipOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
            backupStatus = BackupStatus.ERROR;
            status = "Could close zip file";
            return false;
        }

        currentProgress++;

        return false;
    }

    private boolean doChunkExportImages() {
        status = "Exporting images";

        try {
            if (imageFiles.size() > 0) {
                File img = imageFiles.remove(0);
                currentProgress++;

                zipOutputStream.putNextEntry(new ZipEntry(img.getName()));
                FileInputStream imgInputStream = new FileInputStream(img);

                int length;
                byte[] buffer = new byte[1024];
                while ((length = imgInputStream.read(buffer)) > 0) {
                    zipOutputStream.write(buffer, 0, length);
                }
                imgInputStream.close();
            } else {
                backupStatus = BackupStatus.FINISHED;
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            backupStatus = BackupStatus.ERROR;
            status = "Could not save images to zip";
            return false;
        }
    }

    private enum BackupStatus {
        BACKUP_NOT_RUNNING,
        EXPORTING_SETTINGS,
        SAVE_SETTINGS_TO_ZIP,
        EXPORTING_CARDS,
        SAVE_CARDS_TO_ZIP,
        EXPORTING_IMAGES,
        FINISHED,
        ERROR
    }

    public int getCurrentProgress() {
        return (this.currentProgress * 100) / this.maxProgress;
    }

    public String getDefaultFileName() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String date = formatter.format(new Date(System.currentTimeMillis()));
        return "Daspalen_backup_" + date + "_V1.zip";
    }

    public boolean getFinished() {
        return backupStatus.equals(BackupStatus.FINISHED);
    }

    public String getStatus() {
        return this.status;
    }

}
