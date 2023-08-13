package nunu.orderCount.domain.order.exception;

import nunu.orderCount.global.error.ErrorCode;
import nunu.orderCount.global.error.exception.BusinessException;

public class InvalidZigzagTokenException extends BusinessException {
    public InvalidZigzagTokenException() {
        super(ErrorCode.INVALID_ZIGZAG_TOKEN);
    }

    public InvalidZigzagTokenException(String detailMessage) {
        super(ErrorCode.INVALID_ZIGZAG_TOKEN, detailMessage);
    }
}
