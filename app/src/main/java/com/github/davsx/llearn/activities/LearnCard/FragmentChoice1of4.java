package com.github.davsx.llearn.activities.LearnCard;

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
import java.util.Timer;
import java.util.TimerTask;

public class FragmentChoice1of4 extends LearnCardFragmentBase implements View.OnClickListener {
    private TextView textViewFront;
    private ArrayList<Button> choiceButtons;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_learn_choice_1_of_4, container, false);

        textViewFront = view.findViewById(R.id.textview_front);
        choiceButtons = new ArrayList<>();
        choiceButtons.add((Button) view.findViewById(R.id.choice_1));
        choiceButtons.add((Button) view.findViewById(R.id.choice_2));
        choiceButtons.add((Button) view.findViewById(R.id.choice_3));
        choiceButtons.add((Button) view.findViewById(R.id.choice_4));

        for (Button btn : choiceButtons) {
            btn.setOnClickListener(this);
        }

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        String guess;
        if (learnCardData.isReversed()) {
            guess = learnCardData.getFrontText();
        } else {
            guess = learnCardData.getBackText();
        }
        textViewFront.setText(guess);

        ArrayList<String> choices = learnCardData.getChoices();
        for (int i = 0; i < 4; i++) {
            Button btn = choiceButtons.get(i);
            btn.setText(choices.get(i));
        }
    }

    @Override
    public void onClick(View v) {
        Button btn = (Button) v;
        final String answer = btn.getText().toString();

        String correctAnswer;
        if (learnCardData.isReversed()) {
            correctAnswer = learnCardData.getBackText();
        } else {
            correctAnswer = learnCardData.getFrontText();
        }

        if (answer.equals(correctAnswer)) {
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
        }, 2000);
    }
}
