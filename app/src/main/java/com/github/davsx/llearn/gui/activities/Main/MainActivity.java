package com.github.davsx.llearn.gui.activities.Main;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.github.davsx.llearn.R;
import com.github.davsx.llearn.gui.activities.BaseActivity;
import com.github.davsx.llearn.gui.activities.LearnCard.LearnCardActivity;
import com.github.davsx.llearn.gui.activities.ManageCards.ManageCardsActivity;

public class MainActivity extends BaseActivity {
    @BindView(R.id.button_word_list) Button btnWordList;
    @BindView(R.id.button_create_anki_card) Button createAnkiWordButton;
    @BindView(R.id.button_learn_cards) Button btnLearnCards;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        btnWordList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, ManageCardsActivity.class);
                MainActivity.this.startActivity(i);
            }
        });

        btnLearnCards.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, LearnCardActivity.class);
                MainActivity.this.startActivity(i);
            }
        });

        createAnkiWordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sendIntent = new Intent();
                sendIntent.setAction("org.openintents.action.CREATE_FLASHCARD");
                sendIntent.putExtra("SOURCE_TEXT", "Testing source text");
                sendIntent.putExtra("TARGET_TEXT", "Testing target text");
                startActivity(sendIntent);
            }
        });
    }
}
