package com.github.davsx.llearn.activities.Main;

import android.app.Activity;
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
import com.github.davsx.llearn.activities.MemriseImport.MemriseImportActivity;
import com.github.davsx.llearn.activities.ReviewQuiz.ReviewQuizActivity;
import com.github.davsx.llearn.activities.Settings.SettingsActivity;
import com.github.davsx.llearn.persistence.entity.CardEntity;
import com.github.davsx.llearn.persistence.repository.CardRepository;
import com.github.davsx.llearn.persistence.repository.JournalRepository;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends Activity {

    private static final String TAG = "MainActivity";

    @Inject
    CardRepository cardRepository;
    @Inject
    JournalRepository journalRepository;

    private Button btnLearnCards;
    private Button btnReviewCards;
    private Button btnManageCards;
    private Button btnResetCards;
    private Button btnExportCards;
    private Button btnImportCards;
    private Button btnSettings;
    private Button btnMemriseImport;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        ((LLearnApplication) getApplication()).getApplicationComponent().inject(this);

        btnManageCards = findViewById(R.id.button_manage_cards);
        btnLearnCards = findViewById(R.id.button_learn_cards);
        btnReviewCards = findViewById(R.id.button_review_cards);
        btnResetCards = findViewById(R.id.button_reset_cards);
        btnExportCards = findViewById(R.id.button_export_cards);
        btnImportCards = findViewById(R.id.button_import_cards);
        btnSettings = findViewById(R.id.button_settings);
        btnMemriseImport = findViewById(R.id.button_memrise_import);
    }

    @Override
    protected void onResume() {
        super.onResume();

        updateButtons();
    }

    private void updateButtons() {
        Integer learnableCardCount = cardRepository.learnableCardCount();
        btnLearnCards.setText("Learn cards (" + Integer.toString(learnableCardCount) + ")");
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

        Integer allCardsCount = cardRepository.allCardsCount();
        btnManageCards.setText("Manage cards (" + Integer.toString(allCardsCount) + ")");
        btnManageCards.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, ManageCardsActivity.class);
                MainActivity.this.startActivity(i);
            }
        });

        Integer reviewableCardCount = cardRepository.reviewableCardCount();
        btnReviewCards.setText("Review cards (" + Integer.toString(reviewableCardCount) + ")");
        btnReviewCards.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, ReviewQuizActivity.class);
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

        btnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, SettingsActivity.class);
                MainActivity.this.startActivity(i);
            }
        });

        btnMemriseImport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, MemriseImportActivity.class);
                MainActivity.this.startActivity(i);
            }
        });

        btnResetCards.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                Intent i = new Intent();
//                i.setAction(LLearnConstants.WORD_OF_THE_DAY_INTENT);
//                WordOfTheDayNotificationService s = new WordOfTheDayNotificationService();
//                s.onReceive(getApplicationContext(), i);

                cardRepository.deleteAllCards();
                journalRepository.deleteAllJournals();
                List<String> strings = Arrays.asList("aaaa", "bbbb", "cccc", "dddd", "eeee", "ffff", "gggg", "hhhh", "jjjj", "kkkk");

                List<CardEntity> cards = new ArrayList<>();
                for (int i = 0; i < 1; i++) {
                    for (String string : strings) {
                        CardEntity card = new CardEntity();
                        String text = string; // + String.valueOf(i);
                        card.setFront(text);
                        if (i % 2 == 0) {
                            card.setBack(text);
                        }
                        card.setLearnScore(7);
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
