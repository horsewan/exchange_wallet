package com.zwan.bc.wallet.config;

import com.zwan.bc.wallet.component.ActClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.MalformedURLException;
import java.net.URISyntaxException;

@Configuration
public class JsonrpcConfig {

    @Bean
    public ActClient setActClient(@Value("${coin.rpc}") String url) throws MalformedURLException, URISyntaxException {
        System.out.println("coin.rpc="+url);
        ActClient client = new ActClient(url);
        return client;
    }
}
