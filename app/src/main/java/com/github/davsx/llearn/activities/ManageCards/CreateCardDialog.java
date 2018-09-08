package com.github.davsx.llearn.activities.ManageCards;

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
import com.github.davsx.llearn.service.ManageCards.ManageCardsService;
import com.github.davsx.llearn.persistence.entity.CardEntity;

public class CreateCardDialog extends Dialog {

    @BindView(R.id.front_text)
    EditText frontText;
    @BindView(R.id.back_text)
    EditText backText;
    @BindView(R.id.dialog_cancel)
    Button cancelButton;
    @BindView(R.id.dialog_confirm)
    Button confirmButton;

    private ManageCardsService dataProvider;

    public CreateCardDialog(@NonNull Context context, ManageCardsService dataProvider) {
        super(context);
        this.dataProvider = dataProvider;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_create_card);
        setCancelable(false);
        ButterKnife.bind(this);

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createCard();
            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancel();
            }
        });
    }

    private void createCard() {
        String front = frontText.getText().toString();
        String back = backText.getText().toString();

        CardEntity card = new CardEntity()
                .setCreatedAt(System.currentTimeMillis())
                .setFront(front)
                .setBack(back);

        dataProvider.createCard(card);

        cancel();
    }
}
