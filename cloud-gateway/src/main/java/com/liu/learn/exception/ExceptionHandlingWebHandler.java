package com.liu.learn.exception;

import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebExceptionHandler;
import org.springframework.web.server.WebHandler;
import org.springframework.web.server.handler.WebHandlerDecorator;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

//import org.springframework.web.server.WebExceptionHandler;

public class ExceptionHandlingWebHandler extends WebHandlerDecorator {
    private final List<WebExceptionHandler> exceptionHandlers;

    public ExceptionHandlingWebHandler(WebHandler delegate, List<WebExceptionHandler> handlers) {
        super(delegate);
        this.exceptionHandlers = Collections.unmodifiableList(new ArrayList(handlers));
    }

    public List<WebExceptionHandler> getExceptionHandlers() {
        return this.exceptionHandlers;
    }

    public Mono<Void> handle(ServerWebExchange exchange) {
        Mono completion;
        try {
            completion = super.handle(exchange);
        } catch (Throwable var5) {
            completion = Mono.error(var5);
        }

        WebExceptionHandler handler;
        // 获取全局的 WebExceptionHandler 执行
        for(Iterator var3 = this.exceptionHandlers.iterator(); var3.hasNext(); completion = completion.onErrorResume((ex) -> {
            return "";
        })) {
            handler = (WebExceptionHandler)var3.next();
        }

        return completion;
    }
}

