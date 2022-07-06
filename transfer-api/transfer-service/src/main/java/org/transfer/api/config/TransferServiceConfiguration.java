package org.transfer.api.config;

import org.common.api.dto.TransferEventDto;
import org.common.api.dto.TransferFundDto;
import org.common.api.request.AccountDetailsRequest;
import org.common.api.request.RecordTransferEventRequest;
import org.common.api.request.UpdateAccountDetailsRequest;
import org.common.api.response.TransferResponse;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
public class TransferServiceConfiguration {

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public TransferResponse transferResponse() {
        return new TransferResponse();
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public AccountDetailsRequest accountDetailsRequest() {
        return new AccountDetailsRequest();
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public RecordTransferEventRequest recordTransferEventRequest() {
        return new RecordTransferEventRequest();
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public UpdateAccountDetailsRequest updateAccountDetailsRequest() {
        return new UpdateAccountDetailsRequest();
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public TransferFundDto transferFundDto() {
        return new TransferFundDto();
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public TransferEventDto transferEventDto() {
        return new TransferEventDto();
    }

}
