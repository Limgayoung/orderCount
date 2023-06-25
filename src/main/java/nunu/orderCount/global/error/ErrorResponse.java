package nunu.orderCount.global.error;

import lombok.Getter;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class ErrorResponse {
    private final String code;
    private final String message;
    private final Integer status;
    private final String timestamp;
    private final String trackingId;
    private final String detailMessage;

    public ErrorResponse(ErrorCode code, String detailMessage) {
        this.code = code.getCode();
        this.message = code.getMessage();
        this.status = code.getStatus();
        this.timestamp = LocalDateTime.now().toString();
        this.trackingId = UUID.randomUUID().toString();
        this.detailMessage = detailMessage;
    }
    public ErrorResponse(ErrorCode code) {
        this.code = code.getCode();
        this.message = code.getMessage();
        this.status = code.getStatus();
        this.timestamp = LocalDateTime.now().toString();
        this.trackingId = UUID.randomUUID().toString();
        this.detailMessage = "";
    }

    public static ErrorResponse of(ErrorCode code) {
        return new ErrorResponse(code);
    }
    public static ErrorResponse of(ErrorCode code, String detailMessage) {
        return new ErrorResponse(code, detailMessage);
    }
}
