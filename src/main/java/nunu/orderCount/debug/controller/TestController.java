package nunu.orderCount.debug.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nunu.orderCount.global.response.Response;
import nunu.orderCount.global.response.ResponseCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Tag(name = "test api", description = "debug용 api 모음")
@RestController
@RequestMapping("/test")
@RequiredArgsConstructor
public class TestController {

    @Operation(summary = "swagger 작동 확인용 api", description = "swagger 작동을 확인합니다.", responses = {
            @ApiResponse(responseCode = "200", description = "성공")
    })
    @GetMapping("/swagger")
    public ResponseEntity<Response> swaggerTest(){
        return Response.SUCCESS(ResponseCode.SUCCESS, "data");
    }

    @GetMapping("/response/success")
    public ResponseEntity<Response> responseSuccessTest(){
        log.info("test");
        return Response.SUCCESS(ResponseCode.SUCCESS, "data");
    }
    @GetMapping("/response/success/message")
    public ResponseEntity<Response> responseSuccessMessageTest(){
        log.info("test");
        return Response.SUCCESS(ResponseCode.SUCCESS, "message", "data");
    }
    @GetMapping("/response/success/void")
    public ResponseEntity<Response> responseSuccessVoidTest(){
        log.info("test");
        return Response.SUCCESS();
    }
    @GetMapping("/response/failure")
    public ResponseEntity<Response> responseFailureVoidTest(){
        log.info("test");
        return Response.FAILURE(ResponseCode.FAILURE);
    }
    @GetMapping("/response/failure/message")
    public ResponseEntity<Response> responseFailureTest(){
        log.info("test");
        return Response.FAILURE(ResponseCode.FAILURE, "message");
    }

}
