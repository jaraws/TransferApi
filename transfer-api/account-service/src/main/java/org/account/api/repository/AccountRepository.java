package org.account.api.repository;

import org.account.api.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * AccountRepository which is an interface that extends the Spring Framework class
 * JpaRepository. JpaRepository class is a generics and takes the following two
 * parameters as arguments-
 * What type of Object will this repository be working with- In our case Account
 * Id will be what type of object- String(since id defined in the Account class is String)
 */
@Repository
public interface AccountRepository extends JpaRepository<Account,String> {
}
