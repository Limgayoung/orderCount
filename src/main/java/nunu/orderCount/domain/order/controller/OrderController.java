package nunu.orderCount.domain.order.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nunu.orderCount.domain.member.model.dto.request.RequestJoinDto;
import nunu.orderCount.domain.member.model.dto.response.ResponseJoinDto;
import nunu.orderCount.domain.member.model.dto.response.ResponseLoginDto;
import nunu.orderCount.domain.order.model.dto.request.RequestOrderUpdateDto;
import nunu.orderCount.domain.order.model.dto.response.ResponseOrderUpdateDto;
import nunu.orderCount.domain.order.service.OrderServiceImpl;
import nunu.orderCount.global.error.ErrorResponse;
import nunu.orderCount.global.response.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Slf4j
@Tag(name = "Order 관련 API")
@RequestMapping("/api/orders")
@RestController
@RequiredArgsConstructor
public class OrderController {

    private final OrderServiceImpl orderService;

    @Operation(summary = "주문 업데이트 API", description = "회원 id로 주문 업데이트 진행")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "S200", description = "주문 업데이트 성공"),
            @ApiResponse(responseCode = "200(data)", description = "주문 업데이트 성공", content = @Content(schema = @Schema(implementation = ResponseOrderUpdateDto.class))),
            @ApiResponse(responseCode = "O001", description = "주문 업데이트 실패 - zigzag token null", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "O002", description = "주문 업데이트 실패 - zigzag 요청 실패, zigzag token refresh 필요", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "M004", description = "주문 업데이트 실패 - 회원 정보 없음", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/{id}")
    public ResponseEntity<Response> updateZigzagOrder(@PathVariable("id") Long id) {
        ResponseOrderUpdateDto responseOrderUpdateDto = orderService.orderUpdate(new RequestOrderUpdateDto(id));
        return Response.SUCCESS("주문 업데이트가 완료되었습니다.", responseOrderUpdateDto);
    }
}
