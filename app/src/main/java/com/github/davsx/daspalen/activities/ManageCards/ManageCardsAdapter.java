package com.github.davsx.daspalen.activities.ManageCards;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.github.davsx.daspalen.DaspalenConstants;
import com.github.davsx.daspalen.R;
import com.github.davsx.daspalen.activities.CardEditor.CardEditorActivity;
import com.github.davsx.daspalen.model.Card;
import com.github.davsx.daspalen.service.ManageCards.ManageCardsService;

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
        Card card = manageCardsService.getCardByPosition(position);
        if (card != null) {
            if (card.getEnabled()) {
                holder.textViewIdCard.setBackgroundResource(R.color.colorPrimary);
            } else {
                holder.textViewIdCard.setBackgroundResource(R.color.colorAccent);
            }
            holder.textViewIdCard.setText(Long.toString(card.getCardId()));
            if (card.getLearnScore() < DaspalenConstants.MAX_CARD_LEARN_SCORE) {
                holder.textViewLearnScore.setText(Integer.toString(card.getLearnScore()));
            } else {
                holder.textViewLearnScore.setText("R");
            }
            holder.textViewFront.setText(card.getFrontText());
            holder.textViewBack.setText(card.getBackText());
            holder.card = card;
            holder.position = position;
        }
    }

    @Override
    public int getItemCount() {
        return manageCardsService.getItemCount();
    }

    void showIncompleteCards(boolean show) {
        manageCardsService.setShowIncompleteCards(show);
        notifyDataSetChanged();
    }

    void showLearnableCards(boolean show) {
        manageCardsService.setShowLearnableCards(show);
        notifyDataSetChanged();
    }

    void showReviewableCards(boolean show) {
        manageCardsService.setShowReviewableCards(show);
        notifyDataSetChanged();
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

    public class CardViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
            View.OnLongClickListener {
        private TextView textViewIdCard;
        private TextView textViewLearnScore;
        private TextView textViewFront;
        private TextView textViewBack;
        private Card card;
        private int position;

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

            textViewIdCard.setOnLongClickListener(this);
            textViewFront.setOnLongClickListener(this);
            textViewBack.setOnLongClickListener(this);
            textViewLearnScore.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Intent i = new Intent(context, CardEditorActivity.class);
            i.putExtra("ID_CARD", card.getCardId());
            i.putExtra("CARD_POSITION", position);
            context.startActivity(i);
        }

        @Override
        public boolean onLongClick(View v) {
            String[] items;
            if (card.getEnabled()) {
                items = new String[]{"Disable card", "Delete card"};
            } else {
                items = new String[]{"Enable card", "Delete card"};
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setItems(items, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (which == 0) {
                        if (card.getEnabled()) {
                            manageCardsService.disableCard(card);
                        } else {
                            manageCardsService.enableCard(card);
                        }
                        notifyDataSetChanged();
                    } else if (which == 1) {
                        showConfirmDeleteDialog(false);
                    }
                }
            });
            builder.setTitle("Choose action");
            builder.setCancelable(true);
            builder.show();
            return true;
        }

        private void showConfirmDeleteDialog(final boolean confirmed) {
            String message = confirmed ? "Are you really sure?" : "Are you sure?";
            final android.support.v7.app.AlertDialog.Builder builder =
                    new android.support.v7.app.AlertDialog.Builder(context)
                            .setTitle("Delete card")
                            .setMessage(message)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (confirmed) {
                                        deleteCard();
                                    } else {
                                        dialog.dismiss();
                                        showConfirmDeleteDialog(true);
                                    }
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
            builder.show();
        }

        private void deleteCard() {
            manageCardsService.deleteCard(card, position);
            notifyDataSetChanged();
        }
    }
}
