package nunu.orderCount.domain.order.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Tag(name = "Order 관련 API")
@RequestMapping("/api/orders")
@RestController
@RequiredArgsConstructor
public class OrderController {
}
