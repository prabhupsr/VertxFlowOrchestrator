package com.prr.vertx.bean;

import com.prr.vertx.merger.ResultMerger;
import com.prr.vertx.model.Pair;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author mchidambaranatha
 */
@Component
public class StringMerger implements ResultMerger<String> {
    @Override
    public String Merge(List<Pair> request) {
        return request.toString();
    }
}
