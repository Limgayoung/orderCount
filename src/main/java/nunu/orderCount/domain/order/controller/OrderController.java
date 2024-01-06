package nunu.orderCount.domain.order.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nunu.orderCount.domain.member.model.MemberInfo;
import nunu.orderCount.domain.member.service.MemberService;
import nunu.orderCount.domain.option.model.OptionDtoInfo;
import nunu.orderCount.domain.option.model.dto.request.RequestCreateOptionsDto;
import nunu.orderCount.domain.option.service.OptionService;
import nunu.orderCount.domain.order.model.OptionOrderInfo;
import nunu.orderCount.domain.order.model.OrderDtoInfo;
import nunu.orderCount.domain.order.model.OrderInfo;
import nunu.orderCount.domain.order.model.ProductOptionOrderInfo;
import nunu.orderCount.domain.order.model.dto.request.RequestFindOrdersByOptionGroupAndDateDto;
import nunu.orderCount.domain.order.model.dto.request.RequestFindOrdersDto;
import nunu.orderCount.domain.order.model.dto.request.RequestOrderUpdateDto;
import nunu.orderCount.domain.order.model.dto.response.ResponseFindOrdersByOptionDto;
import nunu.orderCount.domain.order.model.dto.response.ResponseOrderUpdateDto;
import nunu.orderCount.domain.order.service.OrderServiceImpl;
import nunu.orderCount.domain.product.model.ProductDtoInfo;
import nunu.orderCount.domain.product.model.dto.request.RequestUpdateProductDto;
import nunu.orderCount.domain.product.service.ProductServiceImpl;
import nunu.orderCount.global.error.ErrorResponse;
import nunu.orderCount.global.response.Response;
import nunu.orderCount.infra.zigzag.model.dto.response.ResponseZigzagOrderDto;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Tag(name = "Order 관련 API")
@RequestMapping("/api/orders")
@RestController
@RequiredArgsConstructor
public class OrderController {

    private final OrderServiceImpl orderService;
    private final MemberService memberService;
    private final ProductServiceImpl productService;
    private final OptionService optionService;

    //todo: swagger 위한 error 정리
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
        MemberInfo memberInfo = memberService.createMemberInfo(id);
        List<ResponseZigzagOrderDto> ordersFromZigzag = orderService.getOrdersFromZigzag(memberInfo);
        updateProduct(ordersFromZigzag, memberInfo);
        updateOption(ordersFromZigzag, memberInfo);
        ResponseOrderUpdateDto response = updateOrder(ordersFromZigzag, memberInfo);
        return Response.SUCCESS("주문 업데이트가 완료되었습니다.", response);
    }

    @Operation(summary = "전체 배송준비중 주문 옵션별 조회 API", description = "회원 id로 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "S200", description = "주문 조회 성공"),
            @ApiResponse(responseCode = "200(ResponseFindOrdersByOptionDto)", description = "ResponseFindOrdersByOptionDto data", content = @Content(schema = @Schema(implementation = ResponseFindOrdersByOptionDto.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<Response> findTotalOrders(@PathVariable("id") Long id) {
        MemberInfo memberInfo = memberService.createMemberInfo(id);
        ResponseFindOrdersByOptionDto response = orderService.findOrdersByOptionGroup(
                new RequestFindOrdersDto(memberInfo.getMember()));
        return Response.SUCCESS("주문 조회가 완료되었습니다.", response);
    }

    @Operation(summary = "기간별 배송준비중 주문 옵션별 조회 API", description = "회원 id, start/end date로 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "S200", description = "주문 조회 성공"),
            @ApiResponse(responseCode = "200(ResponseFindOrdersByOptionDto)", description = "ResponseFindOrdersByOptionDto data", content = @Content(schema = @Schema(implementation = ResponseFindOrdersByOptionDto.class)))
    })
    @GetMapping("/{id}/period")
    public ResponseEntity<Response> findPeriodOrders(@PathVariable("id") Long id,
                                                     @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam("startDate")LocalDate startDate,
                                                     @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam("endDate")LocalDate endDate) {
        MemberInfo memberInfo = memberService.createMemberInfo(id);
        ResponseFindOrdersByOptionDto response = orderService.findOrdersByOptionGroupAndDate(
                new RequestFindOrdersByOptionGroupAndDateDto(memberInfo.getMember(), startDate, endDate));
        return Response.SUCCESS("주문 조회가 완료되었습니다.", response);
    }

    private void updateProduct(List<ResponseZigzagOrderDto> ordersFromZigzag, MemberInfo memberInfo){
        List<ProductDtoInfo> productDtoInfos = ordersFromZigzag.stream()
                .map(o -> {
                    return new ProductDtoInfo(o.getProductName(), o.getProductId());
                })
                .distinct()
                .collect(Collectors.toList());
        productDtoInfos.forEach(productDtoInfo -> log.info("product: {} {}",productDtoInfo.getProductName(), productDtoInfo.getZigzagProductId()));

        productService.updateProduct(new RequestUpdateProductDto(memberInfo,productDtoInfos));
    }

    private void updateOption(List<ResponseZigzagOrderDto> ordersFromZigzag, MemberInfo memberInfo){
        List<OptionDtoInfo> optionDtoInfos = ordersFromZigzag.stream()
                .map(o -> {
                    return new OptionDtoInfo(o.getOption(), o.getProductId());
                })
                .distinct()
                .collect(Collectors.toList());
        optionService.createOptions(new RequestCreateOptionsDto(memberInfo, optionDtoInfos));
    }
    
    private ResponseOrderUpdateDto updateOrder(List<ResponseZigzagOrderDto> ordersFromZigzag, MemberInfo memberInfo){
        List<OrderDtoInfo> orderDtoInfos = ordersFromZigzag.stream()
                .map(o ->
                        new OrderDtoInfo(o.getQuantity(), o.getOrderItemNumber(), o.getOrderNumber(),
                                o.getOrderDateTime(), o.getProductId(), o.getOption())
                )
                .distinct()
                .collect(Collectors.toList());
        return orderService.orderUpdate(new RequestOrderUpdateDto(memberInfo, orderDtoInfos));
    }
}
