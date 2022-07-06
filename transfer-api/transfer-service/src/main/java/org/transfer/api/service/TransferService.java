package org.transfer.api.service;


import org.common.api.request.AccountDetailsRequest;
import org.common.api.request.RecordTransferEventRequest;
import org.common.api.request.TransferRequest;
import org.common.api.request.UpdateAccountDetailsRequest;
import org.common.api.response.AccountDetailsResponse;
import org.common.api.response.RecordTransferEventResponse;
import org.common.api.response.TransferResponse;
import org.common.api.response.UpdateAccountDetailsResponse;

public interface TransferService {

    /**
     * Get Account Details w.r.t. accounts in AccountDetailsRequest
     * @param accountDetailsRequest
     * @return
     */
    AccountDetailsResponse getAccountDetails(AccountDetailsRequest accountDetailsRequest);

    /**
     * Update Account balance post transfer
     */
    UpdateAccountDetailsResponse updateAccountBalance(UpdateAccountDetailsRequest updateAccountDetailsRequest);

    /**
     * Update TransferEvent in Event Records
     */
    RecordTransferEventResponse recordTransferEvent(RecordTransferEventRequest recordTransferEventRequest);

    /**
     * Transfer Orchestrator method
     * @param transferRequest
     */
    TransferResponse transfer(TransferRequest transferRequest);
}
