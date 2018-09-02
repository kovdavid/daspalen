package com.github.davsx.llearn.gui.activities.LearnCard;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import com.github.davsx.llearn.R;
import com.github.davsx.llearn.data.LearnCard.LearnCardData;

public class FragmentShowCard extends Fragment {
    private TextView textViewFront;
    private TextView textViewBack;
    private Button buttonNext;

    private LearnCardData learnCardData;
    private AnswerReceiver answerReceiver;

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
                answerReceiver.onAnswer(learnCardData.getBackText());
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        textViewFront.setText(learnCardData.getFrontText());
        textViewBack.setText(learnCardData.getBackText());
    }

    public FragmentShowCard setAnswerReceiver(AnswerReceiver answerReceiver) {
        this.answerReceiver = answerReceiver;
        return this;
    }

    public FragmentShowCard setData(LearnCardData data) {
        this.learnCardData = data;
        return this;
    }
}
