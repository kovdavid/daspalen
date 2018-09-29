package com.github.davsx.llearn.service.CardImport;

import com.github.davsx.llearn.persistence.entity.CardEntity;
import com.github.davsx.llearn.persistence.repository.CardRepository;
import com.github.davsx.llearn.service.CardImage.CardImageService;
import com.opencsv.CSVReader;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class CardImportService {

    private CardRepository cardRepository;
    private CardImageService cardImageService;

    private ZipInputStream zipInputStream;
    private ZipEntry zipEntry;

    private String status;
    private ImportStatus importStatus = ImportStatus.IMPORT_NOT_RUNNING;

    public CardImportService(CardRepository cardRepository, CardImageService cardImageService) {
        this.cardRepository = cardRepository;
        this.cardImageService = cardImageService;
    }

    public void startImport(InputStream inputStream) {
        this.status = "Starting import...";
        this.zipInputStream = new ZipInputStream(inputStream);
        this.zipEntry = null;

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
        importStatus = ImportStatus.IMPORT_NOT_RUNNING;
        status = "Import cancelled";
    }

    public boolean doNextChunk() {
        if (importStatus.equals(ImportStatus.DELETING_DATA)) {
            status = "Deleting data";

            cardRepository.deleteAllCards();
            cardImageService.deleteAllImages();
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
                if (zipEntry.getName().equals("cards_export_V1.csv")) {
                    loadCardsFromCsvV1(zipInputStream);
                } else {
                    cardImageService.saveImageFromStream(zipEntry.getName(), zipInputStream);
                }
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

    private void loadCardsFromCsvV1(ZipInputStream zipInputStream) throws IOException {
        InputStreamReader isr = new InputStreamReader(zipInputStream);
        CSVReader csv = new CSVReader(isr);

        List<CardEntity> cards = new ArrayList<>();

        String[] row;
        while ((row = csv.readNext()) != null) {
            cards.add(CardEntity.fromCsvDataV1(row));
        }

        cardRepository.saveMany(cards);

        isr.close();
    }

    public String getStatus() {
        return status;
    }

    public boolean getFinished() {
        return importStatus.equals(ImportStatus.FINISHED);
    }

    private enum ImportStatus {
        IMPORT_NOT_RUNNING,
        DELETING_DATA,
        LOADING_DATA,
        FINISHED,
        ERROR
    }
}