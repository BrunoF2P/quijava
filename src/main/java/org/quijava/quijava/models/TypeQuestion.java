package org.quijava.quijava.models;

public enum TypeQuestion {
    SINGLE_CHOICE(0),
    MULTIPLE_CHOICE(1);

    private final int value;

    TypeQuestion(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static TypeQuestion fromValue(int value) {
        for (TypeQuestion type : values()) {
            if (type.value == value) return type;
        }
        throw new IllegalArgumentException("Invalid value: " + value);
    }
}
