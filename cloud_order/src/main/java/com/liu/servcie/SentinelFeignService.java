package com.liu.servcie;

import com.liu.servcie.impl.ConstomerSentinelService;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;

@Component
@FeignClient(value = "sentinel-service", fallback = ConstomerSentinelService.class)
public interface SentinelFeignService {

    @GetMapping(value = "/hello")
    public String  hello();
}
