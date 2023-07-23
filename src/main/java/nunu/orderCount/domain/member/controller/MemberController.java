package nunu.orderCount.domain.member.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nunu.orderCount.domain.member.model.dto.request.RequestJoinDto;
import nunu.orderCount.domain.member.model.dto.request.RequestLoginDto;
import nunu.orderCount.domain.member.model.dto.request.RequestReissueDto;
import nunu.orderCount.domain.member.model.dto.response.ResponseLoginDto;
import nunu.orderCount.domain.member.model.dto.response.ResponseReissueDto;
import nunu.orderCount.domain.member.service.MemberService;
import nunu.orderCount.global.error.ErrorResponse;
import nunu.orderCount.global.response.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Slf4j
@Tag(name = "Member 관련 API")
@RequestMapping("/members")
@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @Operation(summary = "join API", description = "email과 password로 회원가입 진행")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원가입 성공")
    })
    @PostMapping("/join")
    public ResponseEntity<Response> join(@RequestBody @Valid RequestJoinDto dto) {
        memberService.join(dto);
        return Response.SUCCESS();
    }

    @Operation(summary = "login API", description = "email과 password로 로그인 진행, jwt 발급")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "S200", description = "로그인 성공", content = @Content(schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "S200(data)", description = "로그인 성공 - data", content = @Content(schema = @Schema(implementation = ResponseLoginDto.class))),
            @ApiResponse(responseCode = "M001", description = "로그인 실패", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))

    })
    @PostMapping("/login")
    public ResponseEntity<Response> login(@RequestBody @Valid RequestLoginDto dto) {
        ResponseLoginDto responseLoginDto = memberService.login(dto);
        return Response.SUCCESS("로그인을 성공했습니다.", responseLoginDto);
    }

    //reissue - access token
    @Operation(summary = "reissue API", description = "access token 재발급")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "S200", description = "토큰 재발급 성공", content = @Content(schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "S200(data)", description = "토큰 재발급 성공 data", content = @Content(schema = @Schema(implementation = ResponseReissueDto.class)))
    })
    @PostMapping("/reissue")
    public ResponseEntity<Response> reissueToken(@RequestBody @Valid RequestReissueDto dto) {
        String reissueToken = memberService.refreshToken(dto);
        return Response.SUCCESS("토큰을 재발급했습니다.", new ResponseReissueDto(reissueToken));
    }

    //refresh zigzag token

    //logout

    //delete account
}
