package com.github.davsx.llearn.service.BaseQuiz;

import android.net.Uri;
import android.util.Pair;
import com.github.davsx.llearn.LLearnConstants;
import com.github.davsx.llearn.persistence.entity.CardEntity;
import com.github.davsx.llearn.service.CardImage.CardImageService;
import com.google.common.collect.Lists;
import info.debatty.java.stringsimilarity.Levenshtein;

import java.util.*;

import static com.github.davsx.llearn.LLearnConstants.LEARN_CARD_KEYBOARD_COLUMNS;
import static com.github.davsx.llearn.LLearnConstants.SPANISH_LOWERCASE_LETTERS;

public class QuizData {
    private QuizTypeEnum quizType;
    private String frontText;
    private String backText;
    private Uri imageUri;
    private List<String> choices;
    private List<List<Character>> keyboardKeys;
    private Boolean isReversed = false;
    private Integer cardScore;

    public static QuizData buildFinishData() {
        QuizData data = new QuizData();
        data.setQuizType(QuizTypeEnum.QUIZ_FINISHED);
        return data;
    }

    public static QuizData build(QuizTypeEnum quizType, CardImageService cardImageService, CardEntity card, List<CardEntity> randomCards) {
        if (quizType.equals(QuizTypeEnum.NONE)) {
            return null;
        }
        QuizData data = new QuizData();
        data.setFrontText(card.getFront());
        data.setBackText(card.getBack());
        data.setQuizType(quizType);
        data.setCardScore(card.getLearnScore());
        if (quizType.equals(QuizTypeEnum.SHOW_CARD)) {
            String path = cardImageService.getCardImagePath(card.getId());
            if (path != null) {
                data.setImageUri(Uri.parse(path));
            }
        } else if (quizType.equals(QuizTypeEnum.CHOICE_1of4)) {
            data.setChoices(findChoicesFor(data, randomCards));
        } else if (quizType.equals(QuizTypeEnum.CHOICE_1of4_REVERSE)) {
            data.setReversed();
            data.setChoices(findChoicesFor(data, randomCards));
        } else if (quizType.equals(QuizTypeEnum.KEYBOARD_INPUT)) {
            data.setKeyboardKeys(findKeyboardKeysFor(card.getBack()));
        }
        return data;
    }

    private static List<String> findChoicesFor(QuizData data, List<CardEntity> randomCards) {
        String original = data.getReversed() ? data.getFrontText() : data.getBackText();
        ArrayList<Pair<Integer, String>> candidates = new ArrayList<>();
        for (CardEntity card : randomCards) {
            String candidate = data.getReversed() ? card.getFront() : card.getBack();

            Integer distance = Levenshtein.Distance(original, candidate);
            if (distance > 0) {
                candidates.add(new Pair<>(distance, candidate));
            }
        }

        // Sort by distance only. The actual candidate String can be in random order
        Collections.sort(candidates, new Comparator<Pair<Integer, String>>() {
            @Override
            public int compare(Pair<Integer, String> o1, Pair<Integer, String> o2) {
                return o1.first.compareTo(o2.first);
            }
        });

        Random rng = new Random(System.currentTimeMillis());
        List<String> result = new ArrayList<>();
        // We don't have enough candidates; fill it with empty String
        if (candidates.size() < 3) {
            for (Pair<Integer, String> candidate : candidates) {
                result.add(candidate.second);
            }
            while (result.size() != 3) {
                result.add("");
            }
            result.add(original);
        } else {
            Set<String> choices = new HashSet<>();
            while (choices.size() != 3) {
                int index = rng.nextInt(Math.min(candidates.size(), 10));
                choices.add(candidates.get(index).second);
            }

            result.addAll(choices);
            result.add(original);
        }

        Collections.shuffle(result, rng);

        return result;
    }

    private static List<List<Character>> findKeyboardKeysFor(String word) {
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
        Random rng = new Random(System.currentTimeMillis());

        while (uniqueChars.size() != targetKeyNumber) {
            int index = rng.nextInt(maxIndex);
            uniqueChars.add(SPANISH_LOWERCASE_LETTERS[index]);
        }

        ArrayList<Character> keys = new ArrayList<>(uniqueChars);
        Collections.shuffle(keys);

        return Lists.partition(keys, LEARN_CARD_KEYBOARD_COLUMNS);
    }

    public String getBackText() {
        return backText;
    }

    private void setReversed() {
        isReversed = true;
    }

    public List<String> getChoices() {
        return choices;
    }

    private void setBackText(String backText) {
        this.backText = backText;
    }

    public String getFrontText() {
        return frontText;
    }

    private void setCardScore(Integer cardScore) {
        this.cardScore = cardScore;
    }

    public Uri getImageUri() {
        return imageUri;
    }

    private void setChoices(List<String> choices) {
        this.choices = choices;
    }

    public List<List<Character>> getKeyboardKeys() {
        return keyboardKeys;
    }

    private void setFrontText(String frontText) {
        this.frontText = frontText;
    }

    public QuizTypeEnum getQuizType() {
        return quizType;
    }

    private void setImageUri(Uri imageUri) {
        this.imageUri = imageUri;
    }

    private Boolean getReversed() {
        return isReversed;
    }

    private void setKeyboardKeys(List<List<Character>> keyboardKeys) {
        this.keyboardKeys = keyboardKeys;
    }

    public Integer getCardScore() {
        return cardScore;
    }

    private void setQuizType(QuizTypeEnum learnQuizType) {
        this.quizType = learnQuizType;
    }
}
