package nunu.orderCount.global.config.jwt;

import nunu.orderCount.global.error.ErrorCode;
import nunu.orderCount.global.error.exception.BusinessException;

public class CustomJwtException extends BusinessException {
    public CustomJwtException(ErrorCode errorCode) {
        super(errorCode);
    }

    public CustomJwtException(ErrorCode errorCode, String detailMessage) {
        super(errorCode, detailMessage);
    }
}
