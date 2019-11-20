package com.prr.vertx.model;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

/**
 * @author mchidambaranatha
 */
@Getter
@Builder
@ToString
public class Pair {
    private String handlerName;
    private Object response;
}
