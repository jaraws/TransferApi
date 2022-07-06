package org.common.api.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.common.api.dto.TransferFundDto;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateAccountDetailsRequest {

    private TransferFundDto transferFundDto;

}
