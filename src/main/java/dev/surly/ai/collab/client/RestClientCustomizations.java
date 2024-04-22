package dev.surly.ai.collab.client;

import dev.surly.ai.collab.log.LoggingInterceptor;
import org.springframework.boot.web.client.ClientHttpRequestFactories;
import org.springframework.boot.web.client.ClientHttpRequestFactorySettings;
import org.springframework.boot.web.client.RestClientCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class RestClientCustomizations {

    /**
     * Customizes the timeouts for RestClient.
     * This is necessary so the Spring AI ChatClient doesn't timeout.
     *
     * @return
     */
    @Bean
    public RestClientCustomizer restClientCustomizer() {
        return restClientBuilder -> restClientBuilder
                .requestFactory(ClientHttpRequestFactories.get(
                        ClientHttpRequestFactorySettings.DEFAULTS
                                .withConnectTimeout(Duration.ofSeconds(5))
                                .withReadTimeout(Duration.ofSeconds(60))
                ))
                .requestInterceptor(new LoggingInterceptor());
    }
}
