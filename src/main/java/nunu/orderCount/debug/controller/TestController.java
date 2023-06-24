package nunu.orderCount.debug.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "test api", description = "debug용 api 모음")
@RestController
@RequestMapping("/test")
@RequiredArgsConstructor
public class TestController {

    @Operation(summary = "swagger 작동 확인용 api", description = "swagger 작동을 확인합니다.", responses = {
            @ApiResponse(responseCode = "200", description = "성공")
    })
    @GetMapping("/swagger")
    public ResponseEntity<String> swaggerTest(){
        return ResponseEntity.ok("ok");
    }
}
