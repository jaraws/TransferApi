package org.transfer.api.service.impl;


import org.common.api.dto.AccountDto;
import org.common.api.dto.TransferEventDto;
import org.common.api.exception.TransferException;
import org.common.api.request.AccountDetailsRequest;
import org.common.api.request.RecordTransferEventRequest;
import org.common.api.request.TransferRequest;
import org.common.api.request.UpdateAccountDetailsRequest;
import org.common.api.response.AccountDetailsResponse;
import org.common.api.response.RecordTransferEventResponse;
import org.common.api.response.TransferResponse;
import org.common.api.util.ErrorCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.transfer.api.clients.AccountServiceClient;
import org.transfer.api.clients.EventServiceClient;
import org.transfer.api.service.TransferService;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.function.Predicate;

@Service
public class TransferServiceImpl implements TransferService {

    private final static Logger logger = LoggerFactory.getLogger(TransferServiceImpl.class);
    private final static Logger eventLogger = LoggerFactory.getLogger("EVENT_LOGGER");

    @Autowired
    private AccountServiceClient accountServiceClient;

    @Autowired
    private EventServiceClient eventServiceClient;
    @Autowired
    private ObjectFactory<AccountDetailsRequest> accountDetailsRequestObjectFactory;
    @Autowired
    private ObjectFactory<RecordTransferEventRequest> recordTransferEventRequestObjectFactory;

    @Autowired
    private ObjectFactory<UpdateAccountDetailsRequest> updateAccountDetailsRequestObjectFactory;

    @Autowired
    private ObjectFactory<TransferResponse> transferResponseObjectFactory;

    @Autowired
    private ObjectFactory<TransferEventDto> transferEventDtoObjectFactory;

    private TransferResponse getTransferResponse() {
        return transferResponseObjectFactory.getObject();
    }

    private AccountDetailsRequest getAccountDetailsRequest() {
        return accountDetailsRequestObjectFactory.getObject();
    }

    private RecordTransferEventRequest getRecordTransferEventRequest() {
        return recordTransferEventRequestObjectFactory.getObject();
    }

    private UpdateAccountDetailsRequest getUpdateAccountDetailsRequest() {
        return updateAccountDetailsRequestObjectFactory.getObject();
    }

    private TransferEventDto getTransferEventDto() {
        return transferEventDtoObjectFactory.getObject();
    }

    @Override
    public AccountDetailsResponse getAccountDetails(AccountDetailsRequest accountDetailsRequest) {
        return accountServiceClient.getAccountDetails(accountDetailsRequest);
    }

    @Override
    public AccountDetailsResponse updateAccountBalance(UpdateAccountDetailsRequest updateAccountDetailsRequest) {
        return accountServiceClient.updateAccountDetails(updateAccountDetailsRequest);
    }

    @Override
    public RecordTransferEventResponse recordTransferEvent(RecordTransferEventRequest recordTransferEventRequest) {
        return eventServiceClient.recordTransferEvent(recordTransferEventRequest);
    }

    /**
     * Transfer Process
     * -------------------------------------------
     * 1. Fetch account details
     * 2. Validate if account transfer is possible
     * 3. If Transfer is possible
     * 3.1 Process Transfer
     * 3.2 If Transfer is successful
     * 3.2.1 Update Transfer Response
     * 3.2.2 Record Transfer Event or Write Transfer Event for a later reconcile
     * 3.3 If Transfer failed
     * 3.3.1 Return with appropriate error
     * 4. If Transfer is not possible
     * 4.1 Return with appropriate error
     * --------------------------------------------
     */
    @Override
    public TransferResponse transfer(TransferRequest transferRequest) throws TransferException {

        // Prepare TransferResponse
        TransferResponse transferResponse = getTransferResponse();
        prepareTransferResponse(transferResponse, transferRequest);

        // Fetch account details
        AccountDetailsRequest accountDetailsRequest = getAccountDetailsRequest();
        accountDetailsRequest.setAccountNumbers(Arrays.asList(transferRequest.getSourceAccountNumber(), transferRequest.getDestinationAccountNumber()));
        AccountDetailsResponse accountDetailsResponse = getAccountDetails(accountDetailsRequest);

        if (null != accountDetailsResponse.getErrorCode()) {
            logger.error("Error fetching account details: {}", accountDetailsResponse.getErrorCode());
            transferResponse.setTransferStatus(false);
            transferResponse.setErrors(Arrays.asList(ErrorCode.INTERNAL_SERVER_ERROR.getErrorMessage()));
            return transferResponse;
        }

        // Validate if account transfer is possible
        validateAccountTransfer(transferRequest, transferResponse, accountDetailsResponse);

        // Transfer not possible if validation errors exists
        if (transferResponse.getErrors().size() > 0) {
            // Errors identified during validation
            logger.error("Error validating fund transfer request: {}", transferResponse.getErrors());
            return transferResponse;
        }

        // Process/Prepare Transfer
        processTransfer(transferRequest, accountDetailsResponse);

        // Commit Transfer
        UpdateAccountDetailsRequest updateAccountDetailsRequest = getUpdateAccountDetailsRequest();
        updateAccountDetailsRequest.setListAccountDto(accountDetailsResponse.getListAccountDto());
        AccountDetailsResponse updateAccountBalanceResponse = updateAccountBalance(updateAccountDetailsRequest);

        if (updateAccountBalanceResponse.getErrorCode() != null) {
            logger.error("Error executing fund transfer: {}", accountDetailsResponse.getErrorCode());
            transferResponse.setTransferStatus(false);
            transferResponse.setErrors(Arrays.asList(ErrorCode.INTERNAL_SERVER_ERROR.getErrorMessage()));
            return transferResponse;
        }

        // Update transfer response
        transferResponse.setTransferStatus(true);
        transferResponse.setSourceAccountNumber(transferRequest.getSourceAccountNumber());
        transferResponse.setDestinationAccountNumber(transferRequest.getDestinationAccountNumber());
        transferResponse.setTransferAmount(transferRequest.getTransferAmount());


        // Record Transfer event
        RecordTransferEventRequest recordTransferEventRequest = getRecordTransferEventRequest();
        TransferEventDto transferEventDto = getTransferEventDto();
        try {
            transferEventDto.setTransferAmount(transferRequest.getTransferAmount());
            transferEventDto.setSourceAccountNumber(transferRequest.getSourceAccountNumber());
            transferEventDto.setDestinationAccountNumber(transferRequest.getDestinationAccountNumber());
            recordTransferEventRequest.setTransferEventDto(transferEventDto);
            recordTransferEvent(recordTransferEventRequest);
        } catch (Exception e) {
            // log recordTransferEvent request failure
            logger.error("Error recording TransferEvent :{}", e.getMessage(), e);
            eventLogger.info(String.valueOf(transferEventDto));
        }

        // Return transfer response
        return transferResponse;
    }

    private void prepareTransferResponse(TransferResponse transferResponse, TransferRequest transferRequest) {
        transferResponse.setTransferAmount(transferRequest.getTransferAmount());
        transferResponse.setSourceAccountNumber(transferRequest.getSourceAccountNumber());
        transferResponse.setDestinationAccountNumber(transferRequest.getDestinationAccountNumber());
    }

    private void processTransfer(TransferRequest transferRequest, AccountDetailsResponse accountDetailsResponse) {
        // Check funds for transfer
        Predicate<AccountDto> srcAccountPredicate = accountDto -> accountDto.getAccountNumber().equalsIgnoreCase(transferRequest.getSourceAccountNumber());
        AccountDto sourceAccount = accountDetailsResponse.getListAccountDto()
                .stream()
                .filter(srcAccountPredicate).findAny().orElse(null);

        // Check funds for transfer
        Predicate<AccountDto> destAccountPredicate = accountDto -> accountDto.getAccountNumber().equalsIgnoreCase(transferRequest.getDestinationAccountNumber());
        AccountDto destinationAccount = accountDetailsResponse.getListAccountDto()
                .stream()
                .filter(destAccountPredicate).findAny().orElse(null);

        sourceAccount.setAccountBalance(sourceAccount.getAccountBalance().subtract(transferRequest.getTransferAmount()));
        destinationAccount.setAccountBalance(destinationAccount.getAccountBalance().add(transferRequest.getTransferAmount()));
    }

    private void validateAccountTransfer(TransferRequest transferRequest, TransferResponse transferResponse, AccountDetailsResponse accountDetailsResponse) {

        // Check account response
        if (null == accountDetailsResponse.getListAccountDto()) {
            transferResponse.addErrors(ErrorCode.INVALID_ACCOUNT_NUMBERS.getErrorMessage());
        }

        // Check source account Exists
        Predicate<AccountDto> srcAccountPredicate = accountDto -> accountDto.getAccountNumber().equalsIgnoreCase(transferRequest.getSourceAccountNumber());
        boolean sourceAccountNotExists = accountDetailsResponse.getListAccountDto().stream().noneMatch(srcAccountPredicate);
        if (sourceAccountNotExists) {
            transferResponse.addErrors(ErrorCode.INVALID_SOURCE_ACCOUNT_NUMBER.getErrorMessage());
        }

        // Check destination account exists
        Predicate<AccountDto> destAccountPredicate = accountDto -> accountDto.getAccountNumber().equalsIgnoreCase(transferRequest.getDestinationAccountNumber());
        boolean destinationAccountNotExists = accountDetailsResponse.getListAccountDto().stream().noneMatch(destAccountPredicate);
        if (destinationAccountNotExists) {
            transferResponse.addErrors(ErrorCode.INVALID_DESTINATION_ACCOUNT_NUMBER.getErrorMessage());
        }

        // Check negative or zero fund transfer not allowed
        if (transferRequest.getTransferAmount().compareTo(BigDecimal.ZERO) <= 0) {
            transferResponse.addErrors(ErrorCode.NEGATIVE_OR_ZERO_FUND_TRANSFER_NOT_ALLOWED.getErrorMessage());
        }

        // Check funds for transfer
        AccountDto sourceAccount = accountDetailsResponse.getListAccountDto()
                .stream()
                .filter(srcAccountPredicate).findAny().orElse(null);

        if ((!sourceAccountNotExists) && (sourceAccount.getAccountBalance().compareTo(transferRequest.getTransferAmount()) < 0)) {
            transferResponse.addErrors(ErrorCode.INSUFFICIENT_FUNDS_FOR_TRANSFER.getErrorMessage());
        }
    }

}
