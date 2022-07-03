package org.event.api.service;


import org.common.api.exception.TransferException;
import org.event.api.entity.TransferEvent;

import java.util.List;

public interface EventService {

    List<TransferEvent> getTransferEvents() throws TransferException;

    TransferEvent recordTransferEvent(TransferEvent event) throws TransferException;

}
