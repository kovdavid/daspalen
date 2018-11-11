package com.github.davsx.daspalen.activities.BaseQuiz;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.widget.ProgressBar;
import com.github.davsx.daspalen.R;
import com.github.davsx.daspalen.service.BaseQuiz.AnswerReceiver;
import com.github.davsx.daspalen.service.BaseQuiz.CardQuizService;
import com.github.davsx.daspalen.service.BaseQuiz.QuizData;
import com.github.davsx.daspalen.service.BaseQuiz.QuizTypeEnum;
import com.github.davsx.daspalen.service.Speaker.SpeakerService;

public abstract class BaseQuizActivity extends FragmentActivity implements AnswerReceiver {
    private CardQuizService cardQuizService;
    private SpeakerService speakerService;
    private ProgressBar progressBar;

    protected void startQuiz() {
        if (cardQuizService.startSession()) {
            progressBar.setMax(cardQuizService.getTotalRounds());
            showNextFragment();
        } else {
            finish();
        }
    }

    private void showNextFragment() {
        progressBar.setProgress(cardQuizService.getCompletedRounds());

        QuizData data = cardQuizService.getNextCardData();
        if (data == null) {
            finish();
            return;
        }

        QuizTypeEnum quizType = data.getQuizType();

        BaseQuizFragment fragment = null;
        if (quizType.equals(QuizTypeEnum.QUIZ_FINISHED)) {
            fragment = new FragmentQuizFinished();
        } else if (quizType.equals(QuizTypeEnum.SHOW_CARD)) {
            fragment = new FragmentShowCard();
        } else if (quizType.equals(QuizTypeEnum.KEYBOARD_INPUT)) {
            fragment = new FragmentKeyboardInput();
        } else if (quizType.equals(QuizTypeEnum.CHOICE_1of4)) {
            fragment = new FragmentChoice1of4();
        } else if (quizType.equals(QuizTypeEnum.CHOICE_1of4_REVERSE)) {
            fragment = new FragmentChoice1of4Reverse();
        } else if (quizType.equals(QuizTypeEnum.REVIEW_CARD)) {
            fragment = new FragmentReviewCard();
        }

        if (fragment == null) {
            finish();
        } else {
            fragment.setAnswerReceiver(this);
            fragment.setSpeakerService(speakerService);
            fragment.setData(data);
            renderFragment(fragment);
        }
    }

    private void renderFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_card, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onAnswer(String answer) {
        cardQuizService.processAnswer(answer);
        showNextFragment();
    }

    protected void setCardQuizService(CardQuizService cardQuizService) {
        this.cardQuizService = cardQuizService;
    }

    protected void setProgressBar(ProgressBar progressBar) {
        this.progressBar = progressBar;
    }

    protected void setSpeakerService(SpeakerService speakerService) {
        this.speakerService = speakerService;
    }
}
