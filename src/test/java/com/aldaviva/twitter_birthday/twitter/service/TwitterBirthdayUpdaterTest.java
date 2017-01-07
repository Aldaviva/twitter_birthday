package com.aldaviva.twitter_birthday.twitter.service;

import com.aldaviva.test.JerseyClientTest;
import com.aldaviva.twitter_birthday.common.exceptions.TwitterException;
import com.aldaviva.twitter_birthday.twitter.auth.TwitterAuthService;
import com.aldaviva.twitter_birthday.twitter.data.StubbedTwitterConfig;
import com.aldaviva.twitter_birthday.twitter.data.TwitterConstants;
import com.aldaviva.twitter_birthday.twitter.data.TwitterSession;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.joda.time.DateTime;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.reflect.Whitebox;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.*;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;

public class TwitterBirthdayUpdaterTest extends JerseyClientTest {

    @InjectMocks private TwitterBirthdayUpdaterImpl twitterBirthdayUpdater;
    @Mock private TwitterAuthService twitterAuthService;

    @BeforeMethod
    private void init() throws TwitterException {
        Whitebox.setInternalState(twitterBirthdayUpdater, new StubbedTwitterConfig());

        final TwitterSession session = new TwitterSession();
        session.setAuthenticationToken("a");
        session.setAuthenticityToken("b");
        session.setSessionId("c");
        when(twitterAuthService.getSession()).thenReturn(session);
    }

    @POST
    @Path("/i/profiles/update")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public JsonNode update(@Context UriInfo uriInfo,
                           @Context final HttpHeaders headers,
                           MultivaluedMap<String, String> body) {
        final DateTime expectedBirthday = new DateTime().withTimeAtStartOfDay().withYear(1984);

        assertEquals(headers.getHeaderString("X-Requested-With"), "XMLHttpRequest", "X-Requested-With");
        assertEquals(headers.getHeaderString("Referer"), "https://twitter.com/dril", "Referer");
        assertEquals(headers.getCookies().get("auth_token").getValue(), "a", "authToken");
        assertEquals(headers.getCookies().get("_twitter_sess").getValue(), "c", "_twitter_sess");
        assertThat(headers.getHeaderString(HttpHeaders.COOKIE), not(containsString("$Version")));
        assertEquals(body.getFirst("authenticity_token"), "b", "authenticity_token");

        assertEquals(body.getFirst("page_context"), "me", "page_context");
        assertEquals(body.getFirst("session_context"), "profile", "session_context");
        assertEquals(body.getFirst("user[birthdate][birthday_visibility]"), "1", "user[birthdate][birthday_visibility]");
        assertEquals(body.getFirst("user[birthdate][birthyear_visibility]"), "1", "user[birthdate][birthyear_visibility]");
        assertEquals(body.getFirst("user[birthdate][day]"), String.valueOf(expectedBirthday.getDayOfMonth()), "user[birthdate][day]");
        assertEquals(body.getFirst("user[birthdate][month]"), String.valueOf(expectedBirthday.getMonthOfYear()), "user[birthdate][month]");
        assertEquals(body.getFirst("user[birthdate][year]"), String.valueOf(expectedBirthday.getYear()), "user[birthdate][year]");

        final ObjectNode obj = new ObjectNode(JsonNodeFactory.instance);
        final String formattedBirthdate = TwitterConstants.TWITTER_DATE_FORMATTER.print(expectedBirthday);
        obj.put("has_birthdate", true);
        obj.put("formatted_birthdate", formattedBirthdate);
        obj.put("birthdate", "<span class=\"js-tooltip\" title=\"We follow each other\">    Born on " + formattedBirthdate + "\r\n</span>");
        return obj;
    }

    @Test
    public void updateBirthday() throws TwitterException {
        twitterBirthdayUpdater.updateBirthday();
    }
}
