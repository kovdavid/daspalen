package com.github.davsx.llearn.service.LearnCard;

import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

public class LearnCardData {

    private String frontText;
    private String backText;
    private Uri imageUri;
    private ArrayList<String> choices;
    private boolean isReversed = false;
    private List<List<Character>> keyboardKeys;

    LearnCardData(String frontText, String backText) {
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

    void setKeyboardKeys(List<List<Character>> keyboardKeys) {
        this.keyboardKeys = keyboardKeys;
    }

    public ArrayList<String> getChoices() {
        return choices;
    }

    void setChoices(ArrayList<String> choices) {
        this.choices = choices;
    }

    public boolean isReversed() {
        return isReversed;
    }

    void setReversed(boolean reversed) {
        this.isReversed = reversed;
    }

    public Uri getImageUri() {
        return imageUri;
    }

    public LearnCardData setImageUri(Uri imageUri) {
        this.imageUri = imageUri;
        return this;
    }
}
