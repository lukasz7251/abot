package com.labreh.abot.domain.arbitrage;

import com.google.common.collect.Maps;
import com.labreh.abot.domain.ExchangeManager;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.marketdata.Ticker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.math.MathContext;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

import static java.util.Comparator.comparing;

@Service
public class ArbitrageOpportunityFinderImpl implements ArbitrageOpportunityFinder {

    private final static Logger LOGGER = LoggerFactory.getLogger(ArbitrageOpportunityFinderImpl.class);
    private final static DecimalFormat df = new DecimalFormat("0.##");

    private final ExchangeManager exchangeManager;
    private final ArbitrageOpportunityInOutAnalyzer inOutAnalyzer;
    private final List<CurrencyPair> currencyPairs;

    @Autowired
    public ArbitrageOpportunityFinderImpl(ExchangeManager exchangeManager, ArbitrageOpportunityInOutAnalyzer inOutAnalyzer, @Value("#{'${finder.symbols}'.split(',')}") List<String> symbolsNames) {
        this.exchangeManager = exchangeManager;
        this.inOutAnalyzer = inOutAnalyzer;
        this.currencyPairs = new CopyOnWriteArrayList<>(conertToSymbols(symbolsNames));
    }

    private List<CurrencyPair> conertToSymbols(List<String> symbolsNames) {
        return symbolsNames.stream().map(s->{
            String[] curriences = s.split("_");
            Assert.isTrue(curriences.length == 2, "Invalid symbol name => " + s);
            return new CurrencyPair(curriences[0], curriences[1]);
        }).collect(Collectors.toList());
    }

    @Override
    @Scheduled(cron = "${finder.cron}")
    public void findArbitrageOpportunity(){
        currencyPairs.stream().forEach(symbol -> {
            Map<String, Ticker> priceMap = buildSymbolPricesMap(symbol);;
            if (priceMap.size() < 2){
                return;
            }

            Map.Entry<String, Ticker> highestBidEntry = getHighestBid(priceMap);
            Map.Entry<String, Ticker> lowestAskEntry = getLowestAsk(priceMap);

            BigDecimal lowestAsk = lowestAskEntry.getValue().getAsk();
            BigDecimal highestBid = highestBidEntry.getValue().getBid();

            String buyExchange = lowestAskEntry.getKey();
            String sellExchange = highestBidEntry.getKey();

            BigDecimal pricePercentageDiff =  highestBid.subtract(lowestAsk).divide(lowestAsk, MathContext.DECIMAL128).multiply(BigDecimal.valueOf(100), MathContext.DECIMAL32);
            String formattedPriceDiff = df.format(pricePercentageDiff) + "%";

            ArbitrageOpportunity opportunity = new ArbitrageOpportunity(buyExchange.toUpperCase(), sellExchange.toUpperCase(), lowestAsk, highestBid, symbol.base.getCurrencyCode(), symbol.counter.getCurrencyCode());
            inOutAnalyzer.analyze(opportunity);
        });
    }

    private Map<String, Ticker> buildSymbolPricesMap(CurrencyPair currencyPair) {
        Map<String, Ticker> tickerMap = Maps.newHashMap(buildMapForCurrencyPair(currencyPair));
        if(tickerMap.size() != exchangeManager.all().count()){
            CurrencyPair twinPair = null;
            if(currencyPair.counter.getCurrencyCode().equals("USD")){
                twinPair = new CurrencyPair(currencyPair.base.getCurrencyCode(), "USDT");
            }else if (currencyPair.counter.getCurrencyCode().equals("USDT")){
                twinPair = new CurrencyPair(currencyPair.base.getCurrencyCode(), "USD");
            }else {
                return tickerMap;
            }
            tickerMap.putAll(buildMapForCurrencyPair(twinPair));
        }
        return tickerMap;
    }

    private Map<String, Ticker> buildMapForCurrencyPair(CurrencyPair currencyPair) {
        Map<String, Ticker> tickerMap = Maps.newConcurrentMap();
        exchangeManager.all().parallel().forEach(exchange -> {
            try {
                Ticker ticker = exchange.getMarketDataService().getTicker(currencyPair);
                if(ticker == null || ticker.getBid() == null || ticker.getAsk() == null){
//                    LOGGER.warn("Null objects on " + exchange.getExchangeSpecification().getExchangeName() + " for " + currencyPair + " => " + ticker);
                    return;
                }else if (ticker.getBid().doubleValue() <= 0 || ticker.getAsk().doubleValue() <= 0){
//                    LOGGER.warn("One of prices <= 0 on " + exchange.getExchangeSpecification().getExchangeName() + " for " + currencyPair + " => " + ticker);
                    return;
                }
                tickerMap.put(exchange.getExchangeSpecification().getExchangeName(), ticker);
            } catch (Exception e){
//                LOGGER.warn("An error occured on " + exchange.getExchangeSpecification().getExchangeName(), e);
            }
        });
        return tickerMap;
    }

    private Map.Entry<String, Ticker> getHighestBid(Map<String, Ticker> tickerMap) {
        return tickerMap.entrySet().stream().max(comparing(e -> e.getValue().getBid())).get();
    }

    private Map.Entry<String, Ticker> getLowestAsk(Map<String, Ticker> tickersMap) {
        return tickersMap.entrySet().stream().min(comparing(e -> e.getValue().getBid())).get();
    }

}
