package com.github.davsx.llearn.service.MemriseImport;

import android.support.v4.util.Pair;
import com.github.davsx.llearn.persistence.entity.CardEntity;
import com.github.davsx.llearn.persistence.repository.CardRepository;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class MemriseImportService {

    private CardRepository cardRepository;
    private Integer currentCard = 0;
    private List<Pair<String, String>> cards = new ArrayList<>();

    public MemriseImportService(CardRepository cardRepository) {
        this.cardRepository = cardRepository;
    }

    public void startImport(InputStream inputStream) {
        cards.clear();

        List<String> lines = new ArrayList<>();
        try {
            if (inputStream == null) return;
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        } catch (Exception e) {
            return;
        }

        for (String line : lines) {
            String[] tokens = line.split("\t");
            if (tokens.length >= 2) {
                String backText = tokens[0];
                String frontText = tokens[1];
                cards.add(new Pair<>(frontText, backText));
            }
        }

        currentCard = 0;
    }

    public void skipCard() {
        currentCard++;
    }

    public CardEntity findDuplicateCard(String front, String back) {
        return cardRepository.findDuplicateCard(front, back);
    }

    public void saveCard(String front, String back) {
        CardEntity card = new CardEntity()
                .setFront(front)
                .setBack(back)
                .setCreatedAt(System.currentTimeMillis())
                .setLearnScore(0);
        cardRepository.save(card);

        currentCard++;
    }

    public Pair<String, String> getNextCard() {
        if (currentCard >= cards.size()) return new Pair<>("", "");
        Pair<String, String> card = cards.get(currentCard);
        if (card == null) {
            return new Pair<>("", "");
        } else {
            return card;
        }
    }

    public int getProgress() {
        if (cards.size() <= currentCard) {
            return 100;
        }
        return currentCard * 100 / cards.size();
    }
}
