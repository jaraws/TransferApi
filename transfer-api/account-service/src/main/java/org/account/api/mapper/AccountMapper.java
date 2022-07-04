package org.account.api.mapper;

import org.account.api.entity.Account;
import org.common.api.dto.AccountDto;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.function.Function;

/**
 * AccountMapper Service to map Account entity to AccountDto
 * and AccountDto to Account entity.
 *
 */
@Service
public class AccountMapper {

    @Autowired
    private ObjectFactory<AccountDto> accountDtoObjectFactory;
    @Autowired
    private ObjectFactory<Account> accountEntityObjectFactory;

    private AccountDto getAccountDto() {
        return accountDtoObjectFactory.getObject();
    }

    private Account getAccountEntity() {
        return accountEntityObjectFactory.getObject();
    }

    public Function<Account, AccountDto> accountEntityToDtoMapper = accountEntity -> {
        AccountDto accountDto = getAccountDto();
        accountDto.setAccountNumber(accountEntity.getAccountNumber());
        accountDto.setAccountBalance(accountEntity.getAccountBalance());
        return accountDto;
    };

    public Function<AccountDto, Account> accountDtoToEntityMapper = accountDto -> {
        Account accountEntity = getAccountEntity();
        accountEntity.setAccountNumber(accountDto.getAccountNumber());
        accountEntity.setAccountBalance(accountDto.getAccountBalance());
        return accountEntity;
    };
}
