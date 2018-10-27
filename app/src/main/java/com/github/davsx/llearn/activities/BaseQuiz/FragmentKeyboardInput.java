package com.github.davsx.llearn.activities.BaseQuiz;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.github.davsx.llearn.R;

import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class FragmentKeyboardInput extends BaseQuizFragment implements View.OnClickListener {
    private TextView textViewFront;
    private TextView textViewCardScore;
    private EditText editTextInput;
    private Button buttonSpace;
    private Button buttonBackspace;
    private Button buttonConfirm;
    private LinearLayout layoutKeyboard;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_learn_keyboard_input, container, false);

        textViewFront = rootView.findViewById(R.id.textview_front);
        editTextInput = rootView.findViewById(R.id.edittext_input);
        textViewCardScore = rootView.findViewById(R.id.textview_card_score);
        buttonBackspace = rootView.findViewById(R.id.button_backspace);
        buttonSpace = rootView.findViewById(R.id.keyboard_spacebar);
        layoutKeyboard = rootView.findViewById(R.id.layout_keyboard);

        buttonConfirm = rootView.findViewById(R.id.button_confirm);
        buttonConfirm.setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        LayoutInflater inflater = getLayoutInflater();

        List<Integer> keyResources = Arrays.asList(
                R.id.keyboard_button1,
                R.id.keyboard_button2,
                R.id.keyboard_button3,
                R.id.keyboard_button4,
                R.id.keyboard_button5
        );

        layoutKeyboard.removeAllViews();

        for (final List<Character> row : quizData.getKeyboardKeys()) {
            View keyboardRowView = inflater.inflate(
                    R.layout.fragment_learn_keyboard_input_button_row, layoutKeyboard, false);
            for (int i = 0; i < row.size(); i++) {
                Button btn = keyboardRowView.findViewById(keyResources.get(i));
                btn.setText(row.get(i).toString());

                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int cursorPosition = editTextInput.getSelectionStart();
                        String text = ((Button) v).getText().toString();
                        editTextInput.getText().insert(cursorPosition, text);
//                        editTextInput.append(text);
                    }
                });
            }
            layoutKeyboard.addView(keyboardRowView);
        }

        buttonSpace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editTextInput.getText().append(" ");
            }
        });

        buttonBackspace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int length = editTextInput.getText().length();
                if (length > 0) {
                    int cursorStart = editTextInput.getSelectionStart();
                    int cursorEnd = editTextInput.getSelectionEnd();
                    editTextInput.getText().delete(cursorStart, cursorEnd);
//                    editTextInput.getText().delete(length - 1, length);
                }
            }
        });

        textViewCardScore.setText(quizData.getCardScore().toString());
        textViewFront.setText(quizData.getFrontText());
    }

    @Override
    public void onClick(View v) {
        final String answer = editTextInput.getText().toString();

        if (answer.equals("")) {
            return;
        }

        String correctAnswer = quizData.getBackText();

        int color;
        int timerInterval = 2000;
        if (answer.equals(correctAnswer)) {
            timerInterval = 1000;
            color = ContextCompat.getColor(getContext(), R.color.colorGreen);
        } else {
            color = ContextCompat.getColor(getContext(), R.color.colorAccent);
        }
        editTextInput.setTextColor(color);

        buttonConfirm.setOnClickListener(null);

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                answerReceiver.onAnswer(answer);
            }
        }, timerInterval);
    }
}
