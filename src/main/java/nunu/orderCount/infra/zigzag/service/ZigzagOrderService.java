package nunu.orderCount.infra.zigzag.service;

import lombok.extern.slf4j.Slf4j;
import nunu.orderCount.infra.zigzag.exception.ZigzagParsingException;
import nunu.orderCount.infra.zigzag.model.dto.request.RequestZigzagOrderDto;
import nunu.orderCount.infra.zigzag.model.dto.response.ResponseZigzagOrderDto;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class ZigzagOrderService extends ZigzagWebClientRequester{
    private final String ORDER_REQUEST_URI;
    private final String query;

    public ZigzagOrderService(WebClient webClient,
                              @Value("${webclient.zigzag.order.uri}") String orderRequestUri,
                              @Value("${webclient.zigzag.order.query}") String query) {
        super(webClient);
        this.ORDER_REQUEST_URI = orderRequestUri;
        this.query = query;
    }

    public List<ResponseZigzagOrderDto> zigzagOrderListRequester(String cookie, Integer startDate, Integer endDate) {
        RequestZigzagOrderDto requestZigzagOrderDto = new RequestZigzagOrderDto(query, startDate, endDate);
        String responseJson = post(ORDER_REQUEST_URI, cookie, requestZigzagOrderDto, String.class);
        return parseOrderList(responseJson);
    }

    private List<ResponseZigzagOrderDto> parseOrderList(String json){ //필요한 정보만 parsing
        List<ResponseZigzagOrderDto> responseList = new ArrayList<>();
        JSONParser parser = new JSONParser();
        try {

            JSONObject jsonObj = (JSONObject) parser.parse(json);
            JSONObject data = (JSONObject) jsonObj.get("data");
            JSONObject partnerOrderItemList = (JSONObject) data.get("partner_order_item_list");
            long totalCount = (long) partnerOrderItemList.get("total_count");
            JSONArray itemList = (JSONArray) partnerOrderItemList.get("item_list");

            for(int i=0;i<itemList.size();i++){
                JSONObject orderInfo = (JSONObject) itemList.get(i);
                JSONObject order = (JSONObject) orderInfo.get("order");
                String orderNumber = (String) order.get("order_number");

                JSONObject productInfo = (JSONObject) orderInfo.get("product_info");
                String productName = (String) productInfo.get("name");
                String productOptionName = (String) productInfo.get("options");
                long quantity = (long) orderInfo.get("quantity");
                String orderItemNumber = (String) orderInfo.get("order_item_number");
                String productId = (String) orderInfo.get("product_id");
                long datePaid = (long) orderInfo.get("date_paid");

                ResponseZigzagOrderDto responseZigzagOrderDto = ResponseZigzagOrderDto.builder()
                        .orderNumber(orderNumber)
                        .orderItemNumber(orderItemNumber)
                        .option(productOptionName)
                        .productId(productId)
                        .productName(productName)
                        .datePaid(datePaid)
                        .quantity(quantity)
                        .totalOrderCount(totalCount)
                        .build();

                responseList.add(responseZigzagOrderDto);
            }
        } catch (ParseException e) {
            throw new ZigzagParsingException("zigzag 에서 주문 정보를 받아올 수 없습니다.");
        }
        return responseList;
    }

}