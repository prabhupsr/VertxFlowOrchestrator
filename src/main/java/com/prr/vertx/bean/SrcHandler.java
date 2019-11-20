package com.prr.vertx.bean;

import com.prr.vertx.handler.AbstractAsyncHandler;
import com.prr.vertx.handler.Handler;
import reactor.core.publisher.Mono;

/**
 * @author mchidambaranatha
 */
@Handler
public class SrcHandler extends AbstractAsyncHandler<String, String> {

    @Override
    public Mono<String> process(String request) {
        return Mono.just("request" + "from" + this.getClass().getName());
    }
}
