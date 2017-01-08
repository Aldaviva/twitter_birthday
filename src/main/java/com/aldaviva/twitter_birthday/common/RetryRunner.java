package com.aldaviva.twitter_birthday.common;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

public class RetryRunner {

    private RetryRunner(){}

    public static <T> T runUntilNoExceptions(final int maxAttempts, final Callable<T> callable) throws ExecutionException {
        for(int attemptNum = 1; attemptNum <= maxAttempts; attemptNum++){
            try {
                return callable.call();
            } catch(final Exception e){
                if(attemptNum == maxAttempts) {
                    throw new ExecutionException("Failed after " + attemptNum + " attempts", e);
                }
            }
        }
        return null; // This is unreachable.
    }

}
