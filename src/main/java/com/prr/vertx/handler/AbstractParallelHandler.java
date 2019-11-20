package com.prr.vertx.handler;

import com.prr.vertx.merger.ResultMerger;
import com.prr.vertx.model.Pair;
import lombok.Builder;
import lombok.Getter;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNullElseGet;

/**
 * @author mchidambaranatha
 */
public abstract class AbstractParallelHandler<T, U> extends AbstractAsyncHandler<T, U> {

    private static final String ABSTRACT_RESPONSE_HANDLER = "AbstractResponseHandler";
    private static final String PUBLISH_HANDLER = "PublishHandler";
    private ResultMerger<U> resultMerger;
    private HandlerContainer handlerContainer;

    public void init(Class<ResultMerger> mergerClass, List<AbstractAsyncHandler> asyncHandlers) {
        this.resultMerger = applicationContext.getBean(mergerClass);
        this.handlerContainer = getSubHandlers(asyncHandlers);
    }

    @Override
    public Mono<U> process(T msg) {

        CompletableFuture<FluxSink<Pair>> fluxSinkCompletableFuture = new CompletableFuture<>();

        executeParallelHandlers(msg, fluxSinkCompletableFuture);

        return Flux.<Pair>create(snk -> fluxSinkCompletableFuture.complete(snk))
                .buffer(handlerContainer.getResponseHandlers().size())
                .next()
                .map(pairs -> resultMerger.Merge(pairs));
    }

    private void executeParallelHandlers(T msg, CompletableFuture<FluxSink<Pair>> fluxSink) {

        handleResponseHandlers(msg, fluxSink, handlerContainer.getResponseHandlers());
        handlePublishHandlers(msg, handlerContainer.getPublishHandlers());
    }

    private void handlePublishHandlers(T msg, List<AbstractAsyncHandler> handlers) {
        requireNonNullElseGet(handlers, () -> List.<AbstractAsyncHandler>of()).forEach(handler -> eventBus.publish(handler.getSource(), msg));
    }

    private void handleResponseHandlers(T msg,
                                        CompletableFuture<FluxSink<Pair>> fluxSinkFuture,
                                        List<AbstractAsyncHandler> handlers) {
        handlers.forEach(handler -> requestReply(msg, fluxSinkFuture, handler));
    }

    private void requestReply(T msg,
                              CompletableFuture<FluxSink<Pair>> fluxSinkCompletableFuture,
                              AbstractAsyncHandler responseHandler) {
        eventBus.request(responseHandler.getSource(), msg, event -> {
            FluxSink<Pair> fluxSink = getDataFrom(fluxSinkCompletableFuture);
            event.map(o -> o.body()).map(o -> {
                fluxSink.next(createResponsePair(responseHandler.getSource(), o));
                return null;
            });
        });
    }

    private Pair createResponsePair(String source, Object o) {
        return Pair.builder()
                .handlerName(source)
                .response(o)
                .build();
    }

    private FluxSink<Pair> getDataFrom(CompletableFuture<FluxSink<Pair>> fluxSinkCompletableFuture) {
        try {
            return fluxSinkCompletableFuture.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private HandlerContainer buildHandlerHolder(Map<String, List<AbstractAsyncHandler>> stringListMap) {
        return HandlerContainer
                .builder()
                .responseHandlers(stringListMap.get(ABSTRACT_RESPONSE_HANDLER))
                .publishHandlers(stringListMap.get(PUBLISH_HANDLER))
                .build();
    }

    private String getHandlerType(AbstractAsyncHandler handler) {
        return handler instanceof AbstractResponseHandler ? ABSTRACT_RESPONSE_HANDLER : PUBLISH_HANDLER;
    }

    private HandlerContainer getSubHandlers(List<AbstractAsyncHandler> asyncHandlers) {
        return asyncHandlers.stream()
                .collect(
                        Collectors.collectingAndThen(
                                Collectors.groupingBy(this::getHandlerType),
                                this::buildHandlerHolder));
    }

    @Getter
    @Builder
    private static class HandlerContainer {
        List<AbstractAsyncHandler> responseHandlers;
        List<AbstractAsyncHandler> publishHandlers;
    }
}
