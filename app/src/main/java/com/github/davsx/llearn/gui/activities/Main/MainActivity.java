package com.github.davsx.llearn.gui.activities.Main;

import android.content.Intent;
import android.net.Uri;
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
    @BindView(R.id.button_translate_spanishdict) Button btnTranslateSpanishDict;

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

        btnTranslateSpanishDict.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setPackage("com.spanishdict.spanishdict");
                intent.setData(Uri.parse("http://www.spanishdict.com/translate/bread"));
                intent.addCategory(Intent.CATEGORY_DEFAULT);
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                startActivity(intent);
            }
        });

        // To get the origin of an intent (e.g. when we share the translation from SpanishDict back
        // to our app, use this.getReferrer().getHost() . It should return something like com.spanishdict.spanishdict
        //
        // for older Android:
        // ActivityManager am = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
        // List<ActivityManager.RecentTaskInfo> recentTasks = am.getRecentTasks(10000,ActivityManager.RECENT_WITH_EXCLUDED);
    }
}
