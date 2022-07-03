package org.common.api.response;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.common.api.dto.AccountDto;
import org.common.api.util.ErrorCode;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateAccountDetailsResponse {

    private List<AccountDto> accountDtoList;
    private ErrorCode errorCode;

}
