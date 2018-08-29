package com.github.davsx.llearn.persistence.entity;

public class CardEntityBuilder {
    private Long id;
    private String front;
    private String back;
    private Integer backWordCount;
    private Integer backLength;
    private Integer learnScore = 0;
    private Long createdTimestamp;
    private Long lastReviewTimestamp;

    public CardEntityBuilder setId(Long id) {
        this.id = id;
        return this;
    }

    public CardEntityBuilder setFront(String front) {
        this.front = front;
        return this;
    }

    public CardEntityBuilder setBack(String back) {
        this.back = back;
        this.backLength = back.length();
        this.backWordCount = back.split("\\s+").length;
        return this;
    }

    public CardEntityBuilder setLearnScore(Integer learnScore) {
        this.learnScore = learnScore;
        return this;
    }

    public CardEntityBuilder setCreatedTimestamp(Long createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
        return this;
    }

    public CardEntityBuilder setLastReviewTimestamp(Long lastReviewTimestamp) {
        this.lastReviewTimestamp = lastReviewTimestamp;
        return this;
    }

    public CardEntity createCardEntity() {
        return new CardEntity(id, front, back, backWordCount, backLength, learnScore, createdTimestamp, lastReviewTimestamp);
    }
}