package com.liu.learn.config;

import com.liu.learn.handler.SwaggerResourceHandler;
import com.liu.learn.handler.SwaggerSecurityHandler;
import com.liu.learn.handler.SwaggerUiHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;

@Slf4j
@Configuration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)//当Spring为web服务时，才使注解的类生效；通常是配置类；
public class GatewaySwaggerAutoConfiguration {

    @Autowired
    private SwaggerResourceHandler swaggerResourceHandler;

    @Autowired
    private SwaggerSecurityHandler swaggerSecurityHandler;

    @Autowired
    private SwaggerUiHandler swaggerUiHandler;

    /**
     * 路由映射,将Swagger相关URL映射到相应处理器上
     */
    @Bean
    public RouterFunction swaggerRouterFunction() {
        return RouterFunctions
                .route(RequestPredicates.GET("/swagger-resources")
                        .and(RequestPredicates.accept(MediaType.ALL)), swaggerResourceHandler)
                .andRoute(RequestPredicates.GET("/swagger-resources/configuration/ui")
                        .and(RequestPredicates.accept(MediaType.ALL)), swaggerUiHandler)
                .andRoute(RequestPredicates.GET("/swagger-resources/configuration/security")
                        .and(RequestPredicates.accept(MediaType.ALL)), swaggerSecurityHandler);
    }

}


