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
    INTERNAL_SERVER_ERROR(500, "C003", "서버 에러입니다"),

    //webclient
    REQUEST_API_ERROR(500, "W001", "응답값을 받아올 수 없습니다."),
    JSON_PARSING_ERROR(500, "W002", "json을 파싱할 수 없습니다"),

    //jwt
    JWT_EXPIRED_ERROR(400, "J001", "만료된 토큰입니다."),
    JWT_ERROR(400, "J002", "유효하지 않은 토큰입니다."),

    //security
    NOT_EXIST_ROLE(400, "S001", "권한이 잘못되었습니다."),
    INVALID_AUTHENTICATION(400, "S002", "아이디와 비밀번호가 정확하지 않습니다."),
    PERMISSION_DENIED(400, "S003", "권한이 없습니다"),
    INVALID_TOKEN(400, "S004", "잘못된 토큰입니다."),

    //member
    LOGIN_FAIL(400, "M001", "로그인에 실패했습니다.");


    private final Integer status;
    private final String code;
    private final String message;
}
