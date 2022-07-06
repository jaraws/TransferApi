package org.transfer.api;


import org.common.api.dto.AccountDto;
import org.common.api.dto.TransferEventDto;
import org.common.api.dto.TransferFundDto;
import org.common.api.exception.TransferException;
import org.common.api.request.TransferRequest;
import org.common.api.response.AccountDetailsResponse;
import org.common.api.response.RecordTransferEventResponse;
import org.common.api.response.TransferResponse;
import org.common.api.response.UpdateAccountDetailsResponse;
import org.common.api.util.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.context.WebApplicationContext;
import org.transfer.api.clients.AccountServiceClient;
import org.transfer.api.clients.EventServiceClient;
import org.transfer.api.controller.TransferServiceController;
import org.transfer.api.service.TransferService;
import org.transfer.api.service.impl.TransferServiceImpl;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureTestDatabase
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.yaml")
public class TransferServiceControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Mock
    private AccountServiceClient accountServiceClient;
    @Mock
    private EventServiceClient eventServiceClient;

    @Autowired
    @InjectMocks // auto inject mocked objects
    private TransferServiceImpl transferService ;

    @Autowired
    private TransferServiceController transferServiceController;

    @BeforeEach
    void mockFeignClientAndTransferObjects() throws NoSuchFieldException, IllegalAccessException {

        // Mocking Feign Clients
        TransferService transferService = webApplicationContext.getBean(TransferServiceImpl.class);
        Field fieldAccountServiceClient = TransferServiceImpl.class.getDeclaredField("accountServiceClient");
        fieldAccountServiceClient.setAccessible(true);
        fieldAccountServiceClient.set(transferService, this.accountServiceClient);

        Field fieldEventServiceClient = TransferServiceImpl.class.getDeclaredField("eventServiceClient");
        fieldEventServiceClient.setAccessible(true);
        fieldEventServiceClient.set(transferService, this.eventServiceClient);

        // Mocking Transfer Objects
        UpdateAccountDetailsResponse updateAccountDetailsResponse = getUpdateAccountDetailsResponse();
        when(accountServiceClient.updateAccountDetails(Mockito.any())).thenReturn(updateAccountDetailsResponse);

        RecordTransferEventResponse recordTransferEventResponse = getRecordTransferEventResponse();
        when(eventServiceClient.recordTransferEvent(Mockito.any())).thenReturn(recordTransferEventResponse);

    }

    private RecordTransferEventResponse getRecordTransferEventResponse() {
        RecordTransferEventResponse recordTransferEventResponse = new RecordTransferEventResponse();
        TransferEventDto transferEventDto = new TransferEventDto();
        transferEventDto.setEventId(1);
        transferEventDto.setSourceAccountNumber("sa1001");
        transferEventDto.setDestinationAccountNumber("sa1002");
        transferEventDto.setTransferAmount(BigDecimal.valueOf(1000));
        recordTransferEventResponse.setTransferEventDto(transferEventDto);
        return recordTransferEventResponse;
    }

    private UpdateAccountDetailsResponse getUpdateAccountDetailsResponse() {
        UpdateAccountDetailsResponse updateAccountDetailsResponse = new UpdateAccountDetailsResponse();
        TransferFundDto transferFundDto = new TransferFundDto();
        transferFundDto.setSourceAccountNumber("sa1001");
        transferFundDto.setDestinationAccountNumber("sa1002");
        transferFundDto.setTransferAmount(BigDecimal.valueOf(1000));
        updateAccountDetailsResponse.setTransferFundDto(new TransferFundDto());
        updateAccountDetailsResponse.setErrorCodeList(new ArrayList<>());
        return updateAccountDetailsResponse;
    }

    @Test
    @DisplayName("Test Transfer service when account and event service returns no error")
    void testTransferWhenAccountAndEventServiceReturnsNoError() {
        TransferRequest transferRequest = new TransferRequest();
        transferRequest.setSourceAccountNumber("sa1001");
        transferRequest.setDestinationAccountNumber("sa1002");
        transferRequest.setTransferAmount(BigDecimal.valueOf(1000));
        TransferResponse transferResponse = transferServiceController.transfer(transferRequest);

        assertTrue(transferResponse.getSourceAccountNumber().equalsIgnoreCase("sa1001"));
        assertTrue(transferResponse.getDestinationAccountNumber().equalsIgnoreCase("sa1002"));
        assertTrue(transferResponse.getTransferAmount().compareTo(BigDecimal.valueOf(1000)) ==0);
        assertTrue(transferResponse.getTransferStatus());
        assertTrue(transferResponse.getErrors() == null);
    }

    @Test
    @DisplayName("Test Transfer service when account service returns an error")
    void testTransferWhenAccountServiceReturnsNoError() {
        UpdateAccountDetailsResponse updateAccountDetailsResponse = getUpdateAccountDetailsResponse();
        updateAccountDetailsResponse.getErrorCodeList().add(ErrorCode.ERROR_GETTING_ACCOUNT_INFO);
        when(accountServiceClient.updateAccountDetails(Mockito.any())).thenReturn(updateAccountDetailsResponse);

        TransferRequest transferRequest = new TransferRequest();
        transferRequest.setSourceAccountNumber("sa1001");
        transferRequest.setDestinationAccountNumber("sa1002");
        transferRequest.setTransferAmount(BigDecimal.valueOf(1000));
        TransferResponse transferResponse = transferServiceController.transfer(transferRequest);

        assertTrue(transferResponse.getSourceAccountNumber().equalsIgnoreCase("sa1001"));
        assertTrue(transferResponse.getDestinationAccountNumber().equalsIgnoreCase("sa1002"));
        assertTrue(transferResponse.getTransferAmount().compareTo(BigDecimal.valueOf(1000)) ==0);
        assertFalse(transferResponse.getTransferStatus());
        assertTrue(transferResponse.getErrors() != null);
    }

    @Test
    @DisplayName("Test Transfer service when event service throws exception")
    void testTransferWhenEventServiceThrowsException() {

        when(eventServiceClient.recordTransferEvent(Mockito.any())).thenThrow(new RuntimeException());

        TransferRequest transferRequest = new TransferRequest();
        transferRequest.setSourceAccountNumber("sa1001");
        transferRequest.setDestinationAccountNumber("sa1002");
        transferRequest.setTransferAmount(BigDecimal.valueOf(1000));
        TransferResponse transferResponse = transferServiceController.transfer(transferRequest);

        assertTrue(transferResponse.getSourceAccountNumber().equalsIgnoreCase("sa1001"));
        assertTrue(transferResponse.getDestinationAccountNumber().equalsIgnoreCase("sa1002"));
        assertTrue(transferResponse.getTransferAmount().compareTo(BigDecimal.valueOf(1000)) ==0);
        assertTrue(transferResponse.getTransferStatus());
        assertTrue(transferResponse.getErrors() == null);
    }
}
