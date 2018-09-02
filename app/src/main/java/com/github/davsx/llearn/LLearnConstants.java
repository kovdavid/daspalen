package com.github.davsx.llearn;

import java.util.ArrayList;

public class LLearnConstants {
    public static final Integer MAX_CARD_LEARN_SCORE = 10;
    public static final Integer LEARN_SESSION_CARDS = 10;
    public static final Integer LEARN_SESSION_CANDIDATE_CARDS = new Double (1.5 * LEARN_SESSION_CARDS).intValue();

    public static final String[] SPANISH_LOWERCASE_LETTERS = "abcdefghijklmnñopqrstuvwxyz".split("");
    public static final String[] SPANISH_UPPERCASE_LETTERS = "ABCDEFGHIJKLMNÑOPQRSTUVWXYZ".split("");
}