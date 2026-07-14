package com.spin.transaction_executor.config;

import com.spin.transaction_executor.util.Constants;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@Order(1)
@Component
public class HttpFilter extends GenericFilterBean {

    @Value("${app.security.api-key}")
    private String apiKey;

    private static final Set<String> EXCLUDED_URLS_SWAGGER = new HashSet<>();
    static {
        EXCLUDED_URLS_SWAGGER.add("/v3/api-docs");
        EXCLUDED_URLS_SWAGGER.add("/swagger-ui");
        EXCLUDED_URLS_SWAGGER.add("/favicon.ico");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        String requestUri = httpRequest.getRequestURI();

        if (isExcluded(requestUri)) {
            chain.doFilter(request, response);
            return;
        }

        try {
            String requestApiKey = httpRequest.getHeader(Constants.API_KEY_HEADER);

            if (requestApiKey == null || !requestApiKey.equals(apiKey)) {
                unauthorizedResponse(httpResponse, "Invalid or missing API key");
                return;
            }

            chain.doFilter(request, response);
        } catch (Exception e) {
            log.error("FATAL ERROR: ", e);
        }
    }

    private void unauthorizedResponse(HttpServletResponse response, String message) throws IOException {
        response.resetBuffer();
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(
                "{\"error\":\"Unauthorized\",\"message\":\"" + message + "\"}"
        );
        response.getWriter().flush();
    }

    private boolean isExcluded(String uri) {
        for (String prefix : EXCLUDED_URLS_SWAGGER) {
            if (uri.startsWith(prefix)) {
                return true;
            }
        }
        return false;
    }
}