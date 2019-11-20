package com.prr.vertx.handler;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @author mchidambaranatha
 */
@Component
@Scope("prototype")
@Retention(RUNTIME)
public @interface Handler {
}
