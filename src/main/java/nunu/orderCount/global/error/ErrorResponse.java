package nunu.orderCount.global.error;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class ErrorResponse {
    private final String code;
    private final String message;
    private final String timestamp;
    private final String trackingId;
    private final String detailMessage;

    public ErrorResponse(ErrorCode code, String detailMessage) {
        this.code = code.getCode();
        this.message = code.getMessage();
        this.timestamp = LocalDateTime.now().toString();
        this.trackingId = UUID.randomUUID().toString();
        this.detailMessage = detailMessage;
    }
    public ErrorResponse(ErrorCode code) {
        this.code = code.getCode();
        this.message = code.getMessage();
        this.timestamp = LocalDateTime.now().toString();
        this.trackingId = UUID.randomUUID().toString();
        this.detailMessage = "";
    }

    public static ResponseEntity<ErrorResponse> of(ErrorCode code) {
        return ResponseEntity
                .status(HttpStatus.valueOf(code.getStatus()))
                .body(new ErrorResponse(code));
    }
    public static ResponseEntity<ErrorResponse> of(ErrorCode code, String detailMessage) {
        return ResponseEntity
                .status(HttpStatus.valueOf(code.getStatus()))
                .body(new ErrorResponse(code, detailMessage));
    }
}
