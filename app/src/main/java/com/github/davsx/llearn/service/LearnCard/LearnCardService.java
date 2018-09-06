package com.github.davsx.llearn.service.LearnCard;

import com.github.davsx.llearn.data.LearnCard.CardTypeEnum;
import com.github.davsx.llearn.data.LearnCard.KeyboardKeyChooser;
import com.github.davsx.llearn.data.LearnCard.LearnCardData;
import com.github.davsx.llearn.persistence.entity.CardEntity;
import com.github.davsx.llearn.persistence.repository.CardRepository;

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
    // * For cards with score 0 use "show card" first

    public LearnCardService(CardRepository cardRepository) {
        this.cardRepository = cardRepository;
        currentCardType = CardTypeEnum.NONE;
        learnCardsIndex = 0;
    }

    public boolean startSession() {
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
