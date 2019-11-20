package com.prr.vertx.bean;

import com.prr.vertx.handler.AbstractAsyncHandler;
import com.prr.vertx.handler.Handler;
import reactor.core.publisher.Mono;

/**
 * @author mchidambaranatha
 */
@Handler
public class EndHandler extends AbstractAsyncHandler<String, String> {

    @Override
    public Mono<String> process(String request) {
        System.out.println("from End Handler" + request);
        return Mono.just("request" + "from" + this.getClass().getName());
    }
}
