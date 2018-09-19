package com.github.davsx.llearn.activities.LearnQuiz;

import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.github.davsx.llearn.R;

import java.util.Locale;

public class FragmentShowCard extends LearnQuizFragmentBase {
    private TextView textViewFront;
    private TextView textViewBack;
    private TextView textViewCardScore;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_learn_show_card, container, false);

        textViewFront = view.findViewById(R.id.textview_front);
        textViewBack = view.findViewById(R.id.textview_back);
        textViewCardScore = view.findViewById(R.id.textview_card_score);

        Button buttonNext = view.findViewById(R.id.button_next);
        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                answerReceiver.onAnswer(learnQuizData.getBackText());
            }
        });

        ImageView imageViewTTS = view.findViewById(R.id.button_tts);
        imageViewTTS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speakerService.speak(learnQuizData.getBackText());
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        textViewFront.setText(learnQuizData.getFrontText());
        textViewBack.setText(learnQuizData.getBackText());
        textViewCardScore.setText(learnQuizData.getCardScore().toString());
    }
}
