package com.prr.vertx.handler;

import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Lazy;
import reactor.core.publisher.Mono;

import java.util.List;

import static java.util.Objects.requireNonNullElseGet;

/**
 * @author mchidambaranatha
 */
public abstract class AbstractAsyncHandler<T, U> implements io.vertx.core.Handler<Message<T>> {

    @Autowired
    @Lazy
    protected EventBus eventBus;

    @Autowired
    protected ApplicationContext applicationContext;

    private String source;
    private List<String> target;

    public abstract Mono<U> process(T request);

    public final void handle(Message<T> event) {
        System.out.println("handle called in " + getSource());
        Mono<U> processedResponse = process(event.body());
        processedResponse.subscribe(
                u -> onNext(event, u),
                throwable -> System.out.println(throwable),
                () -> System.out.println("on complete called in " + getSource()));
    }

    private void onNext(Message<T> event, U u) {
        reply(event, u);
        publish(u);
    }

    private void reply(Message<T> event, U processedResponse) {
        if (this instanceof AbstractResponseHandler) {
            event.reply(processedResponse);
        }
    }

    private void publish(U processedResponse) {
        requireNonNullElseGet(getTarget(), () -> List.<String>of())
                .forEach(addr -> eventBus.publish(addr, processedResponse));
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public List<String> getTarget() {
        return target;
    }

    public void setTarget(List<String> target) {
        this.target = target;
    }
}