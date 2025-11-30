package org.quijava.quijava.models;

public enum QuestionDifficulty {
    EASY(1),
    MEDIUM(2),
    HARD(3);

    private final int value;

    QuestionDifficulty(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static QuestionDifficulty fromValue(int value) {
        for (QuestionDifficulty difficulty : values()) {
            if (difficulty.value == value) return difficulty;
        }
        throw new IllegalArgumentException("Invalid difficulty value: " + value);
    }
}

