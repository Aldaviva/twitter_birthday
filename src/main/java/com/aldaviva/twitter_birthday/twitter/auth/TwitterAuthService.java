package com.aldaviva.twitter_birthday.twitter.auth;

import com.aldaviva.twitter_birthday.common.exceptions.TwitterException;
import com.aldaviva.twitter_birthday.twitter.data.TwitterSession;

public interface TwitterAuthService {

    TwitterSession getSession() throws TwitterException;

    TwitterSession startSession() throws TwitterException;
}
