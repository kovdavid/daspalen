package com.github.davsx.daspalen.service.MainActivity;

import com.github.davsx.daspalen.persistence.repository.DaspalenRepository;
import com.github.davsx.daspalen.service.CardImage.CardImageService;

import java.util.Locale;

public class MainActivityService {

    private DaspalenRepository repository;
    private CardImageService cardImageService;

    public MainActivityService(DaspalenRepository repository, CardImageService cardImageService) {
        this.repository = repository;
        this.cardImageService = cardImageService;
    }

    public void wipeData() {
        repository.wipeData();
        cardImageService.wipeData();
    }

    public boolean canLearnCards() {
        return repository.getLearnCardCount() > 0;
    }

    public boolean canReviewCards() {
        return repository.getReviewCardCount() > 0;
    }

    public String getLearnButtonString() {
        return String.format(Locale.getDefault(), "Learn cards\n(%d)", repository.getLearnCardCount());
    }

    public String getManageButtonString() {
        return String.format(Locale.getDefault(), "Manage cards\n(%d)", repository.getAllCardCount());
    }

    public String getReviewButtonString() {
        return String.format(Locale.getDefault(), "Review cards\n(%d/%d)",
                repository.getOverdueReviewCardCount(),
                repository.getReviewCardCount());
    }
}
