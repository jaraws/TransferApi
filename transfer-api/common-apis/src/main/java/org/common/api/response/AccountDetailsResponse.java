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
public class AccountDetailsResponse {

    private List<AccountDto> listAccountDto;
    private List<ErrorCode> errorCode;
}
