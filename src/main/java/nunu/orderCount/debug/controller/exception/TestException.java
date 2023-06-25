package nunu.orderCount.debug.controller.exception;

import nunu.orderCount.global.error.ErrorCode;
import nunu.orderCount.global.error.exception.BusinessException;

public class TestException extends BusinessException {

    public TestException(ErrorCode errorCode) {
        super(errorCode);
    }

    public TestException(ErrorCode errorCode, String detailMessage) {
        super(errorCode, detailMessage);
    }
}
