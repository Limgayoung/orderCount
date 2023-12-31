package nunu.orderCount.global.error;

import lombok.extern.slf4j.Slf4j;
import nunu.orderCount.global.config.security.CustomSecurityException;
import nunu.orderCount.global.error.exception.BusinessException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    //todo: BusinessException 찍지 말고 발생 class 받아서 명확하게 표시해줄 것
    @ExceptionHandler
    protected ResponseEntity<ErrorResponse> businessExceptionHandler(BusinessException e){
//        log.info("error: {}", e.getErrorCode().getMessage());
        log.error("businessException: {}, {}", e.getErrorCode().getMessage(), e.getMessage());
        return ErrorResponse.of(e.getErrorCode(), e.getMessage());
    }

    @ExceptionHandler
    protected ResponseEntity<ErrorResponse> accessDeniedExceptionHandler(AccessDeniedException e) {
        log.error("AccessDeniedException: {}", e.getMessage());
        return businessExceptionHandler(new BusinessException(ErrorCode.PERMISSION_DENIED));
    }

    @ExceptionHandler
    protected ResponseEntity<ErrorResponse> illegalArgumentExceptionHandler(IllegalArgumentException e) {
        log.error("IllegalArgumentException: {}", e.getMessage());
        return businessExceptionHandler(new BusinessException(ErrorCode.PERMISSION_DENIED));
        //todo: 수정 필요
    }


    //todo : MethodArgumentNotValidException,BindException, MethodArgumentTypeMismatchException,HttpRequestMethodNotSupportedException
    //todo: AccessDeniedException, Exception
}
