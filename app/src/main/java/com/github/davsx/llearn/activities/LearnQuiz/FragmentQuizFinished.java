package com.github.davsx.llearn.activities.LearnQuiz;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import com.github.davsx.llearn.R;

public class FragmentQuizFinished extends LearnQuizFragmentBase {

    private Button buttonConfirm;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_learn_quiz_finished, container, false);

        buttonConfirm = view.findViewById(R.id.button_confirm);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        buttonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                answerReceiver.onAnswer("");
            }
        });
    }
}
