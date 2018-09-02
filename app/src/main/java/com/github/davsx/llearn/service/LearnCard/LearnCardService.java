package com.github.davsx.llearn.service.LearnCard;

import com.github.davsx.llearn.data.LearnCard.CardTypeEnum;
import com.github.davsx.llearn.data.LearnCard.KeyboardKeyChooser;
import com.github.davsx.llearn.data.LearnCard.LearnCardData;
import com.github.davsx.llearn.persistence.entity.CardEntity;
import com.github.davsx.llearn.persistence.repository.CardRepository;

import java.util.ArrayList;

public class LearnCardService {
    private CardRepository cardRepository;

    private CardEntity currentCard;
    private CardTypeEnum currentCardType;
    private LearnCardData learnCardData;
    private ArrayList<CardEntity> learnCards;
    private Integer learnCardsIndex;

    public LearnCardService(CardRepository cardRepository) {
        this.cardRepository = cardRepository;
        currentCardType = CardTypeEnum.NONE;
        learnCardsIndex = 0;
    }

    public boolean startSession() {
        learnCards = (ArrayList<CardEntity>) cardRepository.getAllCards();

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

        if (learnCardsIndex % 2 == 1) {
            currentCardType = CardTypeEnum.SHOW_CARD;
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
