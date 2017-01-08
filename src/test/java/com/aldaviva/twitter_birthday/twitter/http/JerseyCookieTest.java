package com.aldaviva.twitter_birthday.twitter.http;

import com.aldaviva.twitter_birthday.config.ApplicationConfig;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTestNg;
import org.testng.annotations.Test;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.client.Client;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;

import static org.testng.Assert.assertEquals;

public class JerseyCookieTest extends JerseyTestNg.ContainerPerClassTest {

    @Path("/test/cookies")
    public static class CookieTestResource {

        @GET
        public String hello(@Context final HttpHeaders headers) {
            return headers.getHeaderString(HttpHeaders.COOKIE);
        }
    }

    @Override
    protected Application configure() {
        return new ResourceConfig(CookieTestResource.class);
    }

    @Test
    public void bindingTest() {
        final Client client = new ApplicationConfig().httpClient();

        final String cookieString = client.target("http://localhost:9998/test/cookies")
                .request()
                .cookie("name1", "value1")
                .cookie("name2", "value2")
                .get(String.class);

        assertEquals(cookieString, "name1=value1; name2=value2", "cookie header value");
    }

}
