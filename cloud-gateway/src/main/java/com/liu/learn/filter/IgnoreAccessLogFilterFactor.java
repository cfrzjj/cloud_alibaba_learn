package com.liu.learn.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class IgnoreAccessLogFilterFactor extends AbstractGatewayFilterFactory <IgnoreAccessLogFilterFactor.Config>{

    public IgnoreAccessLogFilterFactor() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        //config.setIgnoreGlobalFilter(true);
        return new InnerFilter(config);
    }

    /*public Mono filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        log.info(">>>>>>>>>>>>>>>>>>>局部过滤器");
        exchange.getAttributes().put(AccessLogFilter.ATTRIBUTE_IGNORE_LOG_GLOBAL_FILTER, true);
       return chain.filter(exchange);

    }*/


    /**
     * 创建一个内部类，来实现2个接口，指定顺序
     * 这里通过Ordered指定优先级
     */
    private class InnerFilter implements GatewayFilter, Ordered {

        private Config config;

        InnerFilter(Config config) {
            this.config = config;
        }

        @Override
        public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
            /*System.out.println("  pre 自定义过滤器工厂 AAAA  " + this.getClass().getSimpleName());
            boolean root = true == config.isIgnoreGlobalFilter();
            if (root) {
                System.out.println("  is root ");
            } else {
                System.out.println("  is no root ");
            }
            // 在then方法里的，相当于aop中的后置通知
            return chain.filter(exchange).then(Mono.fromRunnable(() -> {
                System.out.println("  post 自定义过滤器工厂 AAAA " + this.getClass().getSimpleName());
            }));*/
            log.info("进入innerFilter=====" + config.isIgnoreGlobalFilter());
            if (config.isIgnoreGlobalFilter() == true) {
                exchange.getAttributes().put(AccessLogFilter.ATTRIBUTE_IGNORE_LOG_GLOBAL_FILTER, true);
            }
            return chain.filter(exchange);
        }

        @Override
        public int getOrder() {
            return -1000;
        }
    }
    public static class Config {

        boolean ignoreGlobalFilter;

        public boolean isIgnoreGlobalFilter() {
            return ignoreGlobalFilter;
        }

        public void setIgnoreGlobalFilter(boolean ignoreGlobalFilter) {
            this.ignoreGlobalFilter = ignoreGlobalFilter;
        }
    }


    @Override
    public String name() {
        return "IgnoreAccessLogFilter";
    }
}
