package com.liu.controller;

import com.liu.config.ConsumerDiscoveryClient;
import com.liu.entities.CommonResult;
import com.liu.entities.Payment;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@Slf4j
public class OrderFirstController {

    private RestTemplate restTemplate = new RestTemplate();

    @Autowired
    private ConsumerDiscoveryClient consumerDiscoveryClient;

   /* @GetMapping("/consumer/payment/get/{id}")
    public com.liu.entities.CommonResult<com.liu.entities.Payment> getPaymentById(@PathVariable("id") Long id) {
        return restTemplate.getForObject(PAYMENT_URL+"/payment/get/"+id, com.liu.entities.CommonResult.class);
    }*/
    @GetMapping("/consumer/payment/get/{id}")
    public CommonResult<Payment> getPaymentById(@PathVariable("id") Long id) {
       //return restTemplate.getForObject(PAYMENT_URL+"/payment/get/"+id, CommonResult.class);
       String url = consumerDiscoveryClient.getUrl("%s/payment/get/"+id);
        System.out.println("url:"+url);
        return restTemplate.getForObject(url,CommonResult.class);
    }

   // @GetMapping("/consumer/payment/create")
   /* public com.liu.entities.CommonResult<com.liu.entities.Payment> create(com.liu.entities.Payment payment) {
        return restTemplate.postForObject(PAYMENT_URL+"/payment/create",payment, com.liu.entities.CommonResult.class);
    }*/
}