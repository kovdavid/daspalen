package com.github.davsx.llearn.service.CardImport;

import com.github.davsx.llearn.persistence.entity.CardEntity;
import com.github.davsx.llearn.persistence.entity.JournalEntity;
import com.github.davsx.llearn.persistence.repository.CardRepository;
import com.github.davsx.llearn.persistence.repository.JournalRepository;
import com.github.davsx.llearn.service.CardImage.CardImageService;
import com.opencsv.CSVReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class CardImportService {

    private CardRepository cardRepository;
    private JournalRepository journalRepository;
    private CardImageService cardImageService;

    private ZipInputStream zipInputStream;
    private ZipEntry zipEntry;

    private String status;
    private ImportStatus importStatus = ImportStatus.IMPORT_NOT_RUNNING;

    public CardImportService(CardRepository cardRepository, JournalRepository journalRepository,
                             CardImageService cardImageService) {
        this.cardRepository = cardRepository;
        this.journalRepository = journalRepository;
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
            journalRepository.deleteAllJournals();
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
                } else if (zipEntry.getName().equals("journals_export_V1.csv")) {
                    loadJournalsFromCsvV1(zipInputStream);
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

    private void loadJournalsFromCsvV1(ZipInputStream zipInputStream) throws IOException {
        InputStreamReader isr = new InputStreamReader(zipInputStream);
        CSVReader csv = new CSVReader(isr);

        List<JournalEntity> journals = new ArrayList<>();

        String[] row;
        while ((row = csv.readNext()) != null) {
            journals.add(JournalEntity.fromCsvDataV1(row));
        }

        journalRepository.saveMany(journals);

        isr.close();
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