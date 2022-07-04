package org.transfer.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;


@EnableDiscoveryClient
@EnableFeignClients
@SpringBootApplication
public class TransferServiceApplication {

    private final static Logger logger = LoggerFactory.getLogger(TransferServiceApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(TransferServiceApplication.class, args);
        logger.info("TransferServiceApplication successfully initialized.");
    }
}
