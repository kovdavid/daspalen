package com.github.davsx.llearn.service.ManageCards;

import com.github.davsx.llearn.LLearnConstants;
import com.github.davsx.llearn.persistence.entity.CardEntity;
import com.github.davsx.llearn.persistence.repository.CardRepository;

import java.util.ArrayList;
import java.util.List;

public class ManageCardsService {
    public static int RESULT_CARD_CHANGED = 1;
    public static int RESULT_CARD_NOT_CHANGED = 2;
    public static int RESULT_CARD_ADDED = 3;
    public static int RESULT_CARD_DELETED = 4;

    private static int LOAD_CHUNK_SIZE = 30;

    private CardRepository cardRepository;
    private ArrayList<CardEntity> cards;
    private Long maxLoadedCardId = 0L;
    private boolean hasMoreCards = true;
    private String searchQuery = null;

    private boolean showIncompleteCards = true;
    private boolean showLearnableCards = true;
    private boolean showReviewableCards = true;

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
            moreCards = cardRepository.getCardsChunked(maxLoadedCardId, getCardTypes(), limit);
        } else {
            moreCards = cardRepository.searchCardsChunked(searchQuery, maxLoadedCardId,
                    getCardTypes(), limit);
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

    public void enableCard(CardEntity card) {
        card.setEnabled(true);
        cardRepository.save(card);
    }

    public void disableCard(CardEntity card) {
        card.setEnabled(false);
        cardRepository.save(card);
    }

    public void cancelSearch() {
        reset();
        searchQuery = null;
        loadMoreCards(LOAD_CHUNK_SIZE * 2);
    }

    public boolean onScrolled(int lastVisibleItemPosition) {
        if (lastVisibleItemPosition + LOAD_CHUNK_SIZE / 2 > cards.size()) {
            if (hasMoreCards) {
                return loadMoreCards(LOAD_CHUNK_SIZE);
            }
        }
        return false;
    }

    public void cardChanged(long cardId, int cardPosition) {
        cards.set(cardPosition, cardRepository.getCardWithId(cardId));
    }

    public void cardDeleted(int cardPosition) {
        cards.remove(cardPosition);
    }

    public void cardAdded() {
        loadMoreCards(LOAD_CHUNK_SIZE);
    }

    public void deleteCard(CardEntity card, int position) {
        cardRepository.deleteCard(card);
        cards.remove(position);
    }

    public void setShowIncompleteCards(boolean show) {
        reset();
        this.showIncompleteCards = show;
        loadMoreCards(LOAD_CHUNK_SIZE * 2);
    }

    public void setShowLearnableCards(boolean show) {
        reset();
        this.showLearnableCards = show;
        loadMoreCards(LOAD_CHUNK_SIZE * 2);
    }

    public void setShowReviewableCards(boolean show) {
        reset();
        this.showReviewableCards = show;
        loadMoreCards(LOAD_CHUNK_SIZE * 2);
    }

    private List<Integer> getCardTypes() {
        List<Integer> types = new ArrayList<>();

        if (showIncompleteCards) {
            types.add(LLearnConstants.CARD_TYPE_INCOMPLETE);
        }
        if (showLearnableCards) {
            types.add(LLearnConstants.CARD_TYPE_LEARN);
        }
        if (showReviewableCards) {
            types.add(LLearnConstants.CARD_TYPE_REVIEW);
        }

        return types;
    }

    public int getItemCount() {
        return cards.size();
    }
}