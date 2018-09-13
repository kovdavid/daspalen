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

public class FragmentShowCardWithImage extends LearnQuizFragmentBase {
    private TextView textViewFront;
    private TextView textViewBack;
    private ImageView imageView;
    private TextToSpeech tts;
    private Integer ttsStatus = TextToSpeech.ERROR;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_learn_show_card_with_image, container, false);

        textViewFront = view.findViewById(R.id.textview_front);
        textViewBack = view.findViewById(R.id.textview_back);
        imageView = view.findViewById(R.id.image_view);

        Button buttonNext = view.findViewById(R.id.button_next);
        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                answerReceiver.onAnswer(learnQuizData.getBackText());
            }
        });

        Button buttonTTS = view.findViewById(R.id.button_tts);
        buttonTTS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ttsStatus == TextToSpeech.SUCCESS) {
                    tts.speak(learnQuizData.getBackText(), TextToSpeech.QUEUE_FLUSH, null, "");
                }
            }
        });


        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        tts = new TextToSpeech(getContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                ttsStatus = status;
            }
        });
        tts.setLanguage(new Locale("es", "ES"));

        textViewFront.setText(learnQuizData.getFrontText());
        textViewBack.setText(learnQuizData.getBackText());
        if (learnQuizData.getImageUri() != null) {
            imageView.setImageURI(learnQuizData.getImageUri());
        }
    }
}
