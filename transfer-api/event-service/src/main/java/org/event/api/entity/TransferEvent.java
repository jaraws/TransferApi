package org.event.api.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * Model for TransferEvent entity.
 * Table name: TRANSFER_EVENT
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "transfer_event")
public class TransferEvent {

    // @ID This annotation specifies
    // the primary key of the entity.
    @Id
    // @GeneratedValue This annotation
    // is used to specify the primary
    // key generation strategy to use
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "event_id")
    private long eventId;

    @Column(name = "source_account_number")
    private String sourceAccountNumber;

    @Column(name = "destination_account_number")
    private String destinationAccountNumber;

    @Column(name = "transfer_amt")
    private BigDecimal transferAmount;

}
