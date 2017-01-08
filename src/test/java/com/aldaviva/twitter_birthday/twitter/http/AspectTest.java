package com.aldaviva.twitter_birthday.twitter.http;

import org.glassfish.jersey.message.internal.CookieProvider;
import org.testng.annotations.Test;

import javax.ws.rs.core.Cookie;
import java.lang.reflect.InvocationTargetException;

import static org.testng.Assert.assertEquals;

public class AspectTest {

    @Test
    public void adviceWorks() {
        final CookieProvider cookieProvider = new CookieProvider();
        final Cookie cookie = new Cookie("name1", "value1");
        final String serializedCookie = cookieProvider.toString(cookie);

        assertEquals(serializedCookie, "name1=value1");
    }

    @Test
    public void reflectionWorks() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        final CookieProvider cookieProvider = CookieProvider.class.getConstructor().newInstance(new Object[0]);
        final Cookie cookie = new Cookie("name1", "value1");
        final String serializedCookie = cookieProvider.toString(cookie);

        assertEquals(serializedCookie, "name1=value1");
    }

}
