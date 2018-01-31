package com.labreh.abot.domain.arbitrage;

public interface Notificator {
    void notifyIN(ArbitrageOpportunity opportunity);

    void notifyOUT(ArbitrageOpportunity opportunity);

    void notifyBuyExchangeChange(ArbitrageOpportunity opportunity);

    void notifySellExchangeChange(ArbitrageOpportunity opportunity);

    void notifyBothExchangesChange(ArbitrageOpportunity opportunity);
}
