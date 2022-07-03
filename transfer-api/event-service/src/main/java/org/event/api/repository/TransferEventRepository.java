package org.event.api.repository;

import org.event.api.entity.TransferEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransferEventRepository extends JpaRepository<TransferEvent,String> {
}
