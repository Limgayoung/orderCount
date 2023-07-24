package nunu.orderCount.domain.member.exception;

import nunu.orderCount.global.error.ErrorCode;
import nunu.orderCount.global.error.exception.BusinessException;

public class RoleIsNotExistException extends BusinessException {

    public RoleIsNotExistException() {
        super(ErrorCode.NOT_EXIST_ROLE);
    }

    public RoleIsNotExistException(String detailMessage) {
        super(ErrorCode.NOT_EXIST_ROLE, detailMessage);
    }
}
