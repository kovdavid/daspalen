package com.github.davsx.llearn.service.ManageCards;

import android.widget.Filter;
import android.widget.Filterable;
import com.github.davsx.llearn.persistence.entity.CardEntity;
import com.github.davsx.llearn.persistence.repository.CardRepository;

import java.util.ArrayList;

public class ManageCardsService implements Filterable {
    private CardRepository cardRepository;
    private ArrayList<CardEntity> filteredCards;
    private ArrayList<CardEntity> allCards;

    public ManageCardsService(CardRepository cardRepository) {
        this.cardRepository = cardRepository;
        this.allCards = (ArrayList<CardEntity>) cardRepository.getAllCards();
        this.filteredCards = this.allCards;
    }

    public CardEntity getCardByPosition(int position) {
        return filteredCards.get(position);
    }

    public int getItemCount() {
        return filteredCards.size();
    }

    public Long createCard(CardEntity card) {
        Long idCard = cardRepository.save(card);
        CardEntity savedCard = cardRepository.getCardWithId(idCard);
        allCards.add(savedCard);
        if (filteredCards != allCards) {
            filteredCards.add(savedCard);
        }
        return idCard;
    }

    public void updateCard(CardEntity card) {
        cardRepository.save(card);
    }

    public void deleteCard(CardEntity card) {
        cardRepository.deleteCard(card);
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                ArrayList<CardEntity> filteredCards = new ArrayList<>();

                if (charSequence == null) {
                    filteredCards = allCards;
                } else {
                    for (CardEntity card : allCards) {
                        if (card.getFront().contains(charSequence) || card.getBack().contains(charSequence)) {
                            filteredCards.add(card);
                        }
                    }
                }

                FilterResults results = new FilterResults();
                results.values = filteredCards;
                return results;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                ManageCardsService.this.filteredCards = (ArrayList<CardEntity>) filterResults.values;
            }
        };
    }
}