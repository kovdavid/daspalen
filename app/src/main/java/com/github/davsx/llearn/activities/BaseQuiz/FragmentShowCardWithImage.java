package com.github.davsx.llearn.activities.BaseQuiz;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.github.davsx.llearn.R;

public class FragmentShowCardWithImage extends BaseQuizFragment {
    private TextView textViewFront;
    private TextView textViewBack;
    private TextView textViewCardScore;
    private ImageView imageView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_learn_show_card_with_image, container, false);

        textViewFront = view.findViewById(R.id.textview_front);
        textViewBack = view.findViewById(R.id.textview_back);
        textViewCardScore = view.findViewById(R.id.textview_card_score);
        imageView = view.findViewById(R.id.image_view);

        Button buttonNext = view.findViewById(R.id.button_next);
        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                answerReceiver.onAnswer(quizData.getBackText());
            }
        });

        view.findViewById(R.id.button_tts).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speakerService.speak(quizData.getBackText());
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        textViewFront.setText(quizData.getFrontText());
        textViewBack.setText(quizData.getBackText());
        textViewCardScore.setText(quizData.getCardScore().toString());
        if (quizData.getImageUri() != null) {
            imageView.setImageURI(quizData.getImageUri());
        }
    }
}
