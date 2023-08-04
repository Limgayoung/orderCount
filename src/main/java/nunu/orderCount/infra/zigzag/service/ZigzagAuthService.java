package nunu.orderCount.infra.zigzag.service;

import lombok.extern.slf4j.Slf4j;
import nunu.orderCount.infra.zigzag.model.dto.request.RequestZigzagLoginDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Slf4j
@Service
public class ZigzagAuthService extends ZigzagWebClientRequester{

    private final String LOGIN_URI;

    public ZigzagAuthService(WebClient webClient,
                             @Value("${webclient.zigzag.login}") String loginUri) {
        super(webClient);
        this.LOGIN_URI = loginUri;
    }

    /**
     * zigzag login
     * @param dto
     * @return cookieString
     */
    public String zigzagLogin(RequestZigzagLoginDto dto) {
        ClientResponse.Headers headers = postGetHeader(LOGIN_URI, dto);
        List<String> cookie = headers.header("Set-Cookie");
        if (cookie.isEmpty()) {
            return null;
        }
        return cookie.get(0);
    }
}
