package com.spin.transaction_executor.config;

import ch.qos.logback.core.util.StringUtil;
import com.spin.transaction_executor.util.Constants;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Order(1)
@Component
public class HttpFilter extends GenericFilterBean {

    @Value("${app.security.api-key}")
    private String apiKey;

    private static final List<String> EXCLUDED_URLS_SWAGGER = Arrays.asList(
            "/v3/api-docs",
            "/swagger-ui",
            "/swagger-ui.html",
            "/swagger-ui/index.html",
            "/favicon.ico"
    );

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        ResetteableStreamHttpServlet wrappedRequest = new ResetteableStreamHttpServlet((HttpServletRequest) request);
        String body = IOUtils.toString(wrappedRequest.getReader());
        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper((HttpServletResponse) response);
        boolean isExcludedUrl = EXCLUDED_URLS_SWAGGER.stream().anyMatch(wrappedRequest.getRequestURI()::startsWith);
        if (!isExcludedUrl) {
            try {
                MDC.put(Constants.REQUEST_ID,java.util.UUID.randomUUID().toString());
                log.info(Constants.EXECUTING, wrappedRequest.getRequestURI());
                log.info(Constants.INPUT, body);

                String requestApiKey = ((HttpServletRequest) request).getHeader(Constants.API_KEY_HEADER);

                if (requestApiKey == null || !requestApiKey.equals(apiKey)) {
                    unauthorizedResponse(responseWrapper, "Invalid or missing API key");
                    return;
                }

                wrappedRequest.resetInputStream();
                chain.doFilter(wrappedRequest, responseWrapper);
            } catch (Exception e) {
                log.error("FATAL ERROR: {}", e.getMessage());
            } finally {
                byte[] responseArray=responseWrapper.getContentAsByteArray();
                String result = new String(responseArray,responseWrapper.getCharacterEncoding());
                try{
                    if (StringUtil.notNullNorEmpty(result)) {
                        log.info(Constants.OUTPUT, result);
                    } else {
                        log.info("Unable to log output");
                    }
                }
                catch(Exception e){
                    log.info("Unable to log output");
                }
                responseWrapper.copyBodyToResponse();
                MDC.remove(Constants.REQUEST_ID);
            }
        } else {
            chain.doFilter(wrappedRequest, responseWrapper);
            responseWrapper.copyBodyToResponse();
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
}