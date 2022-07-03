package org.common.api.exception;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.common.api.util.ErrorCode;

@NoArgsConstructor
@Data
public class TransferException extends Exception {

    private ErrorCode errorCode;

    public TransferException(ErrorCode errorCode, Throwable exception) {
        super(exception);
        this.errorCode = errorCode;
    }

}
