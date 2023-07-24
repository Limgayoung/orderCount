package nunu.orderCount.global.config.security;

import nunu.orderCount.global.error.ErrorCode;
import nunu.orderCount.global.error.exception.BusinessException;

public class CustomSecurityException extends BusinessException {
    public CustomSecurityException(ErrorCode errorCode) {
        super(errorCode);
    }

    public CustomSecurityException(ErrorCode errorCode, String detailMessage) {
        super(errorCode, detailMessage);
    }
}
