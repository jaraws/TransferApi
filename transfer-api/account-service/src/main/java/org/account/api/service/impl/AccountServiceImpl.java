package org.account.api.service.impl;


import org.account.api.entity.Account;
import org.account.api.repository.AccountRepository;
import org.account.api.service.AccountService;
import org.common.api.dto.AccountDto;
import org.common.api.exception.TransferException;
import org.common.api.util.ErrorCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccountServiceImpl implements AccountService {

    @Autowired
    private AccountRepository accountRepository;

    @Override
    public List<Account> getAccountDetails() throws TransferException {
        try {
            return accountRepository.findAll();
        } catch (Exception e) {
            throw new TransferException(ErrorCode.ERROR_GETTING_ACCOUNT_INFO, e);
        }
    }

    @Override
    public List<Account> getAccountDetails(List<String> accountNumbers) throws TransferException {
        try {
            return accountRepository.findAllById(accountNumbers);
        } catch (Exception e) {
            throw new TransferException(ErrorCode.ERROR_GETTING_ACCOUNT_INFO, e);
        }
    }

    @Override
    public List<Account> updateAccountDetails(List<Account> listAccount) throws TransferException {
        List<Account> listAccounts = accountRepository.saveAllAndFlush(listAccount);
        return listAccounts;
    }

}
