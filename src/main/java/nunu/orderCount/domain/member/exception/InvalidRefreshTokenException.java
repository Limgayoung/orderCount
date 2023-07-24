package nunu.orderCount.domain.member.exception;

import nunu.orderCount.global.error.ErrorCode;
import nunu.orderCount.global.error.exception.BusinessException;

public class InvalidRefreshTokenException extends BusinessException {
    public InvalidRefreshTokenException() {
        super(ErrorCode.INVALID_REFRESH_TOKEN);
    }

    public InvalidRefreshTokenException(String detailMessage) {
        super(ErrorCode.INVALID_REFRESH_TOKEN, detailMessage);
    }
}
