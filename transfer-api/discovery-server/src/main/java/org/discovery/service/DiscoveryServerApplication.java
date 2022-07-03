package org.discovery.service;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@EnableEurekaServer
@SpringBootApplication
public class DiscoveryServerApplication {

    private final static Logger logger = LoggerFactory.getLogger(DiscoveryServerApplication.class);
    public static void main(String[] args) {
        SpringApplication.run(DiscoveryServerApplication.class, args);
        logger.info("DiscoveryServerApplication successfully initialized.");
    }
}
