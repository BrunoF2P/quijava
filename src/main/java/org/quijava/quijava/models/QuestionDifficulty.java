package org.quijava.quijava.models;

public enum QuestionDifficulty {
    FACIL(1),
    MEDIANA(2),
    DIFICIL(3);

    private final int value;

    QuestionDifficulty(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
