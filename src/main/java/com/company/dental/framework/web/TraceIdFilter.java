package com.company.dental.framework.web;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
public class TraceIdFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String traceId = resolveHeader(request.getHeader(TraceContext.TRACE_ID_HEADER));
        String requestId = resolveHeader(request.getHeader(TraceContext.REQUEST_ID_HEADER));
        MDC.put(TraceContext.TRACE_ID_KEY, traceId);
        MDC.put(TraceContext.REQUEST_ID_KEY, requestId);
        response.setHeader(TraceContext.TRACE_ID_HEADER, traceId);
        response.setHeader(TraceContext.REQUEST_ID_HEADER, requestId);
        try {
            filterChain.doFilter(request, response);
        } finally {
            MDC.remove(TraceContext.TRACE_ID_KEY);
            MDC.remove(TraceContext.REQUEST_ID_KEY);
        }
    }

    private String resolveHeader(String value) {
        return StringUtils.hasText(value) ? value.trim() : UUID.randomUUID().toString().replace("-", "");
    }
}
