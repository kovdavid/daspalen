package com.github.davsx.llearn.gui.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.github.davsx.llearn.R;
import com.github.davsx.llearn.domain.persistence.CardRepository;
import com.github.davsx.llearn.infrastructure.persistence.entity.CardEntity;

import javax.inject.Inject;
import java.util.ArrayList;

public class ManageCardsAdapter extends RecyclerView.Adapter<ManageCardsAdapter.ViewHolder> {

    public CardRepository cardRepository;
    public LayoutInflater layoutInflater;

    private ArrayList<CardEntity> filteredCards;

    @Inject
    public ManageCardsAdapter(CardRepository cardRepository, LayoutInflater layoutInflater) {
        this.cardRepository = cardRepository;
        this.layoutInflater = layoutInflater;

        this.filteredCards = (ArrayList<CardEntity>) cardRepository.getAllCards();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.activity_manage_cards_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CardEntity card = filteredCards.get(position);
        holder.textViewIdCard.setText(Long.toString(card.getId()));
        holder.textViewLearnScore.setText(Integer.toString(card.getLearnScore()));
        holder.textViewFront.setText(card.getFront());
        holder.textViewFront.setText(card.getBack());
        holder.setCard(card);
    }

    @Override
    public int getItemCount() {
        return filteredCards.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.textview_id_card) TextView textViewIdCard;
        @BindView(R.id.textview_learn_score) TextView textViewLearnScore;
        @BindView(R.id.textview_front) TextView textViewFront;
        @BindView(R.id.textview_back) TextView textViewBack;
        private CardEntity card;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(itemView);

            textViewIdCard.setOnClickListener(this);
            textViewFront.setOnClickListener(this);
            textViewBack.setOnClickListener(this);
            textViewLearnScore.setOnClickListener(this);
        }

        public void setCard(CardEntity card) {
            this.card = card;
        }

        @Override
        public void onClick(View view) {
            // TODO show edit card dialog
        }
    }
}
