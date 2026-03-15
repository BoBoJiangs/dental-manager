package com.company.dental.framework.web;

import com.company.dental.framework.security.AuthContext;
import com.company.dental.framework.security.LoginUser;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.Set;

@Slf4j
@Component
public class ApiAuditLogFilter extends OncePerRequestFilter {

    private static final Set<String> IGNORE_PREFIXES = Set.of(
            "/swagger-ui",
            "/v3/api-docs",
            "/doc.html",
            "/error"
    );

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return IGNORE_PREFIXES.stream().anyMatch(path::startsWith);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        Instant start = Instant.now();
        try {
            filterChain.doFilter(request, response);
        } finally {
            long durationMs = Duration.between(start, Instant.now()).toMillis();
            LoginUser loginUser = AuthContext.get();
            String username = loginUser == null ? "anonymous" : loginUser.getUsername();
            Long orgId = loginUser == null ? null : loginUser.getOrgId();
            Long staffId = loginUser == null ? null : loginUser.getStaffId();
            log.info("api_audit method={} path={} status={} durationMs={} ip={} username={} orgId={} staffId={} traceId={} requestId={}",
                    request.getMethod(),
                    request.getRequestURI(),
                    response.getStatus(),
                    durationMs,
                    request.getRemoteAddr(),
                    username,
                    orgId,
                    staffId,
                    MDC.get(TraceContext.TRACE_ID_KEY),
                    MDC.get(TraceContext.REQUEST_ID_KEY));
        }
    }
}
