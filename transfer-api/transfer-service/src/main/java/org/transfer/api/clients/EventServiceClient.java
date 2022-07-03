package org.transfer.api.clients;

import org.common.api.request.AccountDetailsRequest;
import org.common.api.request.RecordTransferEventRequest;
import org.common.api.response.AccountDetailsResponse;
import org.common.api.response.GetTransferEventsResponse;
import org.common.api.response.RecordTransferEventResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


/*
This value of name attribute is the name of the service registered using
Eureka for discovery. We define the method call to be made to consume the
REST service exposed by the event-service module.
 */
@FeignClient(name = "event-service") // Self load balanced
//@FeignClient(name = "EVENT-SERVICE-CLIENT", url = "http://localhost:8083")
public interface EventServiceClient {

    @RequestMapping(method = RequestMethod.GET, value = "/event-api/transfer-events")
    GetTransferEventsResponse getTransferEvents();

    @RequestMapping(method = RequestMethod.POST, value = "/event-api/transfer-event")
    RecordTransferEventResponse recordTransferEvent(RecordTransferEventRequest recordTransferEventRequest);


}
