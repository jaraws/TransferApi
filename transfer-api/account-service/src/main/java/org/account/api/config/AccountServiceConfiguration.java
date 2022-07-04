package org.account.api.config;

import org.account.api.entity.Account;
import org.common.api.dto.AccountDto;
import org.common.api.response.AccountDetailsResponse;
import org.common.api.response.UpdateAccountDetailsResponse;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
/**
 * Configurations class defines configuration for Account Service
 */
@Configuration
public class AccountServiceConfiguration {

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public AccountDetailsResponse accountDetailsResponse() {
        return new AccountDetailsResponse();
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public AccountDto accountDto() {
        return new AccountDto();
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public Account account() {
        return new Account();
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public UpdateAccountDetailsResponse updateAccountDetailsResponse() {
        return new UpdateAccountDetailsResponse();
    }

}
