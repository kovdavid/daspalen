package com.github.davsx.daspalen.service.BackupImport;

import com.github.davsx.daspalen.model.Card;
import com.github.davsx.daspalen.persistence.repository.DaspalenRepository;
import com.github.davsx.daspalen.service.CardImage.CardImageService;
import com.github.davsx.daspalen.service.Settings.SettingsService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class BackupImportService {

    private DaspalenRepository repository;
    private CardImageService cardImageService;
    private SettingsService settingsService;
    private Gson gson;

    private ZipInputStream zipInputStream;
    private ZipEntry zipEntry;
    private InputStreamReader inputStreamReader;

    private String status;
    private ImportStatus importStatus = ImportStatus.IMPORT_NOT_RUNNING;

    public BackupImportService(DaspalenRepository repository,
                               CardImageService cardImageService,
                               SettingsService settingsService) {
        this.repository = repository;
        this.cardImageService = cardImageService;
        this.settingsService = settingsService;
        this.gson = new GsonBuilder().create();
    }

    public void startImport(InputStream inputStream) {
        this.status = "Starting import...";
        this.zipInputStream = new ZipInputStream(inputStream);
        this.zipEntry = null;
        this.inputStreamReader = new InputStreamReader(zipInputStream);

        this.importStatus = ImportStatus.DELETING_DATA;
    }

    public void cancelImport() {
        if (zipInputStream != null) {
            try {
                zipInputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                zipInputStream = null;
            }
        }
        if (inputStreamReader != null) {
            try {
                inputStreamReader.close();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                inputStreamReader = null;
            }
        }
        importStatus = ImportStatus.IMPORT_NOT_RUNNING;
        status = "Import cancelled";
    }

    public boolean doNextChunk() {
        if (importStatus.equals(ImportStatus.DELETING_DATA)) {
            status = "Deleting data";

            repository.wipeData();
            cardImageService.wipeData();
            importStatus = ImportStatus.LOADING_DATA;

            return true;
        } else if (importStatus.equals(ImportStatus.LOADING_DATA)) {
            try {
                zipEntry = zipInputStream.getNextEntry();
                if (zipEntry == null) {
                    importStatus = ImportStatus.FINISHED;
                    status = "Import finished";
                    return false;
                }

                switch (zipEntry.getName()) {
                    case "cards_V1.json":
                        loadCardsFromJsonV1();
                        break;
                    case "settings_V1.json":
                        loadSettingsFromJsonV1();
                        break;
                    default:
                        cardImageService.saveImageFromStream(zipEntry.getName(), zipInputStream);
                        break;
                }

                return true;
            } catch (Exception e) {
                e.printStackTrace();
                importStatus = ImportStatus.ERROR;
                status = "Could not get zip entry";
                return false;
            }
        }
        importStatus = ImportStatus.FINISHED;
        return false;
    }

    private void loadSettingsFromJsonV1() throws IOException {
        StringBuilder stringBuilder = new StringBuilder();

        byte[] buffer = new byte[8096];
        int length;

        while ((length = zipInputStream.read(buffer)) != -1) {
            stringBuilder.append(new String(buffer, 0, length));
        }

        settingsService.fromJson(stringBuilder.toString());
    }

    private void loadCardsFromJsonV1() throws IOException {
        String json;
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

        List<Card> cards = new ArrayList<>();
        while ((json = bufferedReader.readLine()) != null) {
            Card card = gson.fromJson(json, Card.class);
            cards.add(card);

            if (cards.size() > 100) {
                repository.createNewCards(cards);
                cards.clear();
            }
        }

        if (cards.size() > 0) {
            repository.createNewCards(cards);
        }
    }

    private enum ImportStatus {
        IMPORT_NOT_RUNNING,
        DELETING_DATA,
        LOADING_DATA,
        FINISHED,
        ERROR
    }

    public boolean getFinished() {
        return importStatus.equals(ImportStatus.FINISHED);
    }

    public String getStatus() {
        return status;
    }
}