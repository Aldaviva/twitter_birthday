package com.aldaviva.twitter_birthday.config;

import com.aldaviva.twitter_birthday.twitter.data.BirthdayVisibility;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.ws.rs.core.UriBuilder;
import java.net.URI;

@Component
public class TwitterConfig {

    @Value("${twitter.username:}") private String username;
    @Value("${twitter.baseUri:https://twitter.com}") private URI baseUri;
    @Value("${birthYear:0}") private int birthYear;
    @Value("${twitter.authToken:}") private String authToken;
    @Value("${twitter.birthdayVisibility.day:WE_FOLLOW_EACH_OTHER}") private BirthdayVisibility birthDayVisibility;
    @Value("${twitter.birthdayVisibility.year:ONLY_ME}") private BirthdayVisibility birthYearVisibility;

    public String getUsername() {
        return username;
    }

    public URI getBaseUri() {
        return baseUri;
    }

    public int getBirthYear() {
        return birthYear;
    }

    public URI getProfileUri() {
        return UriBuilder.fromUri(getBaseUri())
                .path(getUsername())
                .build();
    }

    public String getAuthToken() {
        return authToken;
    }

    public BirthdayVisibility getBirthDayVisibility() {
        return birthDayVisibility;
    }

    public BirthdayVisibility getBirthYearVisibility() {
        return birthYearVisibility;
    }

}
