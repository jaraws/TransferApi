package org.event.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Application offers all Event related operations
 * on an account. For eg: Transfer of funds from
 * source account to destination account.
 */
@EnableDiscoveryClient
@EnableTransactionManagement
@SpringBootApplication
public class EventServiceApplication {

    private final static Logger logger = LoggerFactory.getLogger(EventServiceApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(EventServiceApplication.class, args);
        logger.info("EventServiceApplication successfully initialized.");
    }
}