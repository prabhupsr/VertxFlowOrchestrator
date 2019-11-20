package com.prr.vertx;

import com.prr.vertx.config.EventBusConfig;
import com.prr.vertx.model.Message;
import io.vertx.core.eventbus.EventBus;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author mchidambaranatha
 */
@Configuration
@Import(EventBusConfig.class)
public class Publisher {

    private EventBus eventBus;

    public Publisher(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    @PostConstruct
    public void init() {
        createTimer(50);
        //IntStream.range(1, 2).parallel().forEach(this::createTimer);
    }

    private void createTimer(int value) {

        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                eventBus.publish("src", "123456789");
                  eventBus.publish("msgg", new Message(List.of("a", "b", "c", String.valueOf(value))));
            }
        }, 5 * value, 50 * value);
    }

}
