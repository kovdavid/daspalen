package com.github.davsx.llearn;

public class LLearnConstants {
    public static final Integer MAX_CARD_LEARN_SCORE = 8;

    public static final Integer LEARN_SESSION_MAX_CARDS = 10;
    public static final Integer LEARN_SESSION_MAX_ROUNDS = 20;
    public static final Integer LEARN_SESSION_MAX_NEW_CARDS = 5;
    public static final Integer LEARN_SESSION_CANDIDATE_CARDS = Double.valueOf(1.5 * LEARN_SESSION_MAX_CARDS).intValue();
    public static final Integer LEARN_CARD_KEYBOARD_COLUMNS = 5;
    public static final Integer LEARN_SESSION_RANDOM_CARDS_COUNT = 400;

    public static final Integer INTENT_REQUEST_CODE_CREATE_CARD = 1;
    public static final Integer INTENT_REQUEST_CODE_EDIT_CARD = 2;

    public static final char[] SPANISH_LOWERCASE_LETTERS = "abcdefghijklmnñopqrstuvwxyzáéíñóúü".toCharArray();
    public static final char[] SPANISH_UPPERCASE_LETTERS = "ABCDEFGHIJKLMNÑOPQRSTUVWXYZÁÉÍÑÓÚÜ".toCharArray();

    public static final String PKG_SPANISHDICT = "com.spanishdict.spanishdict";
    public static final String PKG_GOOGLE_TRANSLATE = "com.google.android.apps.translate";

    public static final Integer REQUEST_CODE_CREATE_DOCUMENT = 1;
    public static final Integer REQUEST_CODE_OPEN_DOCUMENT = 2;
}