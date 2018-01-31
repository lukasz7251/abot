package com.labreh.abot.domain.arbitrage;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

public class ArbitrageOpportunity {

    private final String buyExchange;
    private final String sellExchange;
    private final BigDecimal buyPrice;
    private final BigDecimal sellPrice;
    private final String baseCurrency;
    private final String counterCurrency;
    private final BigDecimal percentageProfit;

    public ArbitrageOpportunity(String buyExchange, String sellExchange, BigDecimal buyPrice, BigDecimal sellPrice, String baseCurrency, String counterCurrency) {
        this.buyExchange = buyExchange;
        this.sellExchange = sellExchange;
        this.buyPrice = buyPrice;
        this.sellPrice = sellPrice;
        this.baseCurrency = baseCurrency;
        this.counterCurrency = counterCurrency;
        this.percentageProfit = sellPrice.subtract(buyPrice).divide(sellPrice, MathContext.DECIMAL128).multiply(BigDecimal.valueOf(100), MathContext.DECIMAL32).setScale(2, RoundingMode.HALF_EVEN);
    }

    public String getBuyExchange() {
        return buyExchange;
    }

    public String getSellExchange() {
        return sellExchange;
    }

    public BigDecimal getBuyPrice() {
        return buyPrice;
    }

    public BigDecimal getSellPrice() {
        return sellPrice;
    }

    public String getBaseCurrency() {
        return baseCurrency;
    }

    public String getCounterCurrency() {
        return counterCurrency;
    }

    public BigDecimal getPercentageProfit() {
        return percentageProfit;
    }
}
