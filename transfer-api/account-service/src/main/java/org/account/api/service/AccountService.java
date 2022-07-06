package org.account.api.service;


import org.account.api.entity.Account;
import org.common.api.dto.TransferFundDto;
import org.common.api.exception.TransferException;
import org.common.api.util.ErrorCode;

import java.util.List;

public interface AccountService {

    List<Account> getAccountDetails() throws TransferException;
    List<Account> getAccountDetails(List<String> accountNumbers) throws TransferException;
    List<ErrorCode> validateAccountDetailsForTransfer(TransferFundDto transferFundDto) throws TransferException;
    List<ErrorCode>  updateAccountDetails(TransferFundDto transferFundDto)  throws TransferException;
}
