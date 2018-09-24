package com.github.davsx.llearn.service.CardImport;

import com.github.davsx.llearn.persistence.repository.CardRepository;
import com.github.davsx.llearn.service.CardImage.CardImageService;

import java.io.InputStream;

public class CardImportService {

    private CardRepository cardRepository;
    private CardImageService cardImageService;

    public CardImportService(CardRepository cardRepository, CardImageService cardImageService) {
        this.cardRepository = cardRepository;
        this.cardImageService = cardImageService;
    }

    public void startImport(InputStream inputStream) {

    }
}
