package com.liu.config;

import com.alibaba.cloud.sentinel.annotation.SentinelRestTemplate;
import com.liu.entities.ExceptionUtils;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
@LoadBalancerClient(name = "payment-service")
//@SentinelRestTemplate
        //(fallbackClass = ExceptionUtils.class,fallback = "handleFallback",blockHandler = "handleBlock" ,blockHandlerClass = ExceptionUtils.class)
public class ApplicationContextConfig {

    @Bean
    @LoadBalanced
    @SentinelRestTemplate(fallbackClass = ExceptionUtils.class,fallback = "handleFallback",blockHandler = "handleBlock" ,blockHandlerClass = ExceptionUtils.class)
    public RestTemplate getRestTemplate() {
        return new RestTemplate();
    }
}