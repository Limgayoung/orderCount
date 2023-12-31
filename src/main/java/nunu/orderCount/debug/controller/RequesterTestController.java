package nunu.orderCount.debug.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nunu.orderCount.global.response.Response;
import nunu.orderCount.global.response.ResponseCode;
import nunu.orderCount.infra.zigzag.model.dto.request.RequestZigzagLoginDto;
import nunu.orderCount.infra.zigzag.service.ZigzagAuthService;
import nunu.orderCount.infra.zigzag.service.ZigzagOrderService;
import nunu.orderCount.infra.zigzag.service.ZigzagProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Tag(name = "requester test api", description = "debug용 zigzag requester api 모음")
@RestController
@RequestMapping("/api/test/requester")
@RequiredArgsConstructor
public class RequesterTestController {
    private final ZigzagAuthService zigzagAuthService;
    private final ZigzagOrderService zigzagOrderService;
    private final ZigzagProductService zigzagProductService;

    @PostMapping("")
    public ResponseEntity<Response> loginTest(@RequestBody RequestZigzagLoginDto dto) {
        return Response.SUCCESS(ResponseCode.SUCCESS, zigzagAuthService.zigzagLogin(dto));
    }

    @GetMapping("/orderList")
    public ResponseEntity<Response> orderListTest(@RequestParam("startDate") Integer startDate,
                                                  @RequestParam("endDate") Integer endDate,
                                                  @RequestParam("cookieString") String cookie) {
        return Response.SUCCESS(ResponseCode.SUCCESS, zigzagOrderService.zigzagOrderListRequester(cookie, startDate, endDate));
    }

    @GetMapping("/product/image")
    public ResponseEntity<Response> productImageTest(@RequestParam("cookieString") String cookie,
                                                     @RequestParam("productId") String productId) {
        return Response.SUCCESS(ResponseCode.SUCCESS, zigzagProductService.ZigzagProductImageUrlRequester(cookie, productId));
    }

    @GetMapping("/product/images")
    public ResponseEntity<Response> productImagesTest(@RequestParam("cookieString") String cookie,
                                                     @RequestParam("productIdList") List<String> productIdList) {
        return Response.SUCCESS(ResponseCode.SUCCESS, zigzagProductService.ZigzagProductImagesUrlRequester(cookie, productIdList));
    }
}
