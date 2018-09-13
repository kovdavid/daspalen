package com.github.davsx.llearn.activities.LearnQuiz;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import com.github.davsx.llearn.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public abstract class FragmentChoiceBase extends LearnQuizFragmentBase implements View.OnClickListener {
    protected ArrayList<Button> choiceButtons;
    protected TextView textViewQuiz;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(getViewResource(), container, false);

        textViewQuiz = view.findViewById(R.id.textview_quiz);
        choiceButtons = new ArrayList<>();
        choiceButtons.add((Button) view.findViewById(R.id.choice_1));
        choiceButtons.add((Button) view.findViewById(R.id.choice_2));
        choiceButtons.add((Button) view.findViewById(R.id.choice_3));
        choiceButtons.add((Button) view.findViewById(R.id.choice_4));

        for (Button btn : choiceButtons) {
            btn.setOnClickListener(this);
        }

        childViewInit(view);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        textViewQuiz.setText(getQuizString());

        List<String> choices = learnQuizData.getChoices();
        for (int i = 0; i < 4; i++) {
            Button btn = choiceButtons.get(i);
            btn.setText(choices.get(i));
        }
    }

    @Override
    public void onClick(View v) {
        Button btn = (Button) v;
        final String answer = btn.getText().toString();

        String correctAnswer = getCorrectAnswerString();

        int timer_delay = 2000;
        if (answer.equals(correctAnswer)) {
            timer_delay = 1000;
            btn.setBackgroundResource(R.drawable.button_choice_correct);
        } else {
            btn.setBackgroundResource(R.drawable.button_choice_incorrect);

            for (Button button : choiceButtons) {
                if (button.getText().toString().equals(correctAnswer)) {
                    button.setBackgroundResource(R.drawable.button_choice_correct);
                    button.setTextColor(Color.parseColor("#FFFFFF"));
                    break;
                }
            }
        }
        btn.setTextColor(Color.parseColor("#FFFFFF"));

        for (Button button : choiceButtons) {
            button.setOnClickListener(null);
        }

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                answerReceiver.onAnswer(answer);
            }
        }, timer_delay);
    }

    public abstract void childViewInit(View view);

    public abstract String getCorrectAnswerString();

    public abstract String getQuizString();

    public abstract int getViewResource();
}
