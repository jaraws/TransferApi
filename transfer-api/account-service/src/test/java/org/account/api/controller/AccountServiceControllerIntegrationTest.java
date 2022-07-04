package org.account.api.controller;

import org.common.api.dto.AccountDto;
import org.common.api.request.AccountDetailsRequest;
import org.common.api.request.UpdateAccountDetailsRequest;
import org.common.api.response.AccountDetailsResponse;
import org.common.api.response.UpdateAccountDetailsResponse;
import org.common.api.util.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;


/**
 * Integration test cases to test functionalities of
 * AccountServiceController
 */
@SpringBootTest
@AutoConfigureTestDatabase
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.yaml")
public class AccountServiceControllerIntegrationTest {
    @Autowired
    private AccountServiceController accountServiceController;

    @Test
    @DisplayName("Test getAllAccountDetails")
    void testGetAllAccountDetails() {
        AccountDetailsResponse accountDetailsResponse = accountServiceController.getAllAccountDetails();
        List<AccountDto> actualAccounts = accountDetailsResponse.getListAccountDto();

        List<AccountDto> expectedAccounts = new ArrayList<>();
        expectedAccounts.add(new AccountDto("sa1001", BigDecimal.valueOf(1000)));
        expectedAccounts.add(new AccountDto("sa1002", BigDecimal.valueOf(2000)));

        assertTrue(actualAccounts.size() == expectedAccounts.size());
        assertTrue(actualAccounts.stream().anyMatch(accountDto -> "sa1001".equalsIgnoreCase(accountDto.getAccountNumber())));
        assertTrue(actualAccounts.stream().anyMatch(accountDto -> "sa1002".equalsIgnoreCase(accountDto.getAccountNumber())));

        assertTrue(expectedAccounts.get(0).getAccountBalance().compareTo(actualAccounts.stream()
                .filter(accountDto -> "sa1001".equalsIgnoreCase(accountDto.getAccountNumber()))
                .findFirst().get().getAccountBalance()) == 0);

        assertTrue(expectedAccounts.get(1).getAccountBalance().compareTo(actualAccounts.stream()
                .filter(accountDto -> "sa1002".equalsIgnoreCase(accountDto.getAccountNumber()))
                .findFirst().get().getAccountBalance()) == 0);
    }

    @Test
    @DisplayName("Test getAccountDetails with a valid account number")
    void testGetAccountDetailsForValidAccount() {
        // Create AccountDetailsRequest
        List<String> listAccountNumbers = new ArrayList<>();
        listAccountNumbers.add("sa1001");
        AccountDetailsRequest accountDetailsRequest = new AccountDetailsRequest();
        accountDetailsRequest.setAccountNumbers(listAccountNumbers);

        AccountDetailsResponse accountDetailsResponse = accountServiceController.getAccountDetails(accountDetailsRequest);
        List<AccountDto> actualAccounts = accountDetailsResponse.getListAccountDto();

        List<AccountDto> expectedAccounts = new ArrayList<>();
        expectedAccounts.add(new AccountDto("sa1001", BigDecimal.valueOf(1000)));

        assertTrue(actualAccounts.size() == expectedAccounts.size());
        assertTrue(actualAccounts.stream().anyMatch(accountDto -> "sa1001".equalsIgnoreCase(accountDto.getAccountNumber())));

        assertTrue(expectedAccounts.get(0).getAccountBalance().compareTo(actualAccounts.stream()
                .filter(accountDto -> "sa1001".equalsIgnoreCase(accountDto.getAccountNumber()))
                .findFirst().get().getAccountBalance()) == 0);
    }

    @Test
    @DisplayName("Test getAccountDetails with a invalid account number")
    void testGetAccountDetailsForInValidAccount() {
        // Create AccountDetailsRequest
        List<String> listAccountNumbers = new ArrayList<>();
        listAccountNumbers.add("sa1006");
        AccountDetailsRequest accountDetailsRequest = new AccountDetailsRequest();
        accountDetailsRequest.setAccountNumbers(listAccountNumbers);

        AccountDetailsResponse accountDetailsResponse = accountServiceController.getAccountDetails(accountDetailsRequest);
        List<AccountDto> actualAccounts = accountDetailsResponse.getListAccountDto();

        assertTrue(actualAccounts.size() == 0);
    }

    @Test
    @DisplayName("Test updateAccountDetails with a valid account number")
    void testUpdateAccountDetailsForValidAccount() {
        // Create AccountDetailsRequest
        UpdateAccountDetailsRequest updateAccountDetailsRequest = new UpdateAccountDetailsRequest();
        List<AccountDto> listAccountDto = new ArrayList<>();
        listAccountDto.add(new AccountDto("sa1001", BigDecimal.valueOf(500)));
        listAccountDto.add(new AccountDto("sa1002", BigDecimal.valueOf(600)));
        updateAccountDetailsRequest.setListAccountDto(listAccountDto);

        UpdateAccountDetailsResponse updateAccountDetailsResponse = accountServiceController.updateAccountDetails(updateAccountDetailsRequest);
        List<AccountDto> updatedAccounts = updateAccountDetailsResponse.getAccountDtoList();

        List<AccountDto> expectedAccounts = new ArrayList<>();
        expectedAccounts.add(new AccountDto("sa1001", BigDecimal.valueOf(500)));
        expectedAccounts.add(new AccountDto("sa1002", BigDecimal.valueOf(600)));

        assertTrue(updatedAccounts.size() == expectedAccounts.size());
        assertTrue(updatedAccounts.stream().anyMatch(accountDto -> "sa1001".equalsIgnoreCase(accountDto.getAccountNumber())));
        assertTrue(updatedAccounts.stream().anyMatch(accountDto -> "sa1002".equalsIgnoreCase(accountDto.getAccountNumber())));

        assertTrue(expectedAccounts.get(0).getAccountBalance().compareTo(updatedAccounts.stream()
                .filter(accountDto -> "sa1001".equalsIgnoreCase(accountDto.getAccountNumber()))
                .findFirst().get().getAccountBalance()) == 0);

        assertTrue(expectedAccounts.get(1).getAccountBalance().compareTo(updatedAccounts.stream()
                .filter(accountDto -> "sa1002".equalsIgnoreCase(accountDto.getAccountNumber()))
                .findFirst().get().getAccountBalance()) == 0);
    }

    @Test
    @DisplayName("Test updateAccountDetails with a Invalid account number")
    void testUpdateAccountDetailsForInValidAccount() {
        // Create AccountDetailsRequest
        UpdateAccountDetailsRequest updateAccountDetailsRequest = new UpdateAccountDetailsRequest();
        List<AccountDto> listAccountDto = new ArrayList<>();
        listAccountDto.add(new AccountDto("sa1005", BigDecimal.valueOf(500)));
        listAccountDto.add(new AccountDto("sa1006", BigDecimal.valueOf(600)));
        updateAccountDetailsRequest.setListAccountDto(listAccountDto);

        UpdateAccountDetailsResponse updateAccountDetailsResponse = accountServiceController.updateAccountDetails(updateAccountDetailsRequest);
        List<AccountDto> updatedAccounts = updateAccountDetailsResponse.getAccountDtoList();

        assertNull(updatedAccounts);
        assertTrue(ErrorCode.INVALID_ACCOUNT_NUMBERS == updateAccountDetailsResponse.getErrorCode());

    }

}
