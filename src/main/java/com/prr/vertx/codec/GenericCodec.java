package com.prr.vertx.codec;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.MessageCodec;

/**
 * @author mchidambaranatha
 */
public class GenericCodec<T> implements MessageCodec<T, T> {

    private String name;

    public GenericCodec(String name) {
        this.name = name;
    }

    @Override
    public void encodeToWire(Buffer buffer, T message) {
        throw new RuntimeException("serialization not supported");
    }

    @Override
    public T decodeFromWire(int pos, Buffer buffer) {
        throw new RuntimeException("deserialization not supported");
    }

    @Override
    public T transform(T message) {
        return message;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public byte systemCodecID() {
        return -1;
    }
}
