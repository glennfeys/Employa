package com.employa.employa.repository;

/**
 * Abstract away the callback from internal db.
 */
public interface Callback<T> {
    /**
     * Called on success.
     *
     * @param t The result
     */
    void onSuccess(T t);

    /**
     * Called on fail.
     */
    default void onFail() {}
}
