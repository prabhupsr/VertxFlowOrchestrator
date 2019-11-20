package com.prr.vertx.config;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.prr.vertx.codec.GenericCodec;
import com.prr.vertx.codec.ICodec;
import com.prr.vertx.handler.AbstractAsyncHandler;
import com.prr.vertx.handler.AbstractParallelHandler;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.eventbus.EventBus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.springframework.util.ResourceUtils.getFile;

/**
 * @author mchidambaranatha
 */
@Configuration
public class EventBusConfig {

    private ApplicationContext applicationContext;

    public EventBusConfig(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @PostConstruct
    public void init() {
        configureHandlers();
        configureCodec();
    }

    @Bean
    public EventBus eventBus() {
        Vertx vertx = Vertx.vertx(new VertxOptions().setWorkerPoolSize(40));
        EventBus eventBus = vertx.eventBus();
        return eventBus;
    }

    public List<HandlerConfig> loadHandlerConfiguration() {

        try {
            return new ObjectMapper().readValue(
                    getFile("classpath:config/" + "HandlerConfig.json"), new TypeReference<>() {
                    });
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }

    private void configureCodec() {
        EventBus eventBus = eventBus();
        Map<String, ICodec> codecMap = applicationContext.getBeansOfType(ICodec.class);
        codecMap.values().stream().
                flatMap(iCodec -> iCodec.codec().stream())
                .distinct()
                .forEach(aClass -> eventBus.registerDefaultCodec(aClass, new GenericCodec<>(aClass.getName())));
    }

    private void configureHandlers() {
        ArrayList<AbstractAsyncHandler> createdHandlersTempCache = new ArrayList<>();
        loadHandlerConfiguration().forEach(handlerConfig -> configHandler(handlerConfig, createdHandlersTempCache));
    }

    private AbstractAsyncHandler configHandler(HandlerConfig handlerConfig, ArrayList<AbstractAsyncHandler> createdhandlers) {

        Class<?> classFromName = getClassFromName(handlerConfig.getHandlerClassName());
        return createdhandlers.stream()
                .filter(handler -> handler.getClass().equals(classFromName) && Objects.equals(handler.getSource(), handlerConfig.getSource()))
                .findFirst()
                .orElseGet(() -> createHandler(handlerConfig, createdhandlers));
    }

    private AbstractAsyncHandler createHandler(HandlerConfig handlerConfig, ArrayList<AbstractAsyncHandler> createdhandlers) {
        AbstractAsyncHandler handler = getTypedBean(handlerConfig.getHandlerClassName());
        handler.setSource(handlerConfig.getSource());
        handler.setTarget(handlerConfig.getTarget());
        if (handler instanceof AbstractParallelHandler) {
            AbstractParallelHandler parallelHandler = (AbstractParallelHandler) handler;
            List<AbstractAsyncHandler> subHandlers = getSubHandlers(handlerConfig.getSubHandlers(), createdhandlers);
            parallelHandler.init(getClassFromName(handlerConfig.getMergerClassName()), subHandlers);
        }
        eventBus().consumer(handler.getSource(), handler);
        createdhandlers.add(handler);
        return handler;
    }

    private List<AbstractAsyncHandler> getSubHandlers(List<HandlerConfig> handlerConfigs, ArrayList<AbstractAsyncHandler> createdhandlers) {
        return handlerConfigs.stream().map(hc -> configHandler(hc, createdhandlers)).collect(Collectors.toList());
    }

    private <T> T getTypedBean(String name) {
        return applicationContext.getBean(getClassFromName(name));
    }

    private <T> Class<T> getClassFromName(String name) {
        try {
            return (Class<T>) Class.forName(name);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    private static class HandlerConfig {
        private String source;
        private List<String> target;
        private String handlerClassName;
        private List<HandlerConfig> subHandlers;
        private String mergerClassName;
    }
}
