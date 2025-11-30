package org.quijava.quijava.models;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class QuestionDifficultyConverter implements AttributeConverter<QuestionDifficulty, Integer> {

    @Override
    public Integer convertToDatabaseColumn(QuestionDifficulty difficulty) {
        return difficulty != null ? difficulty.getValue() : null;
    }

    @Override
    public QuestionDifficulty convertToEntityAttribute(Integer value) {
        return value != null ? QuestionDifficulty.fromValue(value) : null;
    }
}
