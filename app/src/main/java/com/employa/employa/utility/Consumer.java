package com.employa.employa.utility;

/**
 * Consumer. Zoals de standaard lib consumer. Dit is manueel hierin gezet omdat de std lib implementatie
 * API level 24 nodig heeft.
 */
@FunctionalInterface
public interface Consumer<T> {
    void accept(T v);
}
