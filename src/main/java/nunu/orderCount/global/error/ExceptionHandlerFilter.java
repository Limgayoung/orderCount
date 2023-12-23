package nunu.orderCount.global.error;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nunu.orderCount.global.error.exception.BusinessException;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class ExceptionHandlerFilter extends OncePerRequestFilter {
    private final ObjectMapper objectMapper;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        } catch (BusinessException e) {
            setErrorResponse(response, e);
        } catch (Exception e) {
            setUnexpectedErrorResponse(response, e);
        }
    }

    private void setErrorResponse(HttpServletResponse response, BusinessException e) {
        ErrorResponse errorResponse = new ErrorResponse(e.getErrorCode(), e.getMessage());
        log.error("error occurred in filter: {}", errorResponse.getTrackingId());
        log.error("error message: {}", e.getMessage());
        writeResponse(response, errorResponse, e.getErrorCode().getStatus());
    }
    private void setUnexpectedErrorResponse(HttpServletResponse response, Exception e) {
        ErrorResponse errorResponse = new ErrorResponse(ErrorCode.INTERNAL_SERVER_ERROR, e.getMessage());
        log.error("unexpected error occurred in filter: {}", errorResponse.getTrackingId());
        log.error("error message: {}", e.getMessage() + "\n" + response.toString());
        writeResponse(response, errorResponse, 500);
    }
    private void writeResponse(HttpServletResponse response, ErrorResponse errorResponse, int status) {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.setStatus(status);
        try {
            response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
