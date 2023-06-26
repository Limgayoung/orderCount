package nunu.orderCount.debug.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nunu.orderCount.global.response.Response;
import nunu.orderCount.global.response.ResponseCode;
import nunu.orderCount.infra.zigzag.model.dto.request.RequestZigzagLoginDto;
import nunu.orderCount.infra.zigzag.service.ZigzagAuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Tag(name = "crawler test api", description = "debug용 zigzag crawler api 모음")
@RestController
@RequestMapping("/test/crawler")
@RequiredArgsConstructor
public class CrawlerTestController {
    private final ZigzagAuthService zigzagAuthService;

    @PostMapping("")
    public ResponseEntity<Response> loginTest(@RequestBody RequestZigzagLoginDto dto) {
        return Response.SUCCESS(ResponseCode.SUCCESS, zigzagAuthService.zigzagLogin(dto));
    }
}
