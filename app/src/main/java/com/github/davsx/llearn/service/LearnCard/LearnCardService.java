package com.github.davsx.llearn.service.LearnCard;

import com.github.davsx.llearn.persistence.entity.CardEntity;
import com.github.davsx.llearn.persistence.repository.CardRepository;
import info.debatty.java.stringsimilarity.Levenshtein;
import info.debatty.java.stringsimilarity.StringSimilarityInterface;

import java.util.*;

public class LearnCardService {
    private CardRepository cardRepository;

    private CardEntity currentCard;
    private CardTypeEnum currentCardType;
    private LearnCardData learnCardData;
    private ArrayList<CardEntity> learnCards;
    private Integer learnCardsIndex;

    // Rules:
    // * Max 5 new cards
    // * Use 1 new card if possible
    // * Max score is 8
    // * Cards with score 0-3 use 3x
    // * Cards with score 4-5 use 2x
    // * Cards with score 6-7 use 1x
    // * Max 20 rounds (calculated from card usage above)
    // * For cards with !score 0! || !lastLearned > 1 week! use "show card" first
    // *** That said, some cards will be blocked by "show card" before first real use

    // Question is: how to schedule cards with score: 0 0 2 4 6 7
    // For 0 - do the first real card 1-2 cards after the show card - space out the rest

    // Do a weight system, where we would use the cards with the lowest weight
    // After using a card, it's weight could be recalculated, so that it would be used before or after a card with score 7

    // 0 1 1 2 3 5 8 13 21 34 55 89 144
    public LearnCardService(CardRepository cardRepository) {
        this.cardRepository = cardRepository;
    }

    public boolean startSession() {

        currentCardType = CardTypeEnum.NONE;
        learnCardsIndex = 0;

        learnCards = (ArrayList<CardEntity>) cardRepository.getAllValidCards();

        if (learnCards.size() > 0) {
            setUpNextCard();
            return true;
        } else {
            return false;
        }
    }

    private void setUpNextCard() {
        currentCard = learnCards.get(learnCardsIndex);

        learnCardData = new LearnCardData(currentCard.getFront(), currentCard.getBack());

        if (learnCardsIndex % 4 == 1) {
            currentCardType = CardTypeEnum.SHOW_CARD;
        } else if (learnCardsIndex % 4 == 2) {
            currentCardType = CardTypeEnum.SHOW_CARD_WITH_IMAGE;
        } else if (learnCardsIndex % 4 == 0) {
            currentCardType = CardTypeEnum.CHOICE_1of4;

            Set<String> choicesSet = new HashSet<>();
            choicesSet.add(currentCard.getBack());

            Random rng = new Random();
            rng.setSeed(System.currentTimeMillis());
            while (choicesSet.size() != 4) {
                int index = rng.nextInt(learnCards.size());
                choicesSet.add(learnCards.get(index).getBack());
            }

            ArrayList<String> choicesList = new ArrayList<>(choicesSet);
            Collections.shuffle(choicesList);

            learnCardData.setChoices(choicesList);
            learnCardData.setGuessBack(true);
        } else {
            currentCardType = CardTypeEnum.KEYBOARD_INPUT;
            learnCardData.setKeyboardKeys(KeyboardKeyChooser.choose(currentCard.getBack()));
        }

    }

    public CardTypeEnum getCurrentCardType() {
        return currentCardType;
    }

    public LearnCardData getLearnCardData() {
        return learnCardData;
    }

    public void processAnswer(String answer) {
        learnCardsIndex++;
        setUpNextCard();
    }
}
