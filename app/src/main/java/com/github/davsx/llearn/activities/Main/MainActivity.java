package com.github.davsx.llearn.activities.Main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.github.davsx.llearn.LLearnApplication;
import com.github.davsx.llearn.R;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        ((LLearnApplication) getApplication()).getApplicationComponent().inject(this);

        btnManageCards = findViewById(R.id.button_manage_cards);
        btnLearnCards = findViewById(R.id.button_learn_cards);
        btnResetCards = findViewById(R.id.button_reset_cards);
    }

    @Override
    protected void onResume() {
        super.onResume();

        updateButtons();
    }

    private void updateButtons() {
        Integer learnableCardCount = cardRepository.learnableCardCount();
        btnLearnCards.setText("Learn new cards (" + String.valueOf(learnableCardCount) + ")");
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
