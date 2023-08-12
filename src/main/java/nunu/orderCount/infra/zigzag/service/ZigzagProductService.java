package nunu.orderCount.infra.zigzag.service;

import lombok.extern.slf4j.Slf4j;
import nunu.orderCount.infra.zigzag.exception.ZigzagParsingException;
import nunu.orderCount.infra.zigzag.model.dto.request.RequestZigzagProductInfoDto;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class ZigzagProductService extends ZigzagWebClientRequester{

    private final String PRODUCT_REQUEST_URI;
    private final String query;

    public ZigzagProductService(WebClient webClient,
                                @Value("${webclient.zigzag.product.uri}") String productRequestUri,
                                @Value("${webclient.zigzag.product.query}") String query) {
        super(webClient);
        this.PRODUCT_REQUEST_URI = productRequestUri;
        this.query = query;
    }

    public String ZigzagProductImageUrlRequester(String cookie, String productId){
        RequestZigzagProductInfoDto requestZigzagProductInfoDto = new RequestZigzagProductInfoDto(query, Arrays.asList(productId));
        String responseJson = post(PRODUCT_REQUEST_URI, cookie, requestZigzagProductInfoDto, String.class);
        return parseProductInfo(responseJson);
    }

    /**
     * @param cookie
     * @param productIdList
     * @return productId, imageUrl
     */
    public Map<String, String> ZigzagProductImagesUrlRequester(String cookie, List<String> productIdList){
        RequestZigzagProductInfoDto requestZigzagProductInfoDto = new RequestZigzagProductInfoDto(query, productIdList);
        String responseJson = post(PRODUCT_REQUEST_URI, cookie, requestZigzagProductInfoDto, String.class);
        return parseProductInfoList(responseJson);
    }

    private String parseProductInfo(String json) {
        JSONParser parser = new JSONParser();
        String imageUrl = "";
        try {

            JSONObject jsonObj = (JSONObject) parser.parse(json);
            JSONObject data = (JSONObject) jsonObj.get("data");
            JSONObject catalogProductList = (JSONObject) data.get("catalog_product_list");
            long totalCount = (long) catalogProductList.get("total_count");
            JSONArray productList = (JSONArray) catalogProductList.get("product_list");

            for(int i=0;i<productList.size();i++){
                JSONObject productInfo = (JSONObject) productList.get(i);
                JSONArray productImageList = (JSONArray) productInfo.get("product_image_list");

                //가장 앞의 main 사진 1장만 저장
                JSONObject productImage = (JSONObject) productImageList.get(0);
                imageUrl = (String) productImage.get("image_url");

            }
        } catch (ParseException e) {
            throw new ZigzagParsingException("zigzag 에서 주문 정보를 받아올 수 없습니다.");
        }

        if (imageUrl.equals("")) {
            log.info("이미지 url을 받아오지 못했습니다.");
        }
        return imageUrl;
    }

    private Map<String, String> parseProductInfoList(String json) {
        JSONParser parser = new JSONParser();
        Map<String, String> productImgMap = new HashMap<>();
        try {

            JSONObject jsonObj = (JSONObject) parser.parse(json);
            JSONObject data = (JSONObject) jsonObj.get("data");
            JSONObject catalogProductList = (JSONObject) data.get("catalog_product_list");
            long totalCount = (long) catalogProductList.get("total_count");
            JSONArray productList = (JSONArray) catalogProductList.get("product_list");

            for(int i=0;i<productList.size();i++){
                JSONObject productInfo = (JSONObject) productList.get(i);
                String productId = (String) productInfo.get("id");

                //가장 앞의 main 사진 1장만 저장
                JSONArray productImageList = (JSONArray) productInfo.get("product_image_list");
                JSONObject productImage = (JSONObject) productImageList.get(0);
                String imageUrl = (String) productImage.get("image_url");

                productImgMap.put(productId, imageUrl);
            }
        } catch (ParseException e) {
            throw new ZigzagParsingException("zigzag 에서 주문 정보를 받아올 수 없습니다.");
        }

        return productImgMap;
    }
}
