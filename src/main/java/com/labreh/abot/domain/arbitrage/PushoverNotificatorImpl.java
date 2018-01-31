package com.labreh.abot.domain.arbitrage;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.io.ClassPathTemplateLoader;
import com.github.jknack.handlebars.io.TemplateLoader;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.request.HttpRequestWithBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class PushoverNotificatorImpl implements Notificator {

    private String appToken;
    private String url;
    private String user;
    private final Template inTemplate;
    private final Template outTemplate;
    private final Template buyChangedTemplate;
    private final Template sellChangedTemplate;
    private final Template bothChangedTemplate;

    @Autowired
    public PushoverNotificatorImpl(
            @Value("${pushover.app_token}") String appToken,
            @Value("${pushover.url}") String url,
            @Value("${pushover.user}") String user
    ) throws IOException {
        this.appToken = appToken;
        this.url = url;
        this.user = user;

        Handlebars handlebars = new Handlebars(new ClassPathTemplateLoader("/templates"));
        inTemplate = handlebars.compile("in");
        outTemplate = handlebars.compile("out");
        buyChangedTemplate = handlebars.compile("buy_changed");
        sellChangedTemplate = handlebars.compile("sell_changed");
        bothChangedTemplate = handlebars.compile("both_changed");
    }

    @Override
    public void notifyIN(ArbitrageOpportunity opportunity){
        try {
            createBaseRequest(inTemplate.apply(opportunity)).asJsonAsync();
        } catch (IOException e) {
            throw new RuntimeException("");
        }
    }

    @Override
    public void notifyOUT(ArbitrageOpportunity opportunity){
        try {
            createBaseRequest(outTemplate.apply(opportunity)).asJsonAsync();
        } catch (IOException e) {
            throw new RuntimeException("");
        }
    }

    @Override
    public void notifyBuyExchangeChange(ArbitrageOpportunity opportunity){
        try {
            createBaseRequest(buyChangedTemplate.apply(opportunity)).asJsonAsync();
        } catch (IOException e) {
            throw new RuntimeException("");
        }
    }

    @Override
    public void notifySellExchangeChange(ArbitrageOpportunity opportunity){
        try {
            createBaseRequest(sellChangedTemplate.apply(opportunity)).asJsonAsync();
        } catch (IOException e) {
            throw new RuntimeException("");
        }
    }

    @Override
    public void notifyBothExchangesChange(ArbitrageOpportunity opportunity){
        try {
            createBaseRequest(bothChangedTemplate.apply(opportunity)).asJsonAsync();
        } catch (IOException e) {
            throw new RuntimeException("");
        }
    }

    private HttpRequestWithBody createBaseRequest(String message) {
        return Unirest.post(url)
                .queryString("token", appToken)
                .queryString("user", user)
                .queryString("message", message)
                .queryString("html", "1")
                .queryString("priority", "1");
    }

}
