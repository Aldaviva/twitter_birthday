package com.aldaviva.twitter_birthday.twitter.service;

import com.aldaviva.twitter_birthday.common.exceptions.TwitterException;

public interface TwitterBirthdayUpdater {

    void updateBirthday() throws TwitterException;
}
