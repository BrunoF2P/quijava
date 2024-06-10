package org.quijava.quijava.models;

public enum TypeQuestion {
    Escolha_unica(0),
    Multipla_escolha(1);

    private final int value;

    TypeQuestion(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

}