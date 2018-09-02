package com.github.davsx.llearn.data.LearnCard;

import com.github.davsx.llearn.LLearnConstants;
import com.google.common.collect.Lists;

import java.util.*;

import static com.github.davsx.llearn.LLearnConstants.LEARN_CARD_KEYBOARD_COLUMNS;
import static com.github.davsx.llearn.LLearnConstants.SPANISH_LOWERCASE_LETTERS;

public class KeyboardKeyChooser {
    public static List<List<Character>> choose(String word) {
        Set<Character> uniqueChars = new HashSet<>();

        FOR_CHARS:
        for (char c : word.toCharArray()) {
            for (char testChar : SPANISH_LOWERCASE_LETTERS) {
                if (c == testChar) {
                    uniqueChars.add(c);
                    continue FOR_CHARS;
                }
            }
            for (char testChar : LLearnConstants.SPANISH_UPPERCASE_LETTERS) {
                if (c == testChar) {
                    uniqueChars.add(c);
                    continue FOR_CHARS;
                }
            }
        }

        int targetKeyNumber = LEARN_CARD_KEYBOARD_COLUMNS;
        if (uniqueChars.size() >= LEARN_CARD_KEYBOARD_COLUMNS - 1) {
            // If we have more than ~4 unique keys in word, then do 2 rows at least
            targetKeyNumber += LEARN_CARD_KEYBOARD_COLUMNS;
        }
        while (uniqueChars.size() > targetKeyNumber) {
            targetKeyNumber += LEARN_CARD_KEYBOARD_COLUMNS;
        }

        int maxIndex = SPANISH_LOWERCASE_LETTERS.length;
        Random rng = new Random();
        rng.setSeed(System.currentTimeMillis());

        while (uniqueChars.size() != targetKeyNumber) {
            int index = rng.nextInt(maxIndex);
            uniqueChars.add(SPANISH_LOWERCASE_LETTERS[index]);
        }


        ArrayList<Character> keys = new ArrayList<>();
        keys.addAll(uniqueChars);

        List<List<Character>> result = Lists.partition(keys, LEARN_CARD_KEYBOARD_COLUMNS);

        return result;
    }
}
