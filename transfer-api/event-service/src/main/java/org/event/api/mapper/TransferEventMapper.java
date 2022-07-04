package org.event.api.mapper;

import org.common.api.dto.TransferEventDto;
import org.event.api.entity.TransferEvent;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.function.Function;

/**
 * TransferEventMapper maps TransferEvent entity to TransferEventDto
 * and TransferEventDto to TransferEvent entity.
 */
@Service
public class TransferEventMapper {

    @Autowired
    private ObjectFactory<TransferEvent> transferEventObjectFactory;

    @Autowired
    private ObjectFactory<TransferEventDto> transferEventDtoObjectFactory;

    private TransferEventDto getTransferEventDto() {
        return transferEventDtoObjectFactory.getObject();
    }

    private TransferEvent getTransferEventEntity() {
        return transferEventObjectFactory.getObject();
    }

    public final Function<TransferEvent, TransferEventDto> transferEventEntityToDtoMapper = eventEntity -> {
        TransferEventDto eventDto = getTransferEventDto();
        eventDto.setEventId(eventEntity.getEventId());
        eventDto.setSourceAccountNumber(eventEntity.getSourceAccountNumber());
        eventDto.setDestinationAccountNumber(eventEntity.getDestinationAccountNumber());
        eventDto.setTransferAmount(eventEntity.getTransferAmount());
        return eventDto;
    };

    public final Function<TransferEventDto, TransferEvent> transferEventDtoToEntityMapper = eventDto -> {
        TransferEvent eventEntity = getTransferEventEntity();
        eventEntity.setSourceAccountNumber(eventDto.getSourceAccountNumber());
        eventEntity.setDestinationAccountNumber(eventDto.getDestinationAccountNumber());
        eventEntity.setTransferAmount(eventDto.getTransferAmount());
        return eventEntity;
    };
}
