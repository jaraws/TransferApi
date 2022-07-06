package org.account.api.service.impl;


import org.account.api.entity.Account;
import org.account.api.repository.AccountRepository;
import org.account.api.service.AccountService;
import org.common.api.dto.TransferFundDto;
import org.common.api.exception.TransferException;
import org.common.api.util.ErrorCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

/**
 * Account service class to perform all Account repository operations supported
 * 1. Get Account details
 * 2. Update Account details
 */
@Service
public class AccountServiceImpl implements AccountService {

    @Autowired
    private AccountRepository accountRepository;

    /**
     * Service to read details of all accounts.
     *
     * @return
     * @throws TransferException
     */
    @Override
    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
    public List<Account> getAccountDetails() throws TransferException {
        try {
            return accountRepository.findAll();
        } catch (Exception e) {
            throw new TransferException(ErrorCode.ERROR_GETTING_ACCOUNT_INFO, e);
        }
    }

    /**
     * Service to read details of specified accounts
     *
     * @param accountNumbers
     * @return
     * @throws TransferException
     */
    @Override
    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
    public List<Account> getAccountDetails(List<String> accountNumbers) throws TransferException {
        try {
            return accountRepository.findAllById(accountNumbers);
        } catch (Exception e) {
            throw new TransferException(ErrorCode.ERROR_GETTING_ACCOUNT_INFO, e);
        }
    }

    @Override
    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
    public List<ErrorCode> validateAccountDetailsForTransfer(TransferFundDto transferFundDto) throws TransferException {

        String sourceAccountNumber = transferFundDto.getSourceAccountNumber();
        String destinationAccountNumber = transferFundDto.getDestinationAccountNumber();
        BigDecimal transferAmount = transferFundDto.getTransferAmount();

        List<ErrorCode> errorCodeList = new ArrayList<>();

        // Check source and destination account numbers are not same
        if (sourceAccountNumber.equalsIgnoreCase(destinationAccountNumber)) {
            errorCodeList.add(ErrorCode.SOURCE_DEST_CAN_NOT_BE_SAME);
        }

        // Fetch account details
        List<Account> accountList = getAccountDetails(Arrays.asList(sourceAccountNumber, destinationAccountNumber));

        // Check account response
        if (null == accountList || accountList.size() != 2) {
            errorCodeList.add(ErrorCode.INVALID_ACCOUNT_NUMBERS);
        }

        // Check source account exists
        Predicate<Account> srcAccountPredicate = account -> account.getAccountNumber().equalsIgnoreCase(sourceAccountNumber);
        boolean sourceAccountNotExists = accountList.stream().noneMatch(srcAccountPredicate);
        if (sourceAccountNotExists) {
            errorCodeList.add(ErrorCode.INVALID_SOURCE_ACCOUNT_NUMBER);
        }

        // Check destination account exists
        Predicate<Account> destAccountPredicate = account -> account.getAccountNumber().equalsIgnoreCase(destinationAccountNumber);
        boolean destinationAccountNotExists = accountList.stream().noneMatch(destAccountPredicate);
        if (destinationAccountNotExists) {
            errorCodeList.add(ErrorCode.INVALID_DESTINATION_ACCOUNT_NUMBER);
        }

        // Check negative or zero fund transfer not allowed
        if (transferAmount.compareTo(BigDecimal.ZERO) <= 0) {
            errorCodeList.add(ErrorCode.NEGATIVE_OR_ZERO_FUND_TRANSFER_NOT_ALLOWED);
        }

        // Check funds for transfer
        Account sourceAccount = accountList
                .stream()
                .filter(srcAccountPredicate).findAny().orElse(null);

        if ((!sourceAccountNotExists) && (sourceAccount.getAccountBalance().compareTo(transferAmount) < 0)) {
            errorCodeList.add(ErrorCode.INSUFFICIENT_FUNDS_FOR_TRANSFER);
        }

        return errorCodeList;
    }

    /**
     * Service to update account details
     * <p>
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
     * @param transferFundDto
     * @return
     * @throws TransferException
     */
    @Override
    @Transactional(readOnly = false, isolation = Isolation.REPEATABLE_READ, rollbackFor = TransferException.class)
    public List<ErrorCode> updateAccountDetails(TransferFundDto transferFundDto) throws TransferException {

        String sourceAccountNumber = transferFundDto.getSourceAccountNumber();
        String destinationAccountNumber = transferFundDto.getDestinationAccountNumber();
        BigDecimal transferAmount = transferFundDto.getTransferAmount();
        List<ErrorCode> listErrorCode;

        try {
            // Validate account details again with row level read and write locks acquired using RR isolation level
            listErrorCode = validateAccountDetailsForTransfer(transferFundDto);

            // If errors identified log errors in response and exit
            if (listErrorCode.size() > 0) {
                return listErrorCode;
            }

            // Fetch account details
            List<Account> accountList = getAccountDetails(Arrays.asList(sourceAccountNumber, destinationAccountNumber));

            // Get Source Account
            Account sourceAccount = accountList.stream()
                    .filter((account) -> account.getAccountNumber().equalsIgnoreCase(sourceAccountNumber))
                    .reduce((u, v) -> {
                        throw new IllegalStateException("More than one source account found");
                    }).get();

            // Get Destination Account
            Account destinationAccount = accountList.stream()
                    .filter((account) -> account.getAccountNumber().equalsIgnoreCase(destinationAccountNumber))
                    .reduce((u, v) -> {
                        throw new IllegalStateException("More than one destination account found");
                    }).get();

            // Prepare Account s for fund transfer
            sourceAccount.setAccountBalance(sourceAccount.getAccountBalance().subtract(transferAmount));
            destinationAccount.setAccountBalance(destinationAccount.getAccountBalance().add(transferAmount));

            // Update the account within current isolation level
            List<Account> listAccounts = accountRepository.saveAllAndFlush(Arrays.asList(sourceAccount, destinationAccount));

        } catch (TransferException e) {
            throw e;
        } catch (Exception e) {
            throw new TransferException(ErrorCode.ERROR_UPDATING_ACCOUNT_INFO, e);
        }

        return listErrorCode;
    }

}
