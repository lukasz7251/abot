package com.labreh.abot.domain.arbitrage;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;

@RunWith(MockitoJUnitRunner.class)
public class ArbitrageOpportunityInOutAnalyzerImplTest {

    @Mock
    Notificator notificator;

    ArbitrageOpportunityInOutAnalyzerImpl impl;

    @Before
    public void before(){
        impl = new ArbitrageOpportunityInOutAnalyzerImpl(notificator, new BigDecimal("10.0"), new BigDecimal("5.0"));
    }

    @Test
    public void Analyze_SimpleOpportunity_ShouldNotifyIN() {
        ArbitrageOpportunity opportunity = new ArbitrageOpportunity("exchangeA", "exchangeB", BigDecimal.valueOf(10), BigDecimal.valueOf(12), "currencyA", "currencyB");
        impl.analyze(opportunity);
        Mockito.verify(notificator).notifyIN(opportunity);
    }

    @Test
    public void Analyze_SimpleOpportunity_ShouldNotNotify() {
        ArbitrageOpportunity opportunity = new ArbitrageOpportunity("exchangeA", "exchangeB", BigDecimal.valueOf(10), BigDecimal.valueOf(10.1), "currencyA", "currencyB");
        impl.analyze(opportunity);
        Mockito.verifyNoMoreInteractions(notificator);
    }

    @Test
    public void Analyze_ReplacedPrices_ShouldNotNotify() {
        ArbitrageOpportunity opportunity = new ArbitrageOpportunity("exchangeA", "exchangeB", BigDecimal.valueOf(20), BigDecimal.valueOf(10), "currencyA", "currencyB");
        impl.analyze(opportunity);
        Mockito.verifyNoMoreInteractions(notificator);
    }


    @Test
    public void Analyze_SimpleOpportunity_ShouldNotifyOUT() {
        String currencyA = "currencyA";
        String currencyB = "currencyB";
        ArbitrageOpportunity opportunity1 = new ArbitrageOpportunity("exchangeA", "exchangeB", BigDecimal.valueOf(10), BigDecimal.valueOf(12), currencyA, currencyB);
        ArbitrageOpportunity opportunity2 = new ArbitrageOpportunity("exchangeA", "exchangeB", BigDecimal.valueOf(10), BigDecimal.valueOf(10.1), currencyA, currencyB);
        impl.analyze(opportunity1);
        Mockito.verify(notificator).notifyIN(opportunity1);
        impl.analyze(opportunity2);
        Mockito.verify(notificator).notifyOUT(opportunity2);
        Mockito.verifyNoMoreInteractions(notificator);
    }

    @Test
    public void Analyze_TwoOpportunities_ShouldNotifyINOnce() {
        String currencyA = "currencyA";
        String currencyB = "currencyB";
        ArbitrageOpportunity opportunity1 = new ArbitrageOpportunity(new String("exchangeA"), new String("exchangeB"), BigDecimal.valueOf(10), BigDecimal.valueOf(12), currencyA, currencyB);
        ArbitrageOpportunity opportunity2 = new ArbitrageOpportunity(new String("exchangeA"), new String("exchangeB"), BigDecimal.valueOf(10), BigDecimal.valueOf(12), currencyA, currencyB);
        impl.analyze(opportunity1);
        Mockito.verify(notificator).notifyIN(opportunity1);
        impl.analyze(opportunity2);
        Mockito.verifyNoMoreInteractions(notificator);
    }

    @Test
    public void Analyze_TwoOpportunitiesAndOut_ShouldNotifyINOnceAndOUT() {
        String currencyA = "currencyA";
        String currencyB = "currencyB";
        ArbitrageOpportunity opportunity1 = new ArbitrageOpportunity("exchangeA", "exchangeB", BigDecimal.valueOf(10), BigDecimal.valueOf(12), currencyA, currencyB);
        ArbitrageOpportunity opportunity2 = new ArbitrageOpportunity("exchangeA", "exchangeB", BigDecimal.valueOf(10), BigDecimal.valueOf(11), currencyA, currencyB);
        ArbitrageOpportunity opportunity3 = new ArbitrageOpportunity("exchangeA", "exchangeB", BigDecimal.valueOf(10), BigDecimal.valueOf(10.1), currencyA, currencyB);
        impl.analyze(opportunity1);
        Mockito.verify(notificator).notifyIN(opportunity1);
        impl.analyze(opportunity2);
        Mockito.verifyNoMoreInteractions(notificator);
        impl.analyze(opportunity3);
        Mockito.verify(notificator).notifyOUT(opportunity3);
        Mockito.verifyNoMoreInteractions(notificator);
    }

    @Test
    public void Analyze_TwoOpportunitiesDifferentBuyExchange_ShouldNotifyINOnceAndBuyExchangeChange() {
        ArbitrageOpportunity opportunity1 = new ArbitrageOpportunity("exchangeA", "exchangeB", BigDecimal.valueOf(10), BigDecimal.valueOf(12), "currencyA", "currencyB");
        ArbitrageOpportunity opportunity2 = new ArbitrageOpportunity("exchangeC", "exchangeB", BigDecimal.valueOf(10), BigDecimal.valueOf(12), "currencyA", "currencyB");
        impl.analyze(opportunity1);
        Mockito.verify(notificator).notifyIN(opportunity1);
        impl.analyze(opportunity2);
        Mockito.verify(notificator).notifyBuyExchangeChange(opportunity2);
        Mockito.verifyNoMoreInteractions(notificator);
    }

    @Test
    public void Analyze_TwoOpportunitiesDifferentSellExchange_ShouldNotifyINOnceAndSellExchangeChange() {
        ArbitrageOpportunity opportunity1 = new ArbitrageOpportunity("exchangeA", "exchangeB", BigDecimal.valueOf(10), BigDecimal.valueOf(12), "currencyA", "currencyB");
        ArbitrageOpportunity opportunity2 = new ArbitrageOpportunity("exchangeA", "exchangeC", BigDecimal.valueOf(10), BigDecimal.valueOf(12), "currencyA", "currencyB");
        impl.analyze(opportunity1);
        Mockito.verify(notificator).notifyIN(opportunity1);
        impl.analyze(opportunity2);
        Mockito.verify(notificator).notifySellExchangeChange(opportunity2);
        Mockito.verifyNoMoreInteractions(notificator);
    }

    @Test
    public void Analyze_TwoOpportunitiesDifferentExchanges_ShouldNotifyINOnceAndBothExchangeChange() {
        ArbitrageOpportunity opportunity1 = new ArbitrageOpportunity("exchangeA", "exchangeB", BigDecimal.valueOf(10), BigDecimal.valueOf(12), "currencyA", "currencyB");
        ArbitrageOpportunity opportunity2 = new ArbitrageOpportunity("exchangeC", "exchangeD", BigDecimal.valueOf(10), BigDecimal.valueOf(12), "currencyA", "currencyB");
        impl.analyze(opportunity1);
        Mockito.verify(notificator).notifyIN(opportunity1);
        impl.analyze(opportunity2);
        Mockito.verify(notificator).notifyBothExchangesChange(opportunity2);
        Mockito.verifyNoMoreInteractions(notificator);
    }

    @Test
    public void Analyze_TwoOpportunitiesOneToLow_ShouldNotifyINOnceAndOUT() {
        String currencyA = "currencyA";
        String currencyB = "currencyB";
        ArbitrageOpportunity opportunity1 = new ArbitrageOpportunity("exchangeA", "exchangeB", BigDecimal.valueOf(10), BigDecimal.valueOf(12), currencyA, currencyB);
        ArbitrageOpportunity opportunity2 = new ArbitrageOpportunity("exchangeA", "exchangeB", BigDecimal.valueOf(10), BigDecimal.valueOf(12), currencyA, currencyB);
        ArbitrageOpportunity opportunity3 = new ArbitrageOpportunity("exchangeA", "exchangeB", BigDecimal.valueOf(10), BigDecimal.valueOf(10.1), currencyA, currencyB);
        impl.analyze(opportunity1);
        Mockito.verify(notificator).notifyIN(opportunity1);
        impl.analyze(opportunity2);
        Mockito.verifyNoMoreInteractions(notificator);
        impl.analyze(opportunity3);
        Mockito.verify(notificator).notifyOUT(opportunity3);
        Mockito.verifyNoMoreInteractions(notificator);
    }

}