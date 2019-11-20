package com.prr.vertx.bean;

import com.prr.vertx.handler.AbstractAsyncHandler;
import com.prr.vertx.handler.Handler;
import com.prr.vertx.model.Message;
import reactor.core.publisher.Mono;

/**
 * @author mchidambaranatha
 */
@Handler
public class MsgHandler extends AbstractAsyncHandler<Message, String> {

    @Override
    public Mono<String> process(Message request) {
        System.out.println("########################################");
        return Mono.just(request.toString());
    }
}
