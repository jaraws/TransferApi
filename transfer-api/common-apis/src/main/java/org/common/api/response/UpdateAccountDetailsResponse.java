package org.common.api.response;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.common.api.dto.TransferFundDto;
import org.common.api.util.ErrorCode;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateAccountDetailsResponse {

    private TransferFundDto transferFundDto;
    private List<ErrorCode> errorCodeList;

}
