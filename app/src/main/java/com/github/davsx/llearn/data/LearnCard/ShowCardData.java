package com.github.davsx.llearn.data.LearnCard;

public class ShowCardData {

    private String frontText;
    private String backText;

    public ShowCardData(String frontText, String backText) {
        this.frontText = frontText;
        this.backText = backText;
    }

    public String getFrontText() {
        return frontText;
    }

    public String getBackText() {
        return backText;
    }
}
