package org.account.api.service;


import org.account.api.entity.Account;
import org.common.api.dto.AccountDto;
import org.common.api.exception.TransferException;

import java.util.List;

public interface AccountService {

    List<Account> getAccountDetails() throws TransferException;
    List<Account> getAccountDetails(List<String> accountNumbers) throws TransferException;

    List<Account> updateAccountDetails(List<Account> listAccount)  throws TransferException;
}
