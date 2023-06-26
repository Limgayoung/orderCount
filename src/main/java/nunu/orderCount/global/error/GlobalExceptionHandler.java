package nunu.orderCount.global.error;

import nunu.orderCount.global.error.exception.BusinessException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler
    protected ResponseEntity<ErrorResponse> businessExceptionHandler(BusinessException e){
        return ErrorResponse.of(e.getErrorCode(), e.getMessage());
    }

    //todo : MethodArgumentNotValidException,BindException, MethodArgumentTypeMismatchException,HttpRequestMethodNotSupportedException
    //todo: AccessDeniedException, Exception
}
