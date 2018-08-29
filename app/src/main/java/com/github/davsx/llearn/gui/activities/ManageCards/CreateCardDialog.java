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
import com.github.davsx.llearn.persistence.entity.CardEntityBuilder;

public class CreateCardDialog extends Dialog {

    @BindView(R.id.front_text)
    EditText frontText;
    @BindView(R.id.back_text)
    EditText backText;
    @BindView(R.id.dialog_cancel)
    Button cancelButton;
    @BindView(R.id.dialog_confirm)
    Button confirmButton;

    private ManageCardsDataProvider dataProvider;

    public CreateCardDialog(@NonNull Context context, ManageCardsDataProvider dataProvider) {
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

        CardEntity card = new CardEntityBuilder()
                .setCreatedTimestamp(System.currentTimeMillis())
                .setFront(front)
                .setBack(back)
                .createCardEntity();

        dataProvider.createCard(card);

        cancel();
    }
}
