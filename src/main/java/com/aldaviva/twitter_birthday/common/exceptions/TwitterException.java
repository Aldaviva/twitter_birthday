package com.aldaviva.twitter_birthday.common.exceptions;

public class TwitterException extends Exception {

    public TwitterException(final String message) {
        super(message);
    }

    public TwitterException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
