package com.github.davsx.llearn.service.LearnCard;

import android.net.Uri;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class LearnCardData {
    private static final String TAG = "LearnCardData";

    private String frontText;
    private String backText;
    private Uri imageUri;
    private ArrayList<String> choices;
    private boolean guessBack = true;
    private List<List<Character>> keyboardKeys;

    public LearnCardData(String frontText, String backText) {
        Log.d(TAG, "creating object");
        this.frontText = frontText;
        this.backText = backText;
    }

    public String getFrontText() {
        return frontText;
    }

    public String getBackText() {
        return backText;
    }

    public List<List<Character>> getKeyboardKeys() {
        return keyboardKeys;
    }

    public LearnCardData setChoices(ArrayList<String> choices) {
        this.choices = choices;
        return this;
    }

    public ArrayList<String> getChoices() {
        return choices;
    }

    public LearnCardData setKeyboardKeys(List<List<Character>> keyboardKeys) {
        this.keyboardKeys = keyboardKeys;
        return this;
    }

    public boolean isGuessBack() {
        return guessBack;
    }

    public LearnCardData setGuessBack(boolean guessBack) {
        this.guessBack = guessBack;
        return this;
    }

    public Uri getImageUri() {
        return imageUri;
    }

    public LearnCardData setImageUri(Uri imageUri) {
        this.imageUri = imageUri;
        return this;
    }
}