package org.common.api.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.common.api.dto.TransferEventDto;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecordTransferEventRequest {

    private TransferEventDto transferEventDto;
}
