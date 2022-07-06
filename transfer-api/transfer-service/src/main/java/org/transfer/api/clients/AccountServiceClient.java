package org.transfer.api.clients;

import org.common.api.request.AccountDetailsRequest;
import org.common.api.request.UpdateAccountDetailsRequest;
import org.common.api.response.AccountDetailsResponse;
import org.common.api.response.UpdateAccountDetailsResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/*
This value of name attribute is the name of the service registered using
Eureka for discovery. We define the method call to be made to consume the
REST service exposed by the account-service module.
 */
@FeignClient(name = "account-service") // Self load balanced
//@FeignClient(name = "ACCOUNT-SERVICE-CLIENT", url = "http://localhost:8082")
public interface AccountServiceClient {

    @RequestMapping(method = RequestMethod.GET, value = "/account-api/accounts")
    AccountDetailsResponse getAllAccountDetails();

    @RequestMapping(method = RequestMethod.POST, value = "/account-api/accounts")
    AccountDetailsResponse getAccountDetails(AccountDetailsRequest accountDetailsRequest);

    @RequestMapping(method = RequestMethod.POST, value = "/account-api/update")
    UpdateAccountDetailsResponse updateAccountDetails(UpdateAccountDetailsRequest updateAccountDetailsRequest);

}
