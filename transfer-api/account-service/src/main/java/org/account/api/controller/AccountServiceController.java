package org.account.api.controller;


import org.account.api.entity.Account;
import org.account.api.service.AccountService;
import org.common.api.dto.AccountDto;
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

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/account-api")
public class AccountServiceController {
    private final static Logger logger = LoggerFactory.getLogger(AccountServiceController.class);
    @Autowired
    private ObjectFactory<AccountDetailsResponse> accountDetailsResponseObjectFactory;

    @Autowired
    private ObjectFactory<AccountDto> accountDtoObjectFactory;
    @Autowired
    private ObjectFactory<Account> accountEntityObjectFactory;

    @Autowired
    private ObjectFactory<UpdateAccountDetailsResponse> updateAccountDetailsResponseObjectFactory;

    @Autowired
    private AccountService accountService;

    private Function<Account, AccountDto> accountEntityToDtoMapper = accountEntity -> {
        AccountDto accountDto = getAccountDto();
        accountDto.setAccountNumber(accountEntity.getAccountNumber());
        accountDto.setAccountBalance(accountEntity.getAccountBalance());
        return accountDto;
    };

    private Function<AccountDto, Account> accountDtoToEntityMapper = accountDto -> {
        Account accountEntity = getAccountEntity();
        accountEntity.setAccountNumber(accountDto.getAccountNumber());
        accountEntity.setAccountBalance(accountDto.getAccountBalance());
        return accountEntity;
    };

    @GetMapping(value = "/accounts")
    private AccountDetailsResponse getAllAccountDetails() {
        logger.debug("Getting account details for all accounts");
        AccountDetailsResponse accountDetailsResponse = getAccountDetailsResponse();
        try {
            List<Account> accounts = accountService.getAccountDetails();
            List<AccountDto> listAccountDto = accounts.stream().map(accountEntityToDtoMapper).collect(Collectors.toList());

            accountDetailsResponse.setListAccountDto(listAccountDto);
            return accountDetailsResponse;
        } catch (TransferException e) {
            logger.error("Error fetching account details {},{}", e.getErrorCode(), e);
        }catch (Exception e){
            logger.error(e.getMessage(), e);
        }
        accountDetailsResponse.setErrorCode(ErrorCode.ERROR_GETTING_ACCOUNT_INFO);
        return accountDetailsResponse;

    }

    @PostMapping(value = "/accounts",
            produces = "application/json",
            consumes = "application/json")
    public AccountDetailsResponse getAccountDetails(@RequestBody AccountDetailsRequest accountDetailsRequest) {

        logger.debug("Getting account details for account numbers: {}", accountDetailsRequest.getAccountNumbers());
        AccountDetailsResponse accountDetailsResponse = getAccountDetailsResponse();
        try {
            List<Account> accounts = accountService.getAccountDetails(accountDetailsRequest.getAccountNumbers());
            List<AccountDto> listAccountDto = accounts.stream().map(accountEntityToDtoMapper).collect(Collectors.toList());
            accountDetailsResponse.setListAccountDto(listAccountDto);
            return accountDetailsResponse;
        } catch (TransferException e) {
            logger.error("Error fetching account details {},{}", e.getErrorCode(), e);
        }catch (Exception e){
            logger.error(e.getMessage(), e);
        }
        accountDetailsResponse.setErrorCode(ErrorCode.ERROR_GETTING_ACCOUNT_INFO);
        return accountDetailsResponse;
    }

    @PostMapping(value = "/update",
            produces = "application/json",
            consumes = "application/json")
    public UpdateAccountDetailsResponse updateAccountDetails(@RequestBody UpdateAccountDetailsRequest updateAccountDetailsRequest) {

        logger.debug("Updating account details for account numbers: {}", updateAccountDetailsRequest.getListAccountDto().stream().map(dto->dto.getAccountNumber()).collect(Collectors.toList()));
        UpdateAccountDetailsResponse updateAccountDetailsResponse = getUpdateAccountDetailsResponse();
        try {
            List<Account> accountList = accountService.updateAccountDetails(updateAccountDetailsRequest.getListAccountDto().stream().map(accountDtoToEntityMapper).collect(Collectors.toList()));
            List<AccountDto> listAccountDto = accountList.stream().map(accountEntityToDtoMapper).collect(Collectors.toList());
            updateAccountDetailsResponse.setAccountDtoList(listAccountDto);
            return updateAccountDetailsResponse;
        } catch (TransferException e) {
            logger.error("Error updating account details {},{}", e.getErrorCode(), e);
        }catch (Exception e){
            logger.error(e.getMessage(), e);
        }
        updateAccountDetailsResponse.setErrorCode(ErrorCode.ERROR_UPDATING_ACCOUNT_INFO);
        return updateAccountDetailsResponse;
    }


    private AccountDetailsResponse getAccountDetailsResponse() {
        return accountDetailsResponseObjectFactory.getObject();
    }

    private AccountDto getAccountDto() {
        return accountDtoObjectFactory.getObject();
    }

    private Account getAccountEntity() {
        return accountEntityObjectFactory.getObject();
    }
    private UpdateAccountDetailsResponse getUpdateAccountDetailsResponse() {
        return updateAccountDetailsResponseObjectFactory.getObject();
    }
}
