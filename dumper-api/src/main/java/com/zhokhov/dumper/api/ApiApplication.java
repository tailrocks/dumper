package com.zhokhov.dumper.api;

import io.micronaut.runtime.Micronaut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.ZoneOffset;
import java.util.TimeZone;

public class ApiApplication {

    private static final Logger log = LoggerFactory.getLogger(ApiApplication.class);

    static {
        System.setProperty("java.net.preferIPv4Stack", "true");

        log.debug("Setting UTC time zone by default");
        TimeZone.setDefault(TimeZone.getTimeZone(ZoneOffset.UTC));
    }

    public static void main(String[] args) {
        Micronaut.run(ApiApplication.class, args);
    }

}
