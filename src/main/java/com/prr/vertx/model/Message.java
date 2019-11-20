package com.prr.vertx.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author mchidambaranatha
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Message {
    private List<String> list;
}
