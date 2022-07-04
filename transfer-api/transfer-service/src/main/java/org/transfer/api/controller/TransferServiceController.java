package org.transfer.api.controller;


import org.common.api.exception.TransferException;
import org.common.api.request.TransferRequest;
import org.common.api.response.TransferResponse;
import org.common.api.util.ErrorCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.transfer.api.service.TransferService;

import java.util.Arrays;

@RestController
@RequestMapping("/transfer-api")
public class TransferServiceController {
    private final static Logger logger = LoggerFactory.getLogger(TransferServiceController.class);
    @Autowired
    private ObjectFactory<TransferResponse> transferResponseObjectFactory;

    @Autowired
    private TransferService transferService;

    @PostMapping(value = "/transfer", produces = "application/json", consumes = "application/json")
    public TransferResponse transfer(@RequestBody TransferRequest transferRequest) {

        logger.debug("Transfer request received: {}", transferRequest);
        TransferResponse transferResponse;

        try {
            return transferService.transfer(transferRequest);
        } catch (TransferException e) {
            logger.error("Error performing transfer {},{}", e.getErrorCode(), e);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        transferResponse = getTransferResponse();
        transferResponse.setTransferStatus(false);
        transferResponse.setErrors(Arrays.asList(ErrorCode.INTERNAL_SERVER_ERROR.getErrorMessage()));
        transferResponse.setSourceAccountNumber(transferRequest.getSourceAccountNumber());
        transferResponse.setDestinationAccountNumber(transferRequest.getDestinationAccountNumber());
        transferResponse.setTransferAmount(transferRequest.getTransferAmount());

        return transferResponse;
    }

    private TransferResponse getTransferResponse() {
        return transferResponseObjectFactory.getObject();
    }

}

