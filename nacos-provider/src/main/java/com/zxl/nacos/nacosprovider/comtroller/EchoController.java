package com.zxl.nacos.nacosprovider.comtroller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.zxl.nacos.nacosprovider.service.TestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class EchoController {

    Logger logger=  LoggerFactory.getLogger(EchoController.class);

    @Autowired
    private TestService testService;
    @Autowired
    DiscoveryClient discoveryClient;

    @GetMapping("/hi")
    @SentinelResource(value = "hi")
    public String hi(@RequestParam(value = "name",defaultValue = "archiz",required = false) String name){
        return "hi " + name;
    }

    @GetMapping(value = "/hello/{name}")
    public String apiHello(@PathVariable String name) {
        return testService.sayHello(name);
    }

    @GetMapping("/services")
    public String getServices(){
        List<String> serviceNames=discoveryClient.getServices();

        StringBuilder stringBuilder=new StringBuilder();
        for (String s: serviceNames){
            stringBuilder.append(s).append("\n");
            List<ServiceInstance> serviceInstances=discoveryClient.getInstances(s);
            if(serviceInstances!=null&&serviceInstances.size()>0){
                for (ServiceInstance serviceInstance: serviceInstances){
                    logger.info("serviceName:"+s+" host:"+serviceInstance.getHost()+" port:"+serviceInstance.getPort());
                }
            }
        }
        return stringBuilder.toString();
    }
}
