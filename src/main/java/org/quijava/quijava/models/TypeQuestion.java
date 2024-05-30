package org.quijava.quijava.models;

public enum TypeQuestion {
    ESCOLHA_UNICA(1),
    ESCOLHA_MULTIPLA(2);

    private final int value;

    TypeQuestion(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

}