package com.aldaviva.twitter_birthday.twitter.data;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public interface TwitterConstants {

    String AUTH_TOKEN = "auth_token";
    String TWITTER_SESSION = "_twitter_sess";
    String FORMATTED_BIRTHDATE = "formatted_birthdate";
    String PROFILE_UPDATE_URI = "/i/profiles/update";
    String AUTHENTICITY_TOKEN = "authenticity_token";

    DateTimeFormatter TWITTER_DATE_FORMATTER = DateTimeFormat.forPattern("MMMM d, yyyy");
}
