package nunu.orderCount.domain.member.exception;

import nunu.orderCount.global.error.ErrorCode;
import nunu.orderCount.global.error.exception.BusinessException;

public class LoginFailException extends BusinessException {
    public LoginFailException() {
        super(ErrorCode.LOGIN_FAIL);
    }

    public LoginFailException(String detailMessage) {
        super(ErrorCode.LOGIN_FAIL, detailMessage);
    }

    public LoginFailException(ErrorCode errorCode, String detailMessage) {
        super(errorCode, detailMessage);
    }
}
