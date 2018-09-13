package com.github.davsx.llearn.activities.LearnQuiz;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import com.github.davsx.llearn.R;

public class FragmentShowCard extends LearnQuizFragmentBase {
    private TextView textViewFront;
    private TextView textViewBack;
    private Button buttonNext;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_learn_show_card, container, false);

        textViewFront = view.findViewById(R.id.textview_front);
        textViewBack = view.findViewById(R.id.textview_back);
        buttonNext = view.findViewById(R.id.button_next);

        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // This fragment only shows the card, so we always send a correct answer
                answerReceiver.onAnswer(learnQuizData.getBackText());
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        textViewFront.setText(learnQuizData.getFrontText());
        textViewBack.setText(learnQuizData.getBackText());
    }
}
