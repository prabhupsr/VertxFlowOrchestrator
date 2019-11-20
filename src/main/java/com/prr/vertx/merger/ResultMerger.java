package com.prr.vertx.merger;

import com.prr.vertx.model.Pair;

import java.util.List;

/**
 * @author mchidambaranatha
 */

public interface ResultMerger<U> {
    U Merge(List<Pair> request);
}

