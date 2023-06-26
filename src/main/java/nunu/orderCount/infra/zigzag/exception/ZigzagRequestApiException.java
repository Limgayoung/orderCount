package nunu.orderCount.infra.zigzag.exception;

import nunu.orderCount.global.error.ErrorCode;
import nunu.orderCount.global.error.exception.BusinessException;

public class ZigzagRequestApiException extends BusinessException {
    public ZigzagRequestApiException() {
        super(ErrorCode.REQUEST_API_ERROR);
    }

    public ZigzagRequestApiException(String detailMessage) {
        super(ErrorCode.REQUEST_API_ERROR, detailMessage);
    }
}
