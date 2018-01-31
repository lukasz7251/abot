package com.labreh.abot.configuration;

import com.google.common.collect.ImmutableSet;
import com.google.common.reflect.ClassPath;
import org.knowm.xchange.Exchange;
import org.knowm.xchange.ExchangeFactory;
import org.knowm.xchange.ExchangeSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.io.IOException;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Configuration
public class ExchangesConfiguration {

    @Autowired
    private Environment env;

    @Bean
    public List<Exchange> exchanges() throws IOException {
        String packageName = Exchange.class.getPackage().getName();
        ClassLoader classLoader = this.getClass().getClassLoader();
        ImmutableSet<ClassPath.ClassInfo> exchangeClasses = ClassPath.from(classLoader).getTopLevelClassesRecursive(packageName);
        return exchangeClasses.stream()
                .filter(c->!c.getSimpleName().equals("Exchange"))
                .filter(c->!c.getSimpleName().equals("BaseExchange"))
                .filter(c->c.getSimpleName().endsWith("Exchange"))
                .map(this::createExchange)
                .collect(toList());
    }

    private Exchange createExchange(ClassPath.ClassInfo ec) {
        Exchange exchange = ExchangeFactory.INSTANCE.createExchange(ec.getName());
        ExchangeSpecification exchangeSpecification = exchange.getDefaultExchangeSpecification();
        String exchangeName = exchangeSpecification.getExchangeName().toLowerCase();
        exchangeSpecification.setApiKey(env.getProperty(exchangeName + ".api_key"));
        exchangeSpecification.setSecretKey(env.getProperty(exchangeName + ".api_secret"));
        return ExchangeFactory.INSTANCE.createExchange(exchangeSpecification);
    }

}
