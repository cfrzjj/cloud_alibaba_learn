package com.liu.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@Slf4j
public class OrderSecondController {

   // String PAYMENT_URL = "http://localhost:8071/";
   String PAYMENT_URL = "http://sentinel-service";

    @Autowired
    private RestTemplate restTemplate ;



   @GetMapping("/consumer2/payment/get/{id}")
    public com.liu.entities.CommonResult<com.liu.entities.Payment> getPaymentById(@PathVariable("id") Long id) {
        return restTemplate.getForObject(PAYMENT_URL+"/payment/get/"+id, com.liu.entities.CommonResult.class);
    }
   @GetMapping("/consumer2/hello")
    public String hello() {
        return restTemplate.getForObject(PAYMENT_URL+"/hello", String.class);
    }

}