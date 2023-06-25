package nunu.orderCount.global.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class Response<T> {
    private ResponseStatus status;
    private String code;
    private String message;
    private T data;
    private LocalDateTime timestamp;

    @Builder
    public Response(ResponseStatus status, String code, String message, T data) {
        this.status = status;
        this.code = code;
        this.message = message;
        this.data = data;
        this.timestamp = LocalDateTime.now();
    }

    public static <T>ResponseEntity<Response> SUCCESS(ResponseCode code, T data){
        return new ResponseEntity( Response.builder()
                .status(ResponseStatus.SUCCESS)
                .code(code.getCode())
                .message(code.getMessage())
                .data(data)
                .build(), HttpStatus.OK);
    }
    public static <T>ResponseEntity<Response> SUCCESS(ResponseCode code, String message, T data){
        return new ResponseEntity( Response.builder()
                .status(ResponseStatus.SUCCESS)
                .code(code.getCode())
                .message(message)
                .data(data)
                .build(), HttpStatus.OK);
    }
    public static <T>ResponseEntity<Response> SUCCESS(){
        return new ResponseEntity( Response.builder()
                .status(ResponseStatus.SUCCESS)
                .code("S200")
                .message("ok")
                .data(null)
                .build(), HttpStatus.OK);
    }

    public static <T>ResponseEntity<Response> FAILURE(ResponseCode code){
        return new ResponseEntity( Response.builder()
                .status(ResponseStatus.FAILURE)
                .code(code.getCode())
                .message(code.getMessage())
                .data(null)
                .build(), HttpStatus.OK);
    }
    public static <T>ResponseEntity<Response> FAILURE(ResponseCode code, String message){
        return new ResponseEntity( Response.builder()
                .status(ResponseStatus.FAILURE)
                .code(code.getCode())
                .message(message)
                .data(null)
                .build(), HttpStatus.OK);
    }
}
