package com.liu.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ConsumerDiscoveryClient {
    @Autowired
 private DiscoveryClient discoveryClient;

    public ServiceInstance getServiceInstance() {
         List<ServiceInstance> serviceInstances = discoveryClient.getInstances("payment-service");
        if (serviceInstances.size() == 0) {
         return null;
        }
        for (ServiceInstance serviceInstance : serviceInstances) {
            System.out.println("url:"+serviceInstance.getUri());
        }
    return serviceInstances.get(0);
}

 public String getUrl(String url) {
     ServiceInstance serviceInstance=this.getServiceInstance();
     if (serviceInstance==null)
        throw new RuntimeException("404 ,NOT FOUND");
     String urlR=String.format(url,serviceInstance.getUri().toString());
     return urlR;
 }
}
