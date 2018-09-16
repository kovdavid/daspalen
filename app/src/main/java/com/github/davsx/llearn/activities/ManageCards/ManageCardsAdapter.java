package com.github.davsx.llearn.activities.ManageCards;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.github.davsx.llearn.R;
import com.github.davsx.llearn.persistence.entity.CardEntity;
import com.github.davsx.llearn.service.ManageCards.ManageCardsService;

public class ManageCardsAdapter extends RecyclerView.Adapter<ManageCardsAdapter.CardViewHolder> {

    private ManageCardsService manageCardsService;
    private LayoutInflater layoutInflater;
    private Context context;

    ManageCardsAdapter(Context context, ManageCardsService manageCardsService) {
        this.context = context;
        this.manageCardsService = manageCardsService;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ManageCardsAdapter.CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.activity_manage_cards_list_item, parent, false);
        return new ManageCardsAdapter.CardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ManageCardsAdapter.CardViewHolder holder, int position) {
        CardEntity card = manageCardsService.getCardByPosition(position);
        if (card != null) {
            holder.textViewIdCard.setText(Long.toString(card.getId()));
            holder.textViewLearnScore.setText(Integer.toString(card.getLearnScore()));
            holder.textViewFront.setText(card.getFront());
            holder.textViewBack.setText(card.getBack());
            holder.setCard(card);
        }
    }

    void showOnlyIncomplete(boolean checked) {
        manageCardsService.setShowOnlyIncomplete(checked);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return manageCardsService.getItemCount();
    }

    void searchCards(String query) {
        manageCardsService.searchCards(query);
        notifyDataSetChanged();
    }

    void cancelSearch() {
        manageCardsService.cancelSearch();
        notifyDataSetChanged();
    }

    boolean onScrolled(int lastVisibleItemPosition) {
        return manageCardsService.onScrolled(lastVisibleItemPosition);
    }

    public class CardViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView textViewIdCard;
        private TextView textViewLearnScore;
        private TextView textViewFront;
        private TextView textViewBack;
        private CardEntity card;

        CardViewHolder(View itemView) {
            super(itemView);

            textViewIdCard = itemView.findViewById(R.id.textview_id_card);
            textViewFront = itemView.findViewById(R.id.textview_front);
            textViewBack = itemView.findViewById(R.id.textview_back);
            textViewLearnScore = itemView.findViewById(R.id.textview_learn_score);

            textViewIdCard.setOnClickListener(this);
            textViewFront.setOnClickListener(this);
            textViewBack.setOnClickListener(this);
            textViewLearnScore.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            EditCardDialog editCardDialog = new EditCardDialog(context, manageCardsService, card);
            editCardDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialogInterface) {
                    ManageCardsAdapter.this.notifyDataSetChanged();
                }
            });
            editCardDialog.show();
        }

        public void setCard(CardEntity card) {
            this.card = card;
        }
    }
}
