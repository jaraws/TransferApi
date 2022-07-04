package org.common.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransferEventDto {

    private long eventId;
    private String sourceAccountNumber;
    private String destinationAccountNumber;
    private BigDecimal transferAmount;
}
