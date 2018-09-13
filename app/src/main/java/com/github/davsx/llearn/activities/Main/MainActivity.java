package com.github.davsx.llearn.activities.Main;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.github.davsx.llearn.R;
import com.github.davsx.llearn.activities.LearnQuiz.LearnQuizActivity;
import com.github.davsx.llearn.activities.ManageCards.ManageCardsActivity;

import java.net.Inet4Address;
import java.util.Locale;

public class MainActivity extends Activity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(TAG, "onCreate");

        TextView textViewIpAddress = findViewById(R.id.textview_ip_address);
        Button btnWordList = findViewById(R.id.button_word_list);
        Button btnLearnCards = findViewById(R.id.button_learn_cards);
        Button btnCreateAnkiWord = findViewById(R.id.button_create_anki_card);
        Button btnTranslateSpanishDict = findViewById(R.id.button_translate_spanishdict);

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
                Intent i = new Intent(MainActivity.this, LearnQuizActivity.class);
                MainActivity.this.startActivity(i);
            }
        });

        btnCreateAnkiWord.setOnClickListener(new View.OnClickListener() {
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

        // To trigger installing TTS data
//        private void installVoiceData() {
//            Intent intent = new Intent(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            intent.setPackage("com.google.android.tts"/*replace with the package name of the target TTS engine*/);
//            try {
//                Log.v(TAG, "Installing voice data: " + intent.toUri(0));
//                startActivity(intent);
//            } catch (ActivityNotFoundException ex) {
//                Log.e(TAG, "Failed to install TTS data, no acitivty found for " + intent + ")");
//            }
//        }

        WifiManager wm = (WifiManager) (getApplicationContext().getSystemService(WIFI_SERVICE));
        String ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
        textViewIpAddress.setText(ip);

    }
}
