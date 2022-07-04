package org.common.api.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.common.api.dto.TransferEventDto;
import org.common.api.util.ErrorCode;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecordTransferEventResponse {

    private TransferEventDto transferEventDto;
    private ErrorCode errorCode;
}
