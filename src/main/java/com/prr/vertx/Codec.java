package com.prr.vertx;

import com.prr.vertx.codec.ICodec;
import com.prr.vertx.model.Message;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author mchidambaranatha
 */
@Component
public class Codec implements ICodec {

    @Override
    public List<Class<?>> codec() {
        return List.of(Message.class);
    }
}
