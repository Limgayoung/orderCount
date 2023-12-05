package nunu.orderCount.domain.order.controller;

import nunu.orderCount.domain.order.model.dto.request.RequestOrderUpdateDto;
import nunu.orderCount.domain.order.model.dto.response.ResponseOrderUpdateDto;
import nunu.orderCount.domain.order.service.OrderServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class OrderControllerTest {

    @Autowired
    private MockMvc mvc;

    @Mock
    private OrderServiceImpl orderService;

    @InjectMocks
    private OrderController orderController;

    @Value("zigzag.id")
    private String zigzagId;

    @Value("zigzag.password")
    private String zigzagPassword;


    private final String BASE_URL = "/api/orders";

    @BeforeEach
    void setUp() {

    }

    @DisplayName("주문 업데이트")
    @Test
    void updateZigzagOrder() throws Exception {
        //given
//        doReturn(new ResponseOrderUpdateDto(1, 1)).when(orderService).orderUpdate(new RequestOrderUpdateDto(anyLong()));
        //when


        //then
//        mvc.perform(post(BASE_URL + "/" + 1L))
//                .andExpect(status().isOk());


    }
}