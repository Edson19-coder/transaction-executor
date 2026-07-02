package com.spin.transaction_executor.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.time.Duration;

@Configuration
public class RestClientConfig {
    @Value("${app.config.rest-client.timeout.connection}")
    private long connectionTimeout;
    @Value("${app.config.rest-client.timeout.read}")
    private long readTimeout;

    @Bean
    public RestClient.Builder restClientBuilder() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout((int) Duration.ofSeconds(connectionTimeout).toMillis()); // Tiempo en segundo maximo para conectar
        factory.setReadTimeout((int) Duration.ofSeconds(readTimeout).toMillis()); // Tiempo en segundo maximo para responder

        return RestClient.builder().requestFactory(factory);
    }
}