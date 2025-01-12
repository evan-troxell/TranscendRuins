package com.transcendruins.utilities.finalize;

public interface Finalized {

    public void finalizeData();

    default void checkFinalized(boolean isFinalized) {

        if (isFinalized) {

            throw new UnsupportedOperationException("Data has been declared 'final'.");
        }
    }
}
