package com.employa.employa.utility;

/**
 * Wrapper voor een type. Dit is een cheat om het probleem "variable referenced in lambda must be
 *   declared final or effectively final".
 * @param <T> Het type
 */
public final class Wrapper<T> {
    private T v;

    public Wrapper(T v) {
        this.v = v;
    }

    public T getValue() {
        return v;
    }

    public void setValue(T v) {
        this.v = v;
    }
}
