package org.common.api.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransferResponse {

    private String sourceAccountNumber;
    private String destinationAccountNumber;
    private BigDecimal transferAmount;
    private Boolean transferStatus;
    private List<String> errors;

}
