package org.common.api.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.common.api.dto.TransferEventDto;
import org.common.api.util.ErrorCode;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetTransferEventsResponse {

    private List<TransferEventDto> transferEventDtoList;
    private ErrorCode errorCode;
}
