package com.aldaviva.twitter_birthday.twitter.auth;

import com.aldaviva.twitter_birthday.common.exceptions.TwitterException;
import com.aldaviva.twitter_birthday.config.TwitterConfig;
import com.aldaviva.twitter_birthday.twitter.data.TwitterConstants;
import com.aldaviva.twitter_birthday.twitter.data.TwitterSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Client;
import javax.ws.rs.core.Response;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class TwitterAuthServiceImpl implements TwitterAuthService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TwitterAuthServiceImpl.class);
    private static final Pattern AUTHENTICITY_TOKEN_INPUT_PATTERN = Pattern.compile(".*<input type=\"hidden\" " +
            "value=\"(\\w+?)\" name=\"authenticity_token\" class=\"authenticity_token\">.*", Pattern.DOTALL);

    @Autowired private Client httpClient;
    @Autowired private TwitterConfig twitterConfig;

    private TwitterSession twitterSession;

    @Override
    public TwitterSession getSession() throws TwitterException {
        if (twitterSession == null) {
            twitterSession = startSession();
        }
        return twitterSession;
    }

    @Override
    public TwitterSession startSession() throws TwitterException {
        LOGGER.info("Starting new Twitter session for user {}", twitterConfig.getUsername());
        final TwitterSession session = new TwitterSession();
        session.setAuthenticationToken(twitterConfig.getAuthToken());

        Response response = null;
        try {
            response = httpClient.target(twitterConfig.getProfileUri())
                    .request()
                    .cookie(TwitterConstants.AUTH_TOKEN, session.getAuthenticationToken())
                    .get(Response.class);

            session.setSessionId(response.getCookies().get(TwitterConstants.TWITTER_SESSION).getValue());

            final String responseBody = response.readEntity(String.class);

            final Matcher matcher = AUTHENTICITY_TOKEN_INPUT_PATTERN.matcher(responseBody);
            if (matcher.matches()) {
                session.setAuthenticityToken(matcher.group(1));
            }

            LOGGER.debug("Started session {}", session);
            return session;

        } catch (WebApplicationException | ProcessingException e) {
            throw new TwitterException("Failed to load profile page while creating session", e);
        } finally {
            if (response != null) {
                response.close();
            }
        }
    }
}
