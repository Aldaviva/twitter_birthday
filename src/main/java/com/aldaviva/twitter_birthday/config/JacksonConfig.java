package com.aldaviva.twitter_birthday.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;

@Provider
public class JacksonConfig implements ContextResolver<ObjectMapper> {

	@Autowired private ObjectMapper objectMapper;

	@Override
	public ObjectMapper getContext(final Class<?> type) {
		return objectMapper;
	}

}
