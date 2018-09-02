package com.github.davsx.llearn.gui.activities.ManageCards;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.github.davsx.llearn.R;
import com.github.davsx.llearn.data.ManageCards.ManageCardsDataProvider;
import com.github.davsx.llearn.persistence.entity.CardEntity;

public class EditCardDialog extends Dialog {

    @BindView(R.id.front_text)
    EditText frontText;
    @BindView(R.id.back_text)
    EditText backText;
    @BindView(R.id.dialog_cancel)
    Button cancelButton;
    @BindView(R.id.dialog_confirm)
    Button confirmButton;
    @BindView(R.id.dialog_delete)
    Button deleteButton;

    private ManageCardsDataProvider dataProvider;
    private CardEntity card;

    public EditCardDialog(@NonNull Context context, ManageCardsDataProvider dataProvider, CardEntity card) {
        super(context);
        this.dataProvider = dataProvider;
        this.card = card;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_edit_card);
        setCancelable(false);
        ButterKnife.bind(this);

        frontText.setText(card.getFront());
        backText.setText(card.getBack());

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editCard();
            }
        });
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteCard();
            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancel();
            }
        });
    }

    private void deleteCard() {
        dataProvider.deleteCard(card);
    }

    private void editCard() {
        String front = frontText.getText().toString();
        String back = backText.getText().toString();

        card.setFront(front);
        card.setBack(back);

        dataProvider.updateCard(card);

        cancel();
    }
}
