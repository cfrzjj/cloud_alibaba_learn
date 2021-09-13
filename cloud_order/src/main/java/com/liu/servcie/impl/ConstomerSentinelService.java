package com.liu.servcie.impl;

import com.liu.servcie.SentinelFeignService;
import org.springframework.stereotype.Component;

@Component
public class ConstomerSentinelService implements SentinelFeignService {

    public String  hello(){

        return "进入兜底方法";
    }
}
