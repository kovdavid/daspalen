package com.github.davsx.llearn.service.ManageCards;

import com.github.davsx.llearn.LLearnConstants;
import com.github.davsx.llearn.model.Card;
import com.github.davsx.llearn.persistence.repository.LLearnRepository;

import java.util.ArrayList;
import java.util.List;

public class ManageCardsService {
    public static int RESULT_CARD_CHANGED = 1;
    public static int RESULT_CARD_NOT_CHANGED = 2;
    public static int RESULT_CARD_ADDED = 3;
    public static int RESULT_CARD_DELETED = 4;

    private static int LOAD_CHUNK_SIZE = 30;

    private LLearnRepository repository;
    private ArrayList<Card> cards;
    private Long maxLoadedCardId = 0L;
    private boolean hasMoreCards = true;
    private String searchQuery = null;

    private boolean showIncompleteCards = true;
    private boolean showLearnableCards = true;
    private boolean showReviewableCards = true;

    public ManageCardsService(LLearnRepository repository) {
        this.repository = repository;
        this.cards = new ArrayList<>();
        reset();
        loadMoreCards(LOAD_CHUNK_SIZE * 2);
    }

    public Card getCardByPosition(int position) {
        return cards.get(position);
    }

    private boolean loadMoreCards(int limit) {
        List<Card> moreCards;
        if (searchQuery == null) {
            moreCards = repository.getCardsChunked(maxLoadedCardId, getCardTypes(), limit);
        } else {
            moreCards = repository.searchCardsChunked(searchQuery, maxLoadedCardId, getCardTypes(), limit);
        }
        if (moreCards.size() > 0) {
            maxLoadedCardId = moreCards.get(moreCards.size() - 1).getCardId();
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

    public void enableCard(Card card) {
        card.setEnabled(true);
        repository.updateCard(card);
    }

    public void disableCard(Card card) {
        card.setEnabled(false);
        repository.updateCard(card);
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
        cards.set(cardPosition, repository.getCardWithId(cardId));
    }

    public void cardDeleted(int cardPosition) {
        cards.remove(cardPosition);
    }

    public void cardAdded() {
        loadMoreCards(LOAD_CHUNK_SIZE);
    }

    public void deleteCard(Card card, int position) {
        repository.deleteCard(card);
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