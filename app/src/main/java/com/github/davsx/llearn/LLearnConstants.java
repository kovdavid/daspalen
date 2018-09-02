package com.github.davsx.llearn;

public class LLearnConstants {
    public static final Integer MAX_CARD_LEARN_SCORE = 10;
    public static final Integer LEARN_SESSION_CARDS = 10;
    public static final Integer LEARN_SESSION_CANDIDATE_CARDS = Double.valueOf(1.5 * LEARN_SESSION_CARDS).intValue();

    public static final char[] SPANISH_LOWERCASE_LETTERS = "abcdefghijklmnñopqrstuvwxyzáéíñóúü".toCharArray();
    public static final char[] SPANISH_UPPERCASE_LETTERS = "ABCDEFGHIJKLMNÑOPQRSTUVWXYZÁÉÍÑÓÚÜ".toCharArray();

    public static final Integer LEARN_CARD_KEYBOARD_COLUMNS = 5;
}