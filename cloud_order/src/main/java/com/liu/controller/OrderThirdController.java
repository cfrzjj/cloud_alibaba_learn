package com.liu.controller;

import com.liu.entities.CommonResult;
import com.liu.log.annotation.SysLogAnn;
import com.liu.servcie.PaymentFeignService;
import com.liu.servcie.SentinelFeignService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@Slf4j
@Api(value = "order", tags = "订单")
public class OrderThirdController {

    @Resource
    private PaymentFeignService paymentFeignService;
    @Resource
    private SentinelFeignService sentinelFeignService;

    @GetMapping(value = "/consumer3/payment/get/{id}")
    public CommonResult getPaymentById(@PathVariable("id") Long id){
        return paymentFeignService.getPaymentById(id);
    }

    @GetMapping(value = "/hello")
    @SysLogAnn("测试日志")
    public String  hello(){
        return sentinelFeignService.hello();
    }
}