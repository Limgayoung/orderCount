package nunu.orderCount.domain.member.exception;

import nunu.orderCount.global.error.ErrorCode;
import nunu.orderCount.global.error.exception.BusinessException;

public class ZigzagLoginFailException extends BusinessException {
    public ZigzagLoginFailException() {
        super(ErrorCode.LOGIN_FAIL);
    }

    public ZigzagLoginFailException(String detailMessage) {
        super(ErrorCode.LOGIN_FAIL, detailMessage);
    }
}
