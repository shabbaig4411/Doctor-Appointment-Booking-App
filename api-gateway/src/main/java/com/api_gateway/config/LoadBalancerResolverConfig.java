package com.api_gateway.config;

import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.client.loadbalancer.reactive.ReactiveLoadBalancer;
import org.springframework.cloud.client.loadbalancer.reactive.ReactorLoadBalancerExchangeFilterFunction;
import org.springframework.cloud.client.loadbalancer.reactive.LoadBalancerClientRequestTransformer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Collections;
import java.util.List;

@Configuration
public class LoadBalancerResolverConfig {

    @Bean
    @LoadBalanced
    public WebClient.Builder loadBalancedWebClientBuilder() {
        return WebClient.builder();
    }

    @Bean
    public ReactorLoadBalancerExchangeFilterFunction lbFunction(
            ReactiveLoadBalancer.Factory<ServiceInstance> lbFactory
    ) {
        // Pass an empty list for transformers
        List<LoadBalancerClientRequestTransformer> transformers = Collections.emptyList();

        return new ReactorLoadBalancerExchangeFilterFunction(lbFactory, transformers);
    }
}
