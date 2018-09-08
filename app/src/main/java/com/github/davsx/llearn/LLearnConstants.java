package com.github.davsx.llearn;

import java.util.Arrays;
import java.util.List;

public class LLearnConstants {
    public static final Integer MAX_CARD_LEARN_SCORE = 8;
    public static final List<Integer> LEARN_CARD_PLANNED_USAGES = Arrays.asList(3, 3, 3, 3, 2, 2, 2, 1, 1);

    public static final Integer LEARN_SESSION_MAX_CARDS = 10;
    public static final Integer LEARN_SESSION_MAX_ROUNDS = 20;
    public static final Integer LEARN_SESSION_MAX_NEW_CARDS = 5;
    public static final Integer LEARN_SESSION_CANDIDATE_CARDS = Double.valueOf(1.5 * LEARN_SESSION_MAX_CARDS).intValue();
    public static final Integer LEARN_CARD_KEYBOARD_COLUMNS = 5;
    public static final Integer LEARN_SESSION_RANDOM_CARDS_COUNT = 400;

    public static final char[] SPANISH_LOWERCASE_LETTERS = "abcdefghijklmnñopqrstuvwxyzáéíñóúü".toCharArray();
    public static final char[] SPANISH_UPPERCASE_LETTERS = "ABCDEFGHIJKLMNÑOPQRSTUVWXYZÁÉÍÑÓÚÜ".toCharArray();

}