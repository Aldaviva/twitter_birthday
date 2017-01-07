package com.aldaviva.twitter_birthday.twitter.service;

import com.aldaviva.twitter_birthday.common.exceptions.TwitterException;
import com.aldaviva.twitter_birthday.config.TwitterConfig;
import com.aldaviva.twitter_birthday.twitter.auth.TwitterAuthService;
import com.aldaviva.twitter_birthday.twitter.data.TwitterConstants;
import com.aldaviva.twitter_birthday.twitter.data.TwitterSession;
import com.fasterxml.jackson.databind.JsonNode;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;

@Service
public class TwitterBirthdayUpdaterImpl implements TwitterBirthdayUpdater {

    private static final Logger LOGGER = LoggerFactory.getLogger(TwitterBirthdayUpdaterImpl.class);

    @Autowired
    private TwitterAuthService twitterAuthService;

    @Autowired
    private Client httpClient;

    @Autowired
    private TwitterConfig twitterConfig;

    @Override
    public void updateBirthday() throws TwitterException {
        final TwitterSession session = twitterAuthService.getSession();
        final DateTime birthday = getBirthday();

        LOGGER.info("Updating birthday to {}", birthday.toString(DateTimeFormat.shortDate()));

        final MultivaluedMap<String, String> form = new MultivaluedHashMap<>();
        form.putSingle(TwitterConstants.AUTHENTICITY_TOKEN, session.getAuthenticityToken());
        form.putSingle("page_context", "me");
        form.putSingle("session_context", "profile");
        form.putSingle("user[birthdate][birthday_visibility]", "1");
        form.putSingle("user[birthdate][birthyear_visibility]", "1");
        form.putSingle("user[birthdate][day]", String.valueOf(birthday.getDayOfMonth()));
        form.putSingle("user[birthdate][month]", String.valueOf(birthday.getMonthOfYear()));
        form.putSingle("user[birthdate][year]", String.valueOf(birthday.getYear()));

        try {
            final JsonNode response = httpClient.target(twitterConfig.getBaseUri())
                    .path(TwitterConstants.PROFILE_UPDATE_URI)
                    .request()
                    /* TODO this is a terrible hack
                     * Jersey formats request cookies like this: $Version=1;name1=value1, $Version=2;name2=value2
                     * However, Twitter rejects this header with 403
                     * Twitter expects cookies that look like:   name1=value1;name2=value2
                     * I don't see an easy way to provide an alternate implementation of CookieProvider, so for now
                     * I'm manually serializing the cookies into a header value.
                     *
                     * I should see if this can be fixed in Jersey code, or at least extract that logic from this class.
                     */
                    .header(HttpHeaders.COOKIE, getCookieHeaderValue(session))
//                    .cookie(new Cookie(TwitterConstants.AUTH_TOKEN, session.getAuthenticationToken()))
//                    .cookie(new Cookie(TwitterConstants.TWITTER_SESSION, session.getSessionId()))
                    .header("X-Requested-With", "XMLHttpRequest")
                    .header("Referer", twitterConfig.getProfileUri())
                    .post(Entity.form(form), JsonNode.class);

            final String actualBirthday = response.path(TwitterConstants.FORMATTED_BIRTHDATE).textValue();
            final String expectedBirthday = TwitterConstants.TWITTER_DATE_FORMATTER.print(birthday);
            if (!expectedBirthday.equals(actualBirthday)) {
                throw new TwitterException("Twitter refused to update the birthday from " + actualBirthday + " to " + expectedBirthday);
            }
        } catch (WebApplicationException | ProcessingException e) {
            throw new TwitterException("Failed to update birthday in profile", e);
        }
    }

    private static String getCookieHeaderValue(TwitterSession session) {
        return TwitterConstants.AUTH_TOKEN + "=" + session.getAuthenticationToken() + ";" +
                TwitterConstants.TWITTER_SESSION + "=" + session.getSessionId();
    }

    public DateTime getBirthday() {
        return new DateTime().withTimeAtStartOfDay().withYear(twitterConfig.getBirthYear());
    }
}
