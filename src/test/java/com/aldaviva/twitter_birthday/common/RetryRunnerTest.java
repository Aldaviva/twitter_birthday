package com.aldaviva.twitter_birthday.common;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;

import static org.testng.Assert.assertEquals;

public class RetryRunnerTest {

    private final AtomicInteger attemptNum = new AtomicInteger(); //1-indexed

    @BeforeMethod
    private void reset() {
        attemptNum.set(0);
    }

    @Test
    public void succeedsFirstTime() throws ExecutionException {
        RetryRunner.runUntilNoExceptions(5, new Callable<String>() {
            @Override
            public String call() throws Exception {
                attemptNum.incrementAndGet();
                return "hi";
            }
        });
        assertEquals(attemptNum.get(), 1);
    }


    @Test
    public void retriesOnce() throws ExecutionException {
        RetryRunner.runUntilNoExceptions(5, new Callable<String>() {
            @Override
            public String call() throws Exception {
                if (attemptNum.incrementAndGet() == 2) {
                    return "hi";
                } else {
                    throw new RuntimeException("not going to succeed yet");
                }
            }
        });
        assertEquals(attemptNum.get(), 2);
    }

    @Test
    public void retriesMaxTimes() throws ExecutionException {
        RetryRunner.runUntilNoExceptions(5, new Callable<String>() {
            @Override
            public String call() throws Exception {
                if (attemptNum.incrementAndGet() == 5) {
                    return "hi";
                } else {
                    throw new RuntimeException("not going to succeed yet");
                }
            }
        });
        assertEquals(attemptNum.get(), 5);
    }

    @Test(expectedExceptions = {ExecutionException.class})
    public void neverSucceeds() throws ExecutionException {
        RetryRunner.runUntilNoExceptions(5, new Callable<String>() {
            @Override
            public String call() throws Exception {
                attemptNum.incrementAndGet();
                throw new RuntimeException("not going to succeed yet");
            }
        });
        assertEquals(attemptNum.get(), 5);
    }

}
