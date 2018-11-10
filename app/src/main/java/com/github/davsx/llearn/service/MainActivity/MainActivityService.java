package com.github.davsx.llearn.service.MainActivity;

import com.github.davsx.llearn.persistence.repository.LLearnRepository;
import com.github.davsx.llearn.service.CardImage.CardImageService;

import java.util.Locale;

public class MainActivityService {

    private LLearnRepository repository;
    private CardImageService cardImageService;

    public MainActivityService(LLearnRepository repository, CardImageService cardImageService) {
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
        return String.format(Locale.getDefault(), "Lear cards\n(%d)", repository.getLearnCardCount());
    }

    public String getManageButtonString() {
        return String.format(Locale.getDefault(), "Manage cards\n(%d)", repository.getAllCardCount());
    }

    public String getReviewButtonString() {
        return String.format(Locale.getDefault(), "Lear cards\n(%d/%d)",
                repository.getOverdueReviewCardCount(),
                repository.getReviewCardCount());
    }
}
