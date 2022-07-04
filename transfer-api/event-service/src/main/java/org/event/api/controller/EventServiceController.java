package org.event.api.controller;


import org.common.api.dto.TransferEventDto;
import org.common.api.exception.TransferException;
import org.common.api.request.RecordTransferEventRequest;
import org.common.api.response.GetTransferEventsResponse;
import org.common.api.response.RecordTransferEventResponse;
import org.common.api.util.ErrorCode;
import org.event.api.entity.TransferEvent;
import org.event.api.mapper.TransferEventMapper;
import org.event.api.service.EventService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * EventServiceController defines methods to add Events record and display events
 * 1. Get Transfer Events
 * 2. Record Transfer Events
 *
 */
@RestController
@RequestMapping("/event-api")
public class EventServiceController {
    private final static Logger logger = LoggerFactory.getLogger(EventServiceController.class);

    @Autowired
    private ObjectFactory<GetTransferEventsResponse> getTransferEventsResponseObjectFactory;

    @Autowired
    private ObjectFactory<RecordTransferEventResponse> recordTransferEventResponseObjectFactory;


    @Autowired
    private TransferEventMapper transferEventMapper;

    @Autowired
    private EventService eventService;


    /**
     * Get all TransferEvents
     * @return
     */
    @GetMapping(value = "/transfer-events")
    public GetTransferEventsResponse getTransferEvents() {
        logger.debug("Getting events for all transfers");
        GetTransferEventsResponse getTransferEventsResponse = getGetTransferEventsResponse();
        try {
            List<TransferEvent> transferEvents = eventService.getTransferEvents();
            List<TransferEventDto> transferEventDtoList = transferEvents.stream().map(transferEventMapper.transferEventEntityToDtoMapper).collect(Collectors.toList());
            getTransferEventsResponse.setTransferEventDtoList(transferEventDtoList);
            return getTransferEventsResponse;
        } catch (TransferException e) {
            logger.error("Error getting transfer events for all transfers {},{}", e.getErrorCode(), e);
            getTransferEventsResponse.setErrorCode(e.getErrorCode());
        }catch (Exception e){
            logger.error(e.getMessage(), e);
            getTransferEventsResponse.setErrorCode(ErrorCode.ERROR_GETTING_TRANSFER_EVENT_INFO);
        }

        return getTransferEventsResponse;

    }

    /**
     * Record transfer event
     * @param recordTransferEventRequest
     * @return
     */
    @PostMapping(value = "/transfer-event",
            produces = "application/json",
            consumes = "application/json")
    public RecordTransferEventResponse recordTransferEvent(@RequestBody RecordTransferEventRequest recordTransferEventRequest) {

        logger.debug("Saving transfer event record: {}", recordTransferEventRequest);
        RecordTransferEventResponse recordTransferEventResponse = getRecordTransferEventResponse();

        try {
            TransferEvent transferEvent = eventService.recordTransferEvent(transferEventMapper.transferEventDtoToEntityMapper.apply(recordTransferEventRequest.getTransferEventDto()));
            recordTransferEventResponse.setTransferEventDto(transferEventMapper.transferEventEntityToDtoMapper.apply(transferEvent));
            return recordTransferEventResponse;
        } catch (TransferException e) {
            logger.error("Error recording transfer event details {},{}", e.getErrorCode(), e);
            recordTransferEventResponse.setErrorCode(e.getErrorCode());
        }catch (Exception e){
            logger.error(e.getMessage(), e);
            recordTransferEventResponse.setErrorCode(ErrorCode.ERROR_RECORDING_TRANSFER_EVENT_INFO);
        }
        return recordTransferEventResponse;
    }

    private GetTransferEventsResponse getGetTransferEventsResponse() {
        return getTransferEventsResponseObjectFactory.getObject();
    }

    private RecordTransferEventResponse getRecordTransferEventResponse() {
        return recordTransferEventResponseObjectFactory.getObject();
    }
}
