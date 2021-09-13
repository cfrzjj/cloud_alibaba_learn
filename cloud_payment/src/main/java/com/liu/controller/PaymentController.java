package com.liu.controller;


import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.liu.entitis.CommonResult;
import com.liu.entitis.Payment;
import com.liu.service.PaymentService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@Slf4j
@RestController
@RequestMapping("/payment")
/**
 * 支付controller类
 */
@RefreshScope
@Api(value = "pay", tags = "支付")
public class PaymentController {

    @Value("${server.port}")
    private String serverPort;

    @Resource
    private PaymentService paymentService;

    @PostMapping(value = "/create")
	//添加  前
    /**
     * 添加 后
     */

    public CommonResult create(@RequestBody Payment payment){
        boolean  result=paymentService.save(payment);
        log.info("****插入结果："+result);
        if (result){
            return new CommonResult(444,"插入数据库成功",null);
        }else {
            return new CommonResult(200,"插入数据库失败",result);
        }
    }

    //通过id进行查询
    @GetMapping("/get/{id}")
    /**
     * 通过id进行查询
     */
	//通过id查询
    @SentinelResource(value = "getPaymentById")
    public CommonResult<com.liu.entitis.Payment> getPaymentById(@PathVariable("id") Long id ){
       Payment payment = paymentService.getById(id);
        log.info("****查询结果："+payment);
        if (payment != null){
            return new CommonResult(200,"查询成功"+serverPort,payment);
        }else {
            return new CommonResult(444,"没有对应记录，查询ID："+id,null);
        }
    }

    @GetMapping(value = "/hello")
    @SentinelResource("hello")
    public String hello() {
        log.info("****hello-hello-hello-hello-hello");
        return "Hello Sentinel";
    }

}