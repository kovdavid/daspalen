package com.github.davsx.llearn.gui.activities.LearnCard;

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

public class FragmentShowCardWithImage extends LearnCardFragmentBase {
    private TextView textViewFront;
    private TextView textViewBack;
    private ImageView imageView;
    private Button buttonNext;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_learn_show_card_with_image, container, false);

        textViewFront = view.findViewById(R.id.textview_front);
        textViewBack = view.findViewById(R.id.textview_back);
        buttonNext = view.findViewById(R.id.button_next);
        imageView = view.findViewById(R.id.image_view);

        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
        if (learnCardData.getImageUri() != null) {
            imageView.setImageURI(learnCardData.getImageUri());
        }
    }
}
