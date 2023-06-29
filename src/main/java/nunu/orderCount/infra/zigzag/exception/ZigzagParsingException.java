package nunu.orderCount.infra.zigzag.exception;

import nunu.orderCount.global.error.ErrorCode;
import nunu.orderCount.global.error.exception.BusinessException;

public class ZigzagParsingException extends BusinessException {
    public ZigzagParsingException() {
        super(ErrorCode.JSON_PARSING_ERROR);
    }

    public ZigzagParsingException(String detailMessage) {
        super(ErrorCode.JSON_PARSING_ERROR, detailMessage);
    }
}
