package org.event.api.config;

import org.common.api.dto.TransferEventDto;
import org.common.api.response.GetTransferEventsResponse;
import org.common.api.response.RecordTransferEventResponse;
import org.event.api.entity.TransferEvent;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
public class EventServiceConfiguration {

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public GetTransferEventsResponse transferEventsResponse() {
        return new GetTransferEventsResponse();
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public TransferEventDto transferEventDto() {
        return new TransferEventDto();
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public TransferEvent transferEventEntity() {
        return new TransferEvent();
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public RecordTransferEventResponse recordTransferEventResponse() {
        return new RecordTransferEventResponse();
    }
}
