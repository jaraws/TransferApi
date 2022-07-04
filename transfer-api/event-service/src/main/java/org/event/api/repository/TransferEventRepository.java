package org.event.api.repository;

import org.event.api.entity.TransferEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * TransferEventRepository which is an interface that extends the Spring Framework class
 * JpaRepository. JpaRepository class is a generics and takes the following two
 * parameters as arguments-
 * What type of Object will this repository be working with- In our case TransferEvent
 * Id will be what type of object- String(since id defined in the TransferEvent class is String)
 */
@Repository
public interface TransferEventRepository extends JpaRepository<TransferEvent,String> {
}
