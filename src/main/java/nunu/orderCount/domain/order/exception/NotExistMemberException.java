package nunu.orderCount.domain.order.exception;

import nunu.orderCount.global.error.ErrorCode;
import nunu.orderCount.global.error.exception.BusinessException;

public class NotExistMemberException extends BusinessException {
    public NotExistMemberException() {
        super(ErrorCode.NOT_EXIST_MEMBER);
    }

    public NotExistMemberException(String detailMessage) {
        super(ErrorCode.NOT_EXIST_MEMBER, detailMessage);
    }
}
