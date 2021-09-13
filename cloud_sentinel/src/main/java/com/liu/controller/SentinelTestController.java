package com.liu.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.liu.exception.ConstomerBlockHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class SentinelTestController {

    @GetMapping(value = "/hello")
    @SentinelResource(value = "hello",fallback = "fallbackHandler")
    public String hello() {
        log.info("****hello-hello-hello-hello-hello");
        return "Hello Sentinel";
    }
    /**
     * 自定义熔断异常
     * 返回值和参数要跟目标函数一样
     */
    public String fallbackHandler(){
        return "服务被熔断了，不要调用!";
    }

    @GetMapping(value = "/testrd")
    @SentinelResource(value = "/testrd")
    public String testrd() {
        log.info("****hello-hello-hello-hello-hello");
        int a = 5/0;
        return "Hello testrd";
    }
    @GetMapping(value = "/hello2")
    @SentinelResource(value = "hello2",blockHandler = "blockHandler",blockHandlerClass = ConstomerBlockHandler.class)
    public String hello2() {
        log.info("****hello2-hello2-hello-hello-hello");
        return "Hello2 Sentinel";
    }



    /**
     * 自定义异常策略
     * 返回值和参数要跟目标函数一样，参数可以追加BlockException
     */
    public String handleException( BlockException exception){
        log.info("flow exception{}",exception.getClass().getCanonicalName());
        return  "达到阈值了,不要再访问了!";
    }



    @GetMapping("/fallback")
    @SentinelResource(value = "fallback",
            fallback = "fallback")
    public String fallback() {
        int i = 3 / 0;
        return "fallback";
    }

    public String fallback(Throwable throwable) {
        return throwable.getMessage();
    }


}
