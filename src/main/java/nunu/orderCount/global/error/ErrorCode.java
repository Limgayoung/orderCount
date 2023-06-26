package nunu.orderCount.global.error;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ErrorCode {
    /**
     *  Error Code
     *  400 : 잘못된 요청
     *  401 : 권한 오류
     *  403 : 서버가 허용하지 않은 웹페이지, 미디어 요청
     *  404 : 존재하지 않는 정보에 대한 요청
     */

    //common
    NOT_FOUND_DATA(404, "C001", "해당하는 데이터를 찾을 수 없습니다."),
    BAD_REQUEST(400, "C002", "잘못된 요청입니다."),

    //webclient
    REQUEST_API_ERROR(500, "W001", "응답값을 받아올 수 없습니다.");

    private final Integer status;
    private final String code;
    private final String message;
}
