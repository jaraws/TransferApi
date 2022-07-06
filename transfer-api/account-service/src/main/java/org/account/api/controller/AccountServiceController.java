package org.account.api.controller;


import org.account.api.entity.Account;
import org.account.api.mapper.AccountMapper;
import org.account.api.service.AccountService;
import org.common.api.dto.AccountDto;
import org.common.api.dto.TransferFundDto;
import org.common.api.exception.TransferException;
import org.common.api.request.AccountDetailsRequest;
import org.common.api.request.UpdateAccountDetailsRequest;
import org.common.api.response.AccountDetailsResponse;
import org.common.api.response.UpdateAccountDetailsResponse;
import org.common.api.util.ErrorCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Rest Controller exposes different end points for
 * all account related operations.
 * 1. Read All Accounts
 * 2. Get specific account information
 * 3. Update account balance of an account.
 */
@RestController
@RequestMapping("/account-api")
public class AccountServiceController {
    private final static Logger logger = LoggerFactory.getLogger(AccountServiceController.class);
    @Autowired
    private ObjectFactory<AccountDetailsResponse> accountDetailsResponseObjectFactory;

    @Autowired
    private ObjectFactory<UpdateAccountDetailsResponse> updateAccountDetailsResponseObjectFactory;

    @Autowired
    private AccountMapper accountMapper;

    @Autowired
    private AccountService accountService;

    /**
     * Get details of all accounts
     *
     * @return
     */
    @GetMapping(value = "/accounts")
    public AccountDetailsResponse getAllAccountDetails() {
        logger.debug("Getting account details for all accounts");
        AccountDetailsResponse accountDetailsResponse = getAccountDetailsResponse();
        try {
            List<Account> accounts = accountService.getAccountDetails();
            List<AccountDto> listAccountDto = accounts.stream().map(accountMapper.accountEntityToDtoMapper).collect(Collectors.toList());

            accountDetailsResponse.setListAccountDto(listAccountDto);
            return accountDetailsResponse;
        } catch (TransferException e) {
            logger.error("Error fetching account details {},{}", e.getErrorCode(), e);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        accountDetailsResponse.setErrorCode(Arrays.asList(ErrorCode.ERROR_GETTING_ACCOUNT_INFO));
        return accountDetailsResponse;

    }

    /**
     * Get details of given accounts.
     *
     * @param accountDetailsRequest
     * @return
     */
    @PostMapping(value = "/accounts", produces = "application/json", consumes = "application/json")
    public AccountDetailsResponse getAccountDetails(@RequestBody AccountDetailsRequest accountDetailsRequest) {

        logger.debug("Getting account details for account numbers: {}", accountDetailsRequest.getAccountNumbers());
        AccountDetailsResponse accountDetailsResponse = getAccountDetailsResponse();
        try {
            List<Account> accounts = accountService.getAccountDetails(accountDetailsRequest.getAccountNumbers());
            List<AccountDto> listAccountDto = accounts.stream().map(accountMapper.accountEntityToDtoMapper).collect(Collectors.toList());
            accountDetailsResponse.setListAccountDto(listAccountDto);
            return accountDetailsResponse;
        } catch (TransferException e) {
            logger.error("Error fetching account details {},{}", e.getErrorCode(), e);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        accountDetailsResponse.setErrorCode(Arrays.asList(ErrorCode.ERROR_GETTING_ACCOUNT_INFO));
        return accountDetailsResponse;
    }

    /**
     * Update account details.
     *
     * @param updateAccountDetailsRequest
     * @return
     */
    @PostMapping(value = "/update", produces = "application/json", consumes = "application/json")
    public UpdateAccountDetailsResponse updateAccountDetails(@RequestBody UpdateAccountDetailsRequest updateAccountDetailsRequest) {

        TransferFundDto transferFundDto = updateAccountDetailsRequest.getTransferFundDto();
        logger.debug("Initiating fund transfer from account: {} to account: {}", transferFundDto.getSourceAccountNumber(), transferFundDto.getDestinationAccountNumber());

        UpdateAccountDetailsResponse updateAccountDetailsResponse = getUpdateAccountDetailsResponse();
        updateAccountDetailsResponse.setTransferFundDto(transferFundDto);
        try {
            // validate account details
            List<ErrorCode> errorCodeList = accountService.validateAccountDetailsForTransfer(transferFundDto);

            // If errors identified log errors in response and exit
            if (errorCodeList.size() > 0) {
                updateAccountDetailsResponse.setErrorCodeList(errorCodeList);
                return updateAccountDetailsResponse;
            }

            // No validation errors found, execute fund transfer in isolation
            errorCodeList = accountService.updateAccountDetails(updateAccountDetailsRequest.getTransferFundDto());
            updateAccountDetailsResponse.setErrorCodeList(errorCodeList);

        } catch (TransferException e) {
            logger.error("Error updating account details {},{}", e.getErrorCode(), e);
            updateAccountDetailsResponse.setErrorCodeList(Arrays.asList(e.getErrorCode()));
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            updateAccountDetailsResponse.setErrorCodeList(Arrays.asList(ErrorCode.ERROR_UPDATING_ACCOUNT_INFO));
        }
        return updateAccountDetailsResponse;
    }

    private AccountDetailsResponse getAccountDetailsResponse() {
        return accountDetailsResponseObjectFactory.getObject();
    }


    private UpdateAccountDetailsResponse getUpdateAccountDetailsResponse() {
        return updateAccountDetailsResponseObjectFactory.getObject();
    }
}
