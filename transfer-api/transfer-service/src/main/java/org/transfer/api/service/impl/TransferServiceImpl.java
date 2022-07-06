package org.transfer.api.service.impl;


import org.common.api.dto.TransferEventDto;
import org.common.api.dto.TransferFundDto;
import org.common.api.request.AccountDetailsRequest;
import org.common.api.request.RecordTransferEventRequest;
import org.common.api.request.TransferRequest;
import org.common.api.request.UpdateAccountDetailsRequest;
import org.common.api.response.AccountDetailsResponse;
import org.common.api.response.RecordTransferEventResponse;
import org.common.api.response.TransferResponse;
import org.common.api.response.UpdateAccountDetailsResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.transfer.api.clients.AccountServiceClient;
import org.transfer.api.clients.EventServiceClient;
import org.transfer.api.service.TransferService;

import java.util.stream.Collectors;

/**
 * Service to orchestrate fund transfer between account and
 * event service.
 */
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
    private ObjectFactory<TransferFundDto> transferFundDtoObjectFactory;

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

    private TransferFundDto getTransferFundDto() {
        return transferFundDtoObjectFactory.getObject();
    }


    private TransferEventDto getTransferEventDto() {
        return transferEventDtoObjectFactory.getObject();
    }

    /**
     * Service method get account details of given accounts
     *
     * @param accountDetailsRequest
     * @return
     */
    @Override
    public AccountDetailsResponse getAccountDetails(AccountDetailsRequest accountDetailsRequest) {
        return accountServiceClient.getAccountDetails(accountDetailsRequest);
    }

    /**
     * Service method Update account details
     *
     * @param updateAccountDetailsRequest
     * @return
     */
    @Override
    public UpdateAccountDetailsResponse updateAccountBalance(UpdateAccountDetailsRequest updateAccountDetailsRequest) {
        return accountServiceClient.updateAccountDetails(updateAccountDetailsRequest);
    }

    /**
     * Service method record transfer event with event service
     *
     * @param recordTransferEventRequest
     * @return
     */
    @Override
    public RecordTransferEventResponse recordTransferEvent(RecordTransferEventRequest recordTransferEventRequest) {
        return eventServiceClient.recordTransferEvent(recordTransferEventRequest);
    }

    /**
     * Transfer Process
     * -------------------------------------------
     *
     * 1. Process Transfer -- Send account details to Account service to update their balances
     * 2. If Transfer is successful
     *   2.1. Update Transfer Response
     *   2.2. Record Transfer Event or Write Transfer Event for a later reconcile
     * 3. If Transfer failed
     *   3.1. Return with appropriate error
     *   3.2. Do not log this transfer event in database as it's unsuccessful
     * --------------------------------------------
     */
    @Override
    public TransferResponse transfer(TransferRequest transferRequest) {

        // Prepare TransferResponse
        TransferResponse transferResponse = getTransferResponse();
        prepareTransferResponse(transferResponse, transferRequest);

        // Prepare Commit Transfer Request
        UpdateAccountDetailsRequest updateAccountDetailsRequest = getUpdateAccountDetailsRequest();
        TransferFundDto transferFundDto = getTransferFundDto();
        transferFundDto.setSourceAccountNumber(transferRequest.getSourceAccountNumber());
        transferFundDto.setDestinationAccountNumber(transferRequest.getDestinationAccountNumber());
        transferFundDto.setTransferAmount(transferRequest.getTransferAmount());
        updateAccountDetailsRequest.setTransferFundDto(transferFundDto);

        // Execute commit transfer request
        UpdateAccountDetailsResponse updateAccountBalanceResponse = updateAccountBalance(updateAccountDetailsRequest);

        // Check if there are any errors from Account Service, if any
        // then update transfer response and exit the transfer flow
        if (updateAccountBalanceResponse.getErrorCodeList() != null
                && updateAccountBalanceResponse.getErrorCodeList().size()>0) {
            logger.error("Error executing fund transfer: {}", updateAccountBalanceResponse.getErrorCodeList());
            transferResponse.setTransferStatus(false);
            transferResponse.setErrors(updateAccountBalanceResponse.getErrorCodeList().stream().map(errorCode -> errorCode.getErrorMessage()).collect(Collectors.toList()));
            return transferResponse;
        }

        // Other transfer is successful, update transfer response
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
            // It is not required to update the customer of this failure as
            // transfer is already successful
        }

        // Return transfer response
        return transferResponse;
    }

    private void prepareTransferResponse(TransferResponse transferResponse, TransferRequest transferRequest) {
        transferResponse.setTransferAmount(transferRequest.getTransferAmount());
        transferResponse.setSourceAccountNumber(transferRequest.getSourceAccountNumber());
        transferResponse.setDestinationAccountNumber(transferRequest.getDestinationAccountNumber());
    }

}
