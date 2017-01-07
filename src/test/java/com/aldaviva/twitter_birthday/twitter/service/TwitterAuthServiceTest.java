package com.aldaviva.twitter_birthday.twitter.service;

import com.aldaviva.test.JerseyClientTest;
import com.aldaviva.twitter_birthday.common.exceptions.TwitterException;
import com.aldaviva.twitter_birthday.config.TwitterConfig;
import com.aldaviva.twitter_birthday.twitter.data.StubbedTwitterConfig;
import com.aldaviva.twitter_birthday.twitter.auth.TwitterAuthServiceImpl;
import com.aldaviva.twitter_birthday.twitter.data.TwitterSession;
import org.mockito.InjectMocks;
import org.powermock.reflect.Whitebox;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.*;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.testng.Assert.assertEquals;

public class TwitterAuthServiceTest extends JerseyClientTest {

    private static final String SESSION_ID = "BAh7CSIKZmxhc1hJQzonQWN0aW9uQ29udHJvbGxlcjo6Rmxhc2g6OkZsYXNo%250ASGFzaHsABjoKQHVzZWR7ADoPY3JlYXRlZF9hdGwrCEi10nhZAToMY3NyZl9p%250AZCIlY2U1NjNlZDI2YjkyNTRjZjljMGUyZjc5YTU5ZWNiNzc6B2lkIiVlMDc0%250AMGI1ZmRlZTFmZmE3ODgwxDYxMWE2MjIyODRjYQ%253D%253D--1fca78aa8d13310b7084ad7754084870a8e9ea99";

    @InjectMocks private TwitterAuthServiceImpl twitterAuthService;
    private final TwitterConfig twitterConfig = new StubbedTwitterConfig();

    @BeforeMethod
    private void init() {
        Whitebox.setInternalState(twitterAuthService, twitterConfig);
    }

    @GET
    @Path("/dril")
    @Produces(MediaType.TEXT_HTML)
    public Response getProfile(@Context final HttpHeaders headers) throws URISyntaxException, IOException {
        assertEquals(headers.getCookies().get("auth_token").getValue(), "abcdefg", "authToken");

        final String htmlResponse = new String(Files.readAllBytes(Paths.get(getClass().getClassLoader().getResource("profile.html").toURI())), StandardCharsets.UTF_8);
        return Response.ok(htmlResponse, MediaType.TEXT_HTML_TYPE)
                .cookie(new NewCookie("_twitter_sess", SESSION_ID, "/", ".twitter.com", null, -1, true, true))
                .build();
    }

    @Test
    public void getSession() throws TwitterException {
        final TwitterSession session = twitterAuthService.getSession();

        assertEquals(session.getSessionId(), SESSION_ID, "session ID");
        assertEquals(session.getAuthenticationToken(), twitterConfig.getAuthToken(), "auth token");
        assertEquals(session.getAuthenticityToken(), "cd85670798e4f345ad1ba8842116f08473b7a288", "authenticity token");
    }
}
