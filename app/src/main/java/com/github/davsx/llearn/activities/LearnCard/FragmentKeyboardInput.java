package com.github.davsx.llearn.activities.LearnCard;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.github.davsx.llearn.R;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class FragmentKeyboardInput extends LearnCardFragmentBase implements View.OnClickListener {
    private TextView textViewFront;
    private EditText textViewInput;
    private Button buttonConfirm;
    private Button buttonBackspace;
    private TableLayout keyboardTable;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_learn_keyboard_input, container, false);

        textViewFront = rootView.findViewById(R.id.textview_front);
        textViewInput = rootView.findViewById(R.id.edittext_input);
        buttonConfirm = rootView.findViewById(R.id.button_confirm);
        buttonBackspace = rootView.findViewById(R.id.button_backspace);
        keyboardTable = rootView.findViewById(R.id.keyboard_table);

        buttonConfirm.setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        LayoutInflater inflater = getLayoutInflater();

        for (final List<Character> row : learnCardData.getKeyboardKeys()) {
            View keyboardRowView = inflater.inflate(R.layout.fragment_learn_keyboard_input_button_row, keyboardTable, false);
            LinearLayout linearLayout = keyboardRowView.findViewById(R.id.keyboard_row);
            for (int i = 0; i < row.size(); i++) {
                Button btn = (Button) linearLayout.getChildAt(i);
                btn.setText(row.get(i).toString());

                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String text = ((Button) v).getText().toString();
                        textViewInput.append(text);
                    }
                });
            }
            keyboardTable.addView(keyboardRowView);
        }

        View spaceBarView = inflater.inflate(R.layout.fragment_learn_keyboard_input_spacebar_row, keyboardTable, false);
        Button spaceBar = spaceBarView.findViewById(R.id.keyboard_spacebar);
        spaceBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textViewInput.append(" ");
            }
        });

        buttonBackspace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int length = textViewInput.getText().length();
                if (length > 0) {
                    textViewInput.getText().delete(length - 1, length);
                }
            }
        });

        keyboardTable.addView(spaceBarView);

        textViewFront.setText(learnCardData.getFrontText());
    }

    @Override
    public void onClick(View v) {
        final String answer = textViewInput.getText().toString();
        String correctAnswer = learnCardData.getBackText();

        int color;
        if (answer.equals(correctAnswer)) {
            color = ContextCompat.getColor(getContext(), R.color.colorGreen);
        } else {
            color = ContextCompat.getColor(getContext(), R.color.colorAccent);
        }
        textViewInput.setTextColor(color);

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                answerReceiver.onAnswer(answer);
            }
        }, 2000);
    }
}
