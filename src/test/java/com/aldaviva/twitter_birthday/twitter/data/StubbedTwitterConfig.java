package com.aldaviva.twitter_birthday.twitter.data;

import com.aldaviva.twitter_birthday.config.TwitterConfig;

import java.net.URI;

public class StubbedTwitterConfig extends TwitterConfig {

    @Override
    public String getUsername() {
        return "dril";
    }

    @Override
    public URI getBaseUri() {
        return URI.create("https://twitter.com");
    }

    @Override
    public int getBirthYear() {
        return 1984;
    }

    @Override
    public String getAuthToken() {
        return "abcdefg";
    }
}
