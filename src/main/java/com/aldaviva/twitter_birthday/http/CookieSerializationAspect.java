package com.aldaviva.twitter_birthday.http;

import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.glassfish.jersey.message.internal.StringBuilderUtils;
import org.springframework.util.StringUtils;

import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MultivaluedMap;
import java.net.HttpURLConnection;
import java.util.List;

/**
 * Jersey does not serialize cookies the same way browsers do. This causes problems with servers that expect a
 * browser-like Cookie header.
 *
 * This class uses AspectJ advice to modify Jersey's behavior.
 */
@Aspect
public class CookieSerializationAspect {

	@Pointcut("execution(String javax.ws.rs.ext.RuntimeDelegate.HeaderDelegate.toString(javax.ws.rs.core.Cookie)) && args(cookie)")
	private void cookieProviderToStringPointcut(final Cookie cookie) {
	}

	/**
	 * Serialize cookies as
	 *   name1=value1
	 * instead of
	 *   $Version=1;name1=value1
	 */
	@Around("cookieProviderToStringPointcut(cookie)")
	public String cookieProviderToStringAdvice(final Cookie cookie) throws Throwable {
		final StringBuilder serializedCookieBuilder = new StringBuilder();

		serializedCookieBuilder.append(cookie.getName()).append('=');
		StringBuilderUtils.appendQuotedIfWhitespace(serializedCookieBuilder, cookie.getValue());

		return serializedCookieBuilder.toString();
	}

	@Pointcut("execution(void org.glassfish.jersey.client.internal.HttpUrlConnector.setOutboundHeaders(javax.ws.rs.core.MultivaluedMap, java.net.HttpURLConnection)) && args(headers, uc)")
	private void setOutboundHeadersPointcut(final MultivaluedMap<String, String> headers, final HttpURLConnection uc) {
	}

	/**
	 * Serialize a list of cookies as
	 *   name1=value1; name2=value2
	 * instead of
	 *   name1=value1,name2=value2
	 */
	@After("setOutboundHeadersPointcut(headers, uc)")
	public void setOutboundHeadersAdvice(final MultivaluedMap<String, String> headers, final HttpURLConnection uc) throws Throwable {
		final List<String> cookies = headers.get(HttpHeaders.COOKIE);
		if(cookies != null && cookies.size() > 1) {
			final String fixedCookieHeaderValue = StringUtils.collectionToDelimitedString(cookies, "; ");
			uc.setRequestProperty(HttpHeaders.COOKIE, fixedCookieHeaderValue);
		}
	}

}
