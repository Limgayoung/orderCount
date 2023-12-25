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
import nunu.orderCount.domain.member.model.dto.request.RequestRefreshZigzagTokenDto;
import nunu.orderCount.domain.member.model.dto.request.RequestReissueDto;
import nunu.orderCount.domain.member.model.dto.response.ResponseJoinDto;
import nunu.orderCount.domain.member.model.dto.response.ResponseLoginDto;
import nunu.orderCount.domain.member.model.dto.response.ResponseRefreshZigzagToken;
import nunu.orderCount.domain.member.model.dto.response.ResponseReissueDto;
import nunu.orderCount.domain.member.service.MemberService;
import nunu.orderCount.global.error.ErrorResponse;
import nunu.orderCount.global.response.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Slf4j
@Tag(name = "Member 관련 API")
@RequestMapping("/api/members")
@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @Operation(summary = "join API", description = "email과 password로 회원가입 진행")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "S200", description = "회원가입 성공")
    })
    @PostMapping("/join")
    public ResponseEntity<Response> join(@RequestBody @Valid RequestJoinDto dto) {
        ResponseJoinDto responseJoinDto = memberService.join(dto);
        return Response.SUCCESS("회원가입을 성공했습니다.", responseJoinDto);
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
        memberService.refreshZigzagToken(
                new RequestRefreshZigzagTokenDto(responseLoginDto.getMemberId(), dto.getPassword()));
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
        ResponseReissueDto reissueToken = memberService.reissueToken(dto);
        return Response.SUCCESS("토큰을 재발급했습니다.", reissueToken);
    }

    //refresh zigzag token
    @Operation(summary = "refresh zigzag token API", description = "zigzag token 재발급")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "S200", description = "토큰 재발급 성공", content = @Content(schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "S200(data)", description = "토큰 재발급 성공 data", content = @Content(schema = @Schema(implementation = ResponseRefreshZigzagToken.class)))
    })
    @PostMapping("/refresh-zigzag")
    public ResponseEntity<Response> refreshZigzagToken(@RequestBody @Valid RequestRefreshZigzagTokenDto dto) {
        ResponseRefreshZigzagToken response  = memberService.refreshZigzagToken(dto);
        return Response.SUCCESS("zigzag 토큰을 재발급했습니다.", response);
    }

    //logout

    //delete account
}
