package org.event.api.service.impl;


import org.common.api.exception.TransferException;
import org.common.api.util.ErrorCode;
import org.event.api.entity.TransferEvent;
import org.event.api.repository.TransferEventRepository;
import org.event.api.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Event service class to perform all Event repository operations supported
 * 1. Get Transfer events
 * 2. Record Transfer event
 */
@Service
public class EventServiceImpl implements EventService {

    @Autowired
    private TransferEventRepository transferEventRepository;

    @Override
    @Transactional(readOnly = true,isolation = Isolation.READ_COMMITTED)
    public List<TransferEvent> getTransferEvents() throws TransferException {
        try {
            return transferEventRepository.findAll();
        } catch (Exception e) {
            throw new TransferException(ErrorCode.ERROR_GETTING_TRANSFER_EVENT_INFO, e);
        }
    }

    /**
     * Propagation	Behaviour:
     * REQUIRED:	Always executes in a transaction. If there is any existing transaction it uses it. If none exists then only a new one is created
     * SUPPORTS:	It may or may not run in a transaction. If current transaction exists then it is supported. If none exists then gets executed with out transaction.
     * NOT_SUPPORTED:	Always executes without a transaction. If there is any existing transaction it gets suspended
     * REQUIRES_NEW:	Always executes in a new transaction. If there is any existing transaction it gets suspended
     * NEVER:	Always executes without any transaction. It throws an exception if there is an existing transaction
     * MANDATORY:	Always executes in a transaction. If there is any existing transaction it is used. If there is no existing transaction it will throw an exception.
     * <p>
     * readOnly: false as this method modifies the data in database; otherwise this should be set as false
     *
     * @param event
     * @return
     * @throws TransferException
     */
    @Override
    @Transactional(readOnly = false,rollbackFor = TransferException.class)
    public TransferEvent recordTransferEvent(TransferEvent event) throws TransferException {
        try {
            return transferEventRepository.saveAndFlush(event);
        } catch (Exception e) {
            throw new TransferException(ErrorCode.ERROR_RECORDING_TRANSFER_EVENT_INFO, e);
        }
    }
}
