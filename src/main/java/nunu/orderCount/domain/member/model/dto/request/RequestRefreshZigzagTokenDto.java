package nunu.orderCount.domain.member.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RequestRefreshZigzagTokenDto {
    @Schema(name = "memberId", description = "member id", example = "1L")
    @NotNull
    private Long memberId;
    @Schema(name = "password", description = "비밀번호", example = "password")
    @NotEmpty
    private String password;
}

