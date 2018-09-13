package com.github.davsx.llearn.activities.LearnQuiz;

import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.Button;
import com.github.davsx.llearn.R;

import java.util.Locale;

public class FragmentChoice1of4Reverse extends FragmentChoiceBase {
    private TextToSpeech tts;
    private Integer ttsStatus = TextToSpeech.ERROR;

    @Override
    public void childViewInit(View view) {
        tts = new TextToSpeech(getContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                ttsStatus = status;
            }
        });
        tts.setLanguage(new Locale("es", "ES"));

        Button buttonTTS = view.findViewById(R.id.button_tts);
        buttonTTS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ttsStatus == TextToSpeech.SUCCESS) {
                    tts.speak(learnQuizData.getBackText(), TextToSpeech.QUEUE_FLUSH, null, "");
                }
            }
        });
    }

    @Override
    public String getCorrectAnswerString() {
        return learnQuizData.getFrontText();
    }

    @Override
    public String getQuizString() {
        return learnQuizData.getBackText();
    }

    @Override
    public int getViewResource() {
        return R.layout.fragment_learn_choice_1of4_reverse;
    }
}