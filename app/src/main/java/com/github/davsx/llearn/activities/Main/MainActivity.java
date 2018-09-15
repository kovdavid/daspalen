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
import com.github.davsx.llearn.persistence.repository.CardRepository;

import javax.inject.Inject;

public class MainActivity extends Activity {

    private static final String TAG = "MainActivity";

    @Inject
    CardRepository cardRepository;

    private Button btnLearnCards;
    private Button btnManageCards;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ((LLearnApplication) getApplication()).getApplicationComponent().inject(this);

        btnManageCards = findViewById(R.id.button_manage_cards);
        btnLearnCards = findViewById(R.id.button_learn_cards);

        btnManageCards.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, ManageCardsActivity.class);
                MainActivity.this.startActivity(i);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        updateBtnLearnCards();
    }

    private void updateBtnLearnCards() {
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
    }
}
