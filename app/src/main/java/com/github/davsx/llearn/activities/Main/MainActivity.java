package com.github.davsx.llearn.activities.Main;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.github.davsx.llearn.LLearnApplication;
import com.github.davsx.llearn.R;
import com.github.davsx.llearn.activities.CardExport.CardExportActivity;
import com.github.davsx.llearn.activities.CardImport.CardImportActivity;
import com.github.davsx.llearn.activities.LearnQuiz.LearnQuizActivity;
import com.github.davsx.llearn.activities.ManageCards.ManageCardsActivity;
import com.github.davsx.llearn.persistence.entity.CardEntity;
import com.github.davsx.llearn.persistence.repository.CardRepository;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends Activity {

    private static final String TAG = "MainActivity";

    @Inject
    CardRepository cardRepository;

    private Button btnLearnCards;
    private Button btnManageCards;
    private Button btnResetCards;
    private Button btnExportCards;
    private Button btnImportCards;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        ((LLearnApplication) getApplication()).getApplicationComponent().inject(this);

        btnManageCards = findViewById(R.id.button_manage_cards);
        btnLearnCards = findViewById(R.id.button_learn_cards);
        btnResetCards = findViewById(R.id.button_reset_cards);
        btnExportCards = findViewById(R.id.button_export_cards);
        btnImportCards = findViewById(R.id.button_import_cards);

        findViewById(R.id.button_test).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_TEXT, "car seat");
                intent.putExtra("key_text_input", "car seat");
                intent.putExtra("key_text_output", "");
                intent.putExtra("key_language_from", "en");
                intent.putExtra("key_language_to", "es");
                intent.putExtra("key_suggest_translation", "");
                intent.putExtra("key_from_floating_window", false);
                intent.setComponent(new ComponentName(
                        "com.google.android.apps.translate",
                        "com.google.android.apps.translate.TranslateActivity"));
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        updateButtons();
    }

    private void updateButtons() {
        Integer learnableCardCount = cardRepository.learnableCardCount();
        btnLearnCards.setText("Learn new cards (" + Integer.toString(learnableCardCount) + ")");
        if (learnableCardCount > 0) {
            btnLearnCards.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(MainActivity.this, LearnQuizActivity.class);
                    MainActivity.this.startActivity(i);
                }
            });
        } else {
            btnLearnCards.setOnClickListener(null);
        }

        btnManageCards.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, ManageCardsActivity.class);
                MainActivity.this.startActivity(i);
            }
        });

        btnExportCards.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, CardExportActivity.class);
                MainActivity.this.startActivity(i);
            }
        });

        btnImportCards.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, CardImportActivity.class);
                MainActivity.this.startActivity(i);
            }
        });

        btnResetCards.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cardRepository.deleteAllCards();
                List<String> strings = Arrays.asList("aaaa", "bbbb", "cccc", "dddd", "eeee", "ffff", "gggg", "hhhh", "jjjj", "kkkk");

                List<CardEntity> cards = new ArrayList<>();
                for (int i = 0; i < 1000; i++) {
                    for (String string : strings) {
                        CardEntity card = new CardEntity();
                        String text = string + String.valueOf(i);
                        card.setFront(text);
                        if (i % 2 == 0) {
                            card.setBack(text);
                        }
                        card.setCreatedAt(System.currentTimeMillis());
                        cards.add(card);
                    }
                }
                cardRepository.saveMany(cards);
                updateButtons();
            }
        });
    }
}
