package com.aldaviva.twitter_birthday;

import com.aldaviva.twitter_birthday.common.exceptions.TwitterException;
import com.aldaviva.twitter_birthday.config.SpringConfig;
import com.aldaviva.twitter_birthday.twitter.service.TwitterBirthdayUpdater;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {

    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    public static void main(final String[] args){
        final SpringConfig springConfig = new SpringConfig();
        springConfig.onStartup();

        int exitCode = 0;
        try {
            springConfig.getApplicationContext().getBean(TwitterBirthdayUpdater.class).updateBirthday();
        } catch (final TwitterException e) {
            LOGGER.error("Failed to update birthday in Twitter profile.", e);
            exitCode = 1;
        } finally {
            springConfig.getApplicationContext().stop();
        }

        System.exit(exitCode);
    }

}
