package com.aldaviva.twitter_birthday.config;

import org.slf4j.bridge.SLF4JBridgeHandler;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

public class SpringConfig {

    public static final String ENV = "env";
    public static final String ENV_PROD = "prod";
    public static final String ENV_DEV = "dev";
    public static final String ENV_TEST = "test";
    public static final String PROFILE_TEST = "test";

    private AnnotationConfigApplicationContext context;

    public void onStartup(){
        if(context == null) {
            initLogging();

            context = new AnnotationConfigApplicationContext(ApplicationConfig.class);
            context.start();

            // Will update the Twitter birthday using ApplicationConfig.afterInit()

            // Program will then exit
        }
    }

    private void initLogging(){
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();
    }

    public AbstractApplicationContext getApplicationContext(){
        return context;
    }
}
