package com.prr.vertx.bean;

import com.prr.vertx.handler.AbstractResponseHandler;
import com.prr.vertx.handler.Handler;
import reactor.core.publisher.Mono;

/**
 * @author mchidambaranatha
 */
@Handler
public class XyzResponseHandler extends AbstractResponseHandler<String, String> {
    @Override
    public Mono<String> process(String request) {
        return Mono.just("XYZ");
    }
}
