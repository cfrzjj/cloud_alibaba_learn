package com.liu.learn.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import springfox.documentation.swagger.web.SwaggerResourcesProvider;

/**
 * @description: 提供Swagger3 获取注册中心的服务资源  http://localhost:5000/swagger-resources
 * @date 2021/4/28 22:33
 */
@Slf4j
@Component
public class SwaggerResourceHandler implements HandlerFunction<ServerResponse> {

    @Autowired
    private SwaggerResourcesProvider swaggerResources;

    @Override
    public Mono<ServerResponse> handle(ServerRequest request) {
        return ServerResponse.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(swaggerResources.get()));
    }

}


