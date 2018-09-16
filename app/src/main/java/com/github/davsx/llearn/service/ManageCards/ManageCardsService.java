package com.github.davsx.llearn.service.ManageCards;

import com.github.davsx.llearn.persistence.entity.CardEntity;
import com.github.davsx.llearn.persistence.repository.CardRepository;

import java.util.ArrayList;
import java.util.List;

public class ManageCardsService {
    private static int LOAD_CHUNK_SIZE = 30;

    private CardRepository cardRepository;
    private ArrayList<CardEntity> cards;
    private Long maxLoadedCardId = 0L;
    private boolean hasMoreCards = true;
    private String searchQuery = null;
    private boolean showOnlyIncomplete = false;

    public ManageCardsService(CardRepository cardRepository) {
        this.cardRepository = cardRepository;
        this.cards = new ArrayList<>();
        reset();
        loadMoreCards(LOAD_CHUNK_SIZE * 2);
    }

    public CardEntity getCardByPosition(int position) {
        return cards.get(position);
    }

    private boolean loadMoreCards(int limit) {
        List<CardEntity> moreCards;
        if (searchQuery == null) {
            moreCards = cardRepository.getCardsChunked(maxLoadedCardId, showOnlyIncomplete, limit);
        } else {
            moreCards = cardRepository.searchCardsChunked(searchQuery, maxLoadedCardId, showOnlyIncomplete, limit);
        }
        if (moreCards.size() > 0) {
            maxLoadedCardId = moreCards.get(moreCards.size() - 1).getId();
            cards.addAll(moreCards);
            return true;
        } else {
            hasMoreCards = false;
            return false;
        }
    }

    private void reset() {
        cards.clear();
        maxLoadedCardId = 0L;
        hasMoreCards = true;
    }

    public void searchCards(String query) {
        if (searchQuery == null) {
            reset();
            searchQuery = query;
        }
        loadMoreCards(LOAD_CHUNK_SIZE * 2);
    }

    public void cancelSearch() {
        reset();
        searchQuery = null;
    }

    public void setShowOnlyIncomplete(boolean showOnlyIncomplete) {
        reset();
        this.showOnlyIncomplete = showOnlyIncomplete;
        loadMoreCards(LOAD_CHUNK_SIZE * 2);
    }

    public Long createCard(CardEntity card) {
//        Long idCard = cardRepository.save(card);
//        CardEntity savedCard = cardRepository.getCardWithId(idCard);
//        allCards.add(savedCard);
//        if (filteredCards != allCards) {
//            filteredCards.add(savedCard);
//        }
//        return idCard;
        return null;
    }

    public void updateCard(CardEntity card) {
        cardRepository.save(card);
    }

    public void deleteCard(CardEntity card) {
        cardRepository.deleteCard(card);
    }

    public boolean onScrolled(int lastVisibleItemPosition) {
        if (lastVisibleItemPosition + LOAD_CHUNK_SIZE / 2 > cards.size()) {
            if (hasMoreCards) {
                return loadMoreCards(LOAD_CHUNK_SIZE);
            }
        }
        return false;
    }

    public int getItemCount() {
        return cards.size();
    }
}