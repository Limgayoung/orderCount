package nunu.orderCount.global.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResponseCode {
    /**
     * Success Code
     * 200 : 요청 성공
     * 201 : 요청으로 인해 새로운 리소스 생성
     * 204 : 요청에 성공했으나 데이터는 없음
     */

    SUCCESS("S001", "요청이 완료 되었습니다."),
    CREATED("S002", "생성이 완료되었습니다."),
    NO_CONTENT("S003", "요청에 대한 정보가 없습니다."),
    FAILURE("F001", "에러는 아니지만 실패했습니다.");

    private final String code;
    private final String message;
}
