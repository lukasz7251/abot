package com.labreh.abot.domain.arbitrage;

import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;

@Service
public class ArbitrageOpportunityInOutAnalyzerImpl implements ArbitrageOpportunityInOutAnalyzer {

    private Map<Object, ArbitrageOpportunity> opportunityMap = Maps.newHashMap();
    private final Notificator notificator;
    private final BigDecimal diffToIn;
    private final BigDecimal diffToOut;

    @Autowired
    public ArbitrageOpportunityInOutAnalyzerImpl(Notificator notificator, @Value("${analyzer.diff_to_in}") BigDecimal diffToIn, @Value("${analyzer.diff_to_out}") BigDecimal diffToOut) {
        this.notificator = notificator;
        this.diffToIn = diffToIn;
        this.diffToOut = diffToOut;
    }

    @Override
    public void analyze(ArbitrageOpportunity opportunity) {
        ArbitrageOpportunity prev = opportunityMap.put(createKey(opportunity), opportunity);
        boolean newOpportunity = prev == null;
        if (newOpportunity){
            if (opportunity.getPercentageProfit().compareTo(diffToIn) < 0){
                return;
            }else {
                onNewOpportunity(opportunity);
                return;
            }
        } else {
            if (opportunity.getPercentageProfit().compareTo(diffToOut) < 0){
                analyzeNoOpportunity(opportunity.getBaseCurrency(), opportunity.getCounterCurrency());
                return;
            }
            boolean buyExchangeChange = ! prev.getBuyExchange().equalsIgnoreCase(opportunity.getBuyExchange());
            boolean sellExchangeChange = ! prev.getSellExchange().equalsIgnoreCase(opportunity.getSellExchange());
            if (buyExchangeChange || sellExchangeChange){
                onExchangeChange(buyExchangeChange, sellExchangeChange, opportunity);
                return;
            }
        }
    }

    private void analyzeNoOpportunity(String baseCurrency, String counterCurrency){
        boolean endOfOpportunity = opportunityMap.containsKey(createKey(baseCurrency, counterCurrency));
        if(endOfOpportunity){
            onOpportunityEnd(removeCurrentOpportunity(baseCurrency, counterCurrency));
        }
    }

    private ArbitrageOpportunity removeCurrentOpportunity(String baseCurrency, String counterCurrency) {
        return opportunityMap.remove(createKey(baseCurrency, counterCurrency));
    }

    private void onNewOpportunity(ArbitrageOpportunity opportunity) {
        notificator.notifyIN(opportunity);
    }

    private void onOpportunityEnd(ArbitrageOpportunity opportunity) {
        notificator.notifyOUT(opportunity);
    }

    private void onExchangeChange(boolean buyExchangeChange, boolean sellExchangeChange, ArbitrageOpportunity opportunity) {
        if (buyExchangeChange && ! sellExchangeChange){
            notificator.notifyBuyExchangeChange(opportunity);
        }else if (sellExchangeChange && ! buyExchangeChange){
            notificator.notifySellExchangeChange(opportunity);
        }else if (buyExchangeChange && sellExchangeChange){
            notificator.notifyBothExchangesChange(opportunity);
        }else {
            throw new IllegalArgumentException("There is no buy/sell exchange change");
        }
    }

    private String createKey(String baseCurrency, String counterCurrency) {
        return baseCurrency + "_" + counterCurrency;
    }

    private String createKey(ArbitrageOpportunity opportunity) {
        return createKey(opportunity.getBaseCurrency(), opportunity.getCounterCurrency());
    }

}
