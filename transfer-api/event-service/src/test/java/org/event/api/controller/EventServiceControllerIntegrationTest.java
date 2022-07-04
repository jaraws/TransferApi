package org.event.api.controller;


import org.common.api.dto.TransferEventDto;
import org.common.api.request.RecordTransferEventRequest;
import org.common.api.response.GetTransferEventsResponse;
import org.common.api.response.RecordTransferEventResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Integration test cases to test functionalities of
 * EventServiceController
 */
@SpringBootTest
@AutoConfigureTestDatabase
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.yaml")
public class EventServiceControllerIntegrationTest {

    @Autowired
    private EventServiceController eventServiceController;

    @Test
    @DisplayName("Test getTransferEvents")
    void testGetTransferEvents() {
        GetTransferEventsResponse getTransferEventsResponse = eventServiceController.getTransferEvents();
        List<TransferEventDto> transferEventDtoList = getTransferEventsResponse.getTransferEventDtoList();

        assertTrue(transferEventDtoList.size() == 0);

        TransferEventDto transferEventDto = new TransferEventDto();
        transferEventDto.setTransferAmount(BigDecimal.valueOf(100));
        transferEventDto.setSourceAccountNumber("sa1001");
        transferEventDto.setDestinationAccountNumber("sa1002");
        RecordTransferEventRequest recordTransferEventRequest = new RecordTransferEventRequest();
        recordTransferEventRequest.setTransferEventDto(transferEventDto);

        eventServiceController.recordTransferEvent(recordTransferEventRequest);

        getTransferEventsResponse = eventServiceController.getTransferEvents();
        transferEventDtoList = getTransferEventsResponse.getTransferEventDtoList();

        assertTrue(transferEventDtoList.size() == 1);
    }

    @Test
    @DisplayName("Test recordTransferEvent")
    void testRecordTransferEvent() {

        TransferEventDto transferEventDto = new TransferEventDto();
        transferEventDto.setTransferAmount(BigDecimal.valueOf(100));
        transferEventDto.setSourceAccountNumber("sa1001");
        transferEventDto.setDestinationAccountNumber("sa1002");
        RecordTransferEventRequest recordTransferEventRequest = new RecordTransferEventRequest();
        recordTransferEventRequest.setTransferEventDto(transferEventDto);

        RecordTransferEventResponse recordTransferEventResponse = eventServiceController.recordTransferEvent(recordTransferEventRequest);

        assertTrue(recordTransferEventResponse.getTransferEventDto().getEventId() != 0);
        assertTrue("sa1001".equalsIgnoreCase(recordTransferEventResponse.getTransferEventDto().getSourceAccountNumber()));
        assertTrue("sa1002".equalsIgnoreCase(recordTransferEventResponse.getTransferEventDto().getDestinationAccountNumber()));
        assertTrue(recordTransferEventResponse.getTransferEventDto().getTransferAmount().compareTo(BigDecimal.valueOf(100)) == 0);

    }
}
