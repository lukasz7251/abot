package com.labreh.abot.domain;

import org.knowm.xchange.Exchange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class ExchangeManagerImpl implements ExchangeManager {

    private final Map<String,Exchange> exchanges;

    @Autowired
    public ExchangeManagerImpl(List<Exchange> exchanges) {
        this.exchanges = exchanges.stream().collect(Collectors.toMap(e -> e.getExchangeSpecification().getExchangeName().toUpperCase(),e -> e));
    }

    @Override
    public Exchange get(String exchangeName){
        return exchanges.get(exchangeName.toUpperCase());
    }

    @Override
    public Stream<Exchange> all() {
        return exchanges.values().stream();
    }
}
