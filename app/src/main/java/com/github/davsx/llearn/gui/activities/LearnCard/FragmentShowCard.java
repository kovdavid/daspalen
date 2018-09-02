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
import android.widget.Toast;
import com.github.davsx.llearn.R;
import com.github.davsx.llearn.data.LearnCard.ShowCardData;

public class FragmentShowCard extends Fragment {
    private TextView textViewFront;
    private TextView textViewBack;
    private Button buttonNext;

    private ShowCardData showCardData;
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
                answerReceiver.onAnswer(showCardData.getBackText());
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        textViewFront.setText(showCardData.getFrontText());
        textViewBack.setText(showCardData.getBackText());
    }

    public FragmentShowCard setAnswerReceiver(AnswerReceiver answerReceiver) {
        this.answerReceiver = answerReceiver;
        return this;
    }

    public FragmentShowCard setData(ShowCardData data) {
        this.showCardData = data;
        return this;
    }
}
