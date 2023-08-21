package nunu.orderCount.domain.order.exception;

import nunu.orderCount.global.error.ErrorCode;
import nunu.orderCount.global.error.exception.BusinessException;

public class ZigzagRequestFailException extends BusinessException {
    public ZigzagRequestFailException() {
        super(ErrorCode.FAIL_ZIGZAG_REQUEST);
    }

    public ZigzagRequestFailException(String detailMessage) {
        super(ErrorCode.FAIL_ZIGZAG_REQUEST, detailMessage);
    }
}
