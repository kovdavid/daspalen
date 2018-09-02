package com.github.davsx.llearn.service.LearnCard;

import com.github.davsx.llearn.data.LearnCard.CardTypeEnum;
import com.github.davsx.llearn.data.LearnCard.ShowCardData;
import com.github.davsx.llearn.persistence.entity.CardEntity;
import com.github.davsx.llearn.persistence.repository.CardRepository;

import java.util.ArrayList;

public class LearnCardService {
    private CardRepository cardRepository;

    private CardEntity currentCard;
    private CardTypeEnum currentCardType;
    private ShowCardData showCardData;
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
            showNextCard();
            return true;
        } else {
            return false;
        }
    }

    private void showNextCard() {
        currentCard = learnCards.get(learnCardsIndex);

        currentCardType = CardTypeEnum.SHOW_CARD;
        showCardData = new ShowCardData(currentCard.getFront(), currentCard.getBack());
    }

    public CardEntity getCurrentCard() {
        return currentCard;
    }

    public CardTypeEnum getCurrentCardType() {
        return currentCardType;
    }

    public ShowCardData getShowCardData() {
        return showCardData;
    }

    private void setCurrentCard(CardEntity currentCard) {
        this.currentCard = currentCard;
    }

    public void processAnswer(String answer) {
        learnCardsIndex++;
        showNextCard();
    }
}
