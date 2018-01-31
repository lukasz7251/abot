package com.labreh.abot.domain;

import org.knowm.xchange.Exchange;

import java.util.stream.Stream;

public interface ExchangeManager {
    Exchange get(String exchangeName);
    Stream<Exchange> all();
}
