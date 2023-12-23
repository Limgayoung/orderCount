package nunu.orderCount.infra.zigzag.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import nunu.orderCount.infra.zigzag.model.dto.response.ResponseZigzagOrderDto;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
class ZigzagOrderServiceTest {

    @Autowired
    ZigzagOrderService zigzagOrderService;

    @Value("${webclient.zigzag.cookie}")
    String cookie;

    @Test
    void zigzagOrderListRequester() {
        List<ResponseZigzagOrderDto> responseZigzagOrders = zigzagOrderService.zigzagOrderListRequester(cookie,
                20231220, 20231222);

        assertThat(responseZigzagOrders.size()).isGreaterThan(0);
    }
    
    //todo: zigzag error 상황 test 필요
}