package org.event.api.service.impl;


import org.common.api.exception.TransferException;
import org.common.api.util.ErrorCode;
import org.event.api.entity.TransferEvent;
import org.event.api.repository.TransferEventRepository;
import org.event.api.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    public List<TransferEvent> getTransferEvents() throws TransferException {
        try {
            return transferEventRepository.findAll();
        } catch (Exception e) {
            throw new TransferException(ErrorCode.ERROR_GETTING_TRANSFER_EVENT_INFO, e);
        }
    }

    @Override
    public TransferEvent recordTransferEvent(TransferEvent event) throws TransferException {
        try {
            return transferEventRepository.saveAndFlush(event);
        } catch (Exception e) {
            throw new TransferException(ErrorCode.ERROR_RECORDING_TRANSFER_EVENT_INFO, e);
        }
    }
}
