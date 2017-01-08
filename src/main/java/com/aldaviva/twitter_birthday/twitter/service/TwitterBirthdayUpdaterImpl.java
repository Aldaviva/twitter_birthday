package com.aldaviva.twitter_birthday.twitter.service;

import com.aldaviva.twitter_birthday.common.RetryRunner;
import com.aldaviva.twitter_birthday.common.exceptions.TwitterException;
import com.aldaviva.twitter_birthday.config.TwitterConfig;
import com.aldaviva.twitter_birthday.twitter.auth.TwitterAuthService;
import com.aldaviva.twitter_birthday.twitter.data.TwitterConstants;
import com.aldaviva.twitter_birthday.twitter.data.TwitterSession;
import com.fasterxml.jackson.databind.JsonNode;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

@Service
public class TwitterBirthdayUpdaterImpl implements TwitterBirthdayUpdater {

    private static final Logger LOGGER = LoggerFactory.getLogger(TwitterBirthdayUpdaterImpl.class);

    private static final int MAX_ATTEMPTS = 5;

    @Autowired private TwitterAuthService twitterAuthService;
    @Autowired private Client httpClient;
    @Autowired private TwitterConfig twitterConfig;

    @Override
    public void updateBirthday() throws TwitterException {
        final TwitterSession session = twitterAuthService.getSession();
        final DateTime birthday = getBirthday();

        LOGGER.info("Updating birthday to {}", birthday.toString(TwitterConstants.TWITTER_DATE_FORMATTER));

        final MultivaluedMap<String, String> form = new MultivaluedHashMap<>();
        form.putSingle(TwitterConstants.AUTHENTICITY_TOKEN, session.getAuthenticityToken());
        form.putSingle("page_context", "me");
        form.putSingle("session_context", "profile");
        form.putSingle("user[birthdate][birthday_visibility]", "1"); //TODO allow the TwitterConfig to customize these enum values
        form.putSingle("user[birthdate][birthyear_visibility]", "1");
        form.putSingle("user[birthdate][day]", String.valueOf(birthday.getDayOfMonth()));
        form.putSingle("user[birthdate][month]", String.valueOf(birthday.getMonthOfYear()));
        form.putSingle("user[birthdate][year]", String.valueOf(birthday.getYear()));

        try {
            RetryRunner.runUntilNoExceptions(MAX_ATTEMPTS, new Callable<DateTime>() {
                @Override
                public DateTime call() throws TwitterException {
                    try {
                        final JsonNode response = httpClient.target(twitterConfig.getBaseUri())
                                .path(TwitterConstants.PROFILE_UPDATE_URI)
                                .request()
                                .cookie(new Cookie(TwitterConstants.AUTH_TOKEN, session.getAuthenticationToken()))
                                .cookie(new Cookie(TwitterConstants.TWITTER_SESSION, session.getSessionId()))
                                .header("X-Requested-With", "XMLHttpRequest")
                                .header("Referer", twitterConfig.getProfileUri())
                                .post(Entity.form(form), JsonNode.class);

                        final String actualBirthday = response.path(TwitterConstants.FORMATTED_BIRTHDATE).textValue();
                        final String expectedBirthday = TwitterConstants.TWITTER_DATE_FORMATTER.print(birthday);
                        if (!expectedBirthday.equals(actualBirthday)) {
                            throw new TwitterException("Twitter refused to update the birthday from " + actualBirthday + " to " + expectedBirthday);
                        }

                        LOGGER.info("Successfully updated birthday on Twitter profile to {}", actualBirthday);
                        return birthday;
                    } catch (WebApplicationException | ProcessingException e) {
                        throw new TwitterException("Update request failed", e);
                    }
                }
            });
        } catch (final ExecutionException e) {
            if (e.getCause() instanceof TwitterException) {
                throw (TwitterException) e.getCause();
            } else {
                throw new RuntimeException(e.getCause());
            }
        }
    }


    public DateTime getBirthday() {
        return new DateTime().withTimeAtStartOfDay().withYear(twitterConfig.getBirthYear());
    }
}
