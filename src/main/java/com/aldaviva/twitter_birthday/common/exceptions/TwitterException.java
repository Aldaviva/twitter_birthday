package com.aldaviva.twitter_birthday.common.exceptions;

public class TwitterException extends Exception {

	private static final long serialVersionUID = 1L;

	public TwitterException(final String message) {
		super(message);
	}

	public TwitterException(final String message, final Throwable cause) {
		super(message, cause);
	}
}
