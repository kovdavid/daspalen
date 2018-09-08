package com.github.davsx.llearn.service.LearnCard;

import com.github.davsx.llearn.persistence.entity.CardEntity;

public class AnswerEvaluator {
    public static boolean isCorrect(CardTypeEnum type, CardEntity card, String answer) {
        if (type.equals(CardTypeEnum.SHOW_CARD)) {
            return true;
        }
        if (type.equals(CardTypeEnum.CHOICE_1of4) || type.equals(CardTypeEnum.KEYBOARD_INPUT)) {
            return answer.equals(card.getBack());
        }
        if (type.equals(CardTypeEnum.CHOICE_1of4_REVERSE)) {
            return answer.equals(card.getFront());
        }
        return true;
    }
}
