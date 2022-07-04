package org.transfer.api;


import org.common.api.dto.AccountDto;
import org.common.api.dto.TransferEventDto;
import org.common.api.request.TransferRequest;
import org.common.api.response.AccountDetailsResponse;
import org.common.api.response.RecordTransferEventResponse;
import org.common.api.response.TransferResponse;
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
        AccountDetailsResponse accountDetailsResponse = new AccountDetailsResponse();
        List<AccountDto> accountDtoList = new ArrayList<>();

        AccountDto accountDto1 = new AccountDto();
        accountDto1.setAccountNumber("sa1001");
        accountDto1.setAccountBalance(BigDecimal.valueOf(5000));

        AccountDto accountDto2 = new AccountDto();
        accountDto2.setAccountNumber("sa1002");
        accountDto2.setAccountBalance(BigDecimal.valueOf(1000));

        accountDtoList.add(accountDto1);
        accountDtoList.add(accountDto2);

        accountDetailsResponse.setListAccountDto(accountDtoList);
        when(accountServiceClient.getAccountDetails(Mockito.any())).thenReturn(accountDetailsResponse);

        RecordTransferEventResponse recordTransferEventResponse = new RecordTransferEventResponse();
        TransferEventDto transferEventDto = new TransferEventDto();
        transferEventDto.setEventId(1);
        transferEventDto.setSourceAccountNumber("sa1001");
        transferEventDto.setDestinationAccountNumber("sa1002");
        transferEventDto.setTransferAmount(BigDecimal.valueOf(1000));

        recordTransferEventResponse.setTransferEventDto(transferEventDto);
        when(eventServiceClient.recordTransferEvent(Mockito.any())).thenReturn(recordTransferEventResponse);

        accountDetailsResponse = new AccountDetailsResponse();
        accountDtoList = new ArrayList<>();

        accountDto1 = new AccountDto();
        accountDto1.setAccountNumber("sa1001");
        accountDto1.setAccountBalance(BigDecimal.valueOf(4000));

        accountDto2 = new AccountDto();
        accountDto2.setAccountNumber("sa1002");
        accountDto2.setAccountBalance(BigDecimal.valueOf(2000));

        accountDtoList.add(accountDto1);
        accountDtoList.add(accountDto2);

        accountDetailsResponse.setListAccountDto(accountDtoList);
        when(accountServiceClient.updateAccountDetails(Mockito.any())).thenReturn(accountDetailsResponse);

    }

    @Test
    @DisplayName("Test transfer from source to destination when:" +
            "1. Both Accounts are valid" +
            "2. Source Account has sufficient account balance")
    void testTransferSufficientFunds() {
        TransferRequest transferRequest = new TransferRequest();
        transferRequest.setSourceAccountNumber("sa1001");
        transferRequest.setDestinationAccountNumber("sa1002");
        transferRequest.setTransferAmount(BigDecimal.valueOf(1000));
        TransferResponse transferResponse = transferServiceController.transfer(transferRequest);

        assertTrue(transferResponse.getTransferStatus());
    }

    @Test
    @DisplayName("Test transfer from source to destination when:" +
            "1. Both Accounts are valid" +
            "2. Source Account has insufficient account balance")
    void testTransferInsufficientFunds() {
        TransferRequest transferRequest = new TransferRequest();
        transferRequest.setSourceAccountNumber("sa1001");
        transferRequest.setDestinationAccountNumber("sa1002");
        transferRequest.setTransferAmount(BigDecimal.valueOf(10000));
        TransferResponse transferResponse = transferServiceController.transfer(transferRequest);

        assertFalse(transferResponse.getTransferStatus());
    }

    @Test
    @DisplayName("Test transfer from source to destination when:" +
            "1. Both Accounts are valid" +
            "2. Source Account and destination account is same")
    void testTransferAcrossSameAccount() {
        TransferRequest transferRequest = new TransferRequest();
        transferRequest.setSourceAccountNumber("sa1001");
        transferRequest.setDestinationAccountNumber("sa1001");
        transferRequest.setTransferAmount(BigDecimal.valueOf(10000));
        TransferResponse transferResponse = transferServiceController.transfer(transferRequest);

        assertFalse(transferResponse.getTransferStatus());
    }

    @Test
    @DisplayName("Test transfer from source to destination when:" +
            "1. Both Accounts are valid" +
            "2. Zero/Negative Fund transfer")
    void testTransferZeroOrNegativeFundTransfer() {
        TransferRequest transferRequest = new TransferRequest();
        transferRequest.setSourceAccountNumber("sa1001");
        transferRequest.setDestinationAccountNumber("sa1001");
        transferRequest.setTransferAmount(BigDecimal.valueOf(0));
        TransferResponse transferResponse = transferServiceController.transfer(transferRequest);

        assertFalse(transferResponse.getTransferStatus());

        transferRequest = new TransferRequest();
        transferRequest.setSourceAccountNumber("sa1001");
        transferRequest.setDestinationAccountNumber("sa1001");
        transferRequest.setTransferAmount(BigDecimal.valueOf(-100));
        transferResponse = transferServiceController.transfer(transferRequest);

        assertFalse(transferResponse.getTransferStatus());
    }
}
