package org.common.api.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.common.api.dto.AccountDto;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateAccountDetailsRequest {

    private List<AccountDto> listAccountDto;
}
