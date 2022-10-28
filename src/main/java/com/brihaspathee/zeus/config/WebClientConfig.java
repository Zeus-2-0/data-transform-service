package com.brihaspathee.zeus.config;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Created in Intellij IDEA
 * User: Balaji Varadharajan
 * Date: 21, October 2022
 * Time: 5:25 AM
 * Project: Zeus
 * Package Name: com.brihaspathee.zeus.config
 * To change this template use File | Settings | File and Code Template
 */
@Component
public class WebClientConfig {

    /**
     * Return web client object
     * @return
     */
    @Bean
    public WebClient getWebClient(){
        return WebClient.builder().build();
    }
}
