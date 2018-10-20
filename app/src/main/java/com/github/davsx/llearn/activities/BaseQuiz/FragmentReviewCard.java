package com.github.davsx.llearn.activities.BaseQuiz;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.github.davsx.llearn.R;
import com.github.davsx.llearn.service.ReviewQuiz.ReviewQuizService;

import java.util.Timer;
import java.util.TimerTask;

public class FragmentReviewCard extends BaseQuizFragment {
    private ViewGroup viewHolder;
    private TextView textViewFront;
    private ImageView imageView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_review_card, container, false);

        textViewFront = view.findViewById(R.id.textview_front);
        viewHolder = view.findViewById(R.id.view_holder);

        View subView = inflater.inflate(R.layout.fragment_review_card_layout1, viewHolder, false);
        subView.findViewById(R.id.button_check_answer).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswerClicked();
            }
        });

        imageView = subView.findViewById(R.id.image_view);

        viewHolder.addView(subView);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        textViewFront.setText(quizData.getFrontText());
        if (quizData.getImageUri() != null) {
            imageView.setImageURI(quizData.getImageUri());
        }
    }

    private void checkAnswerClicked() {
        View view = getLayoutInflater().inflate(R.layout.fragment_review_card_layout2, viewHolder, false);
        TextView textViewBack = view.findViewById(R.id.textview_back);
        textViewBack.setText(quizData.getBackText());

        view.findViewById(R.id.button_answer_wrong).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleAnswer(ReviewQuizService.ANSWER_WRONG);
            }
        });
        view.findViewById(R.id.button_answer_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleAnswer(ReviewQuizService.ANSWER_OK);
            }
        });
        view.findViewById(R.id.button_answer_good).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleAnswer(ReviewQuizService.ANSWER_GOOD);
            }
        });

        viewHolder.removeAllViews();
        viewHolder.addView(view);
    }

    private void handleAnswer(final String answer) {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                answerReceiver.onAnswer(answer);
            }
        }, 1000);
    }
}
