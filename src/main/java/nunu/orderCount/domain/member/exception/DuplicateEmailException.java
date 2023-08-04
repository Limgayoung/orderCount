package nunu.orderCount.domain.member.exception;

import nunu.orderCount.global.error.ErrorCode;
import nunu.orderCount.global.error.exception.BusinessException;

public class DuplicateEmailException extends BusinessException {
    public DuplicateEmailException() {
        super(ErrorCode.DUPLICATE_EMAIL);
    }

    public DuplicateEmailException(String detailMessage) {
        super(ErrorCode.DUPLICATE_EMAIL, detailMessage);
    }
}
