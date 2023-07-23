package nunu.orderCount.domain.member.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;

@Schema(name = "회원가입 request dto", description = "회원가입 시 필요한 유저 정보")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RequestJoinDto {

    @Email(message = "올바르지 않은 이메일 형식입니다")
    @Schema(description = "이메일", example = "email@naver.com")
    private String email;

    @Schema(description = "비밀번호", example = "password1234")
    private String password;

    //todo: platform 추가
}