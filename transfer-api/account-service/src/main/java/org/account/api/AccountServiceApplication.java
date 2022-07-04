package org.account.api;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * Application offers all account related operations w.r.t.
 * different accounts.
 */
@EnableDiscoveryClient
@SpringBootApplication
public class AccountServiceApplication {

    private final static Logger logger = LoggerFactory.getLogger(AccountServiceApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(AccountServiceApplication.class, args);
        logger.info("AccountServiceApplication successfully initialized.");
    }
}
