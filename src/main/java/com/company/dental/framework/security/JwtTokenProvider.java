package com.company.dental.framework.security;

import com.company.dental.common.enums.ErrorCode;
import com.company.dental.common.exception.BusinessException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;

@Component
public class JwtTokenProvider {

    private final JwtProperties properties;
    private SecretKey secretKey;

    public JwtTokenProvider(JwtProperties properties) {
        this.properties = properties;
    }

    @PostConstruct
    public void init() {
        if (!StringUtils.hasText(properties.getSecret()) || properties.getSecret().length() < 32) {
            throw new IllegalStateException("dental.jwt.secret must contain at least 32 characters");
        }
        this.secretKey = Keys.hmacShaKeyFor(properties.getSecret().getBytes(StandardCharsets.UTF_8));
    }

    public String createAccessToken(LoginUser loginUser) {
        Instant now = Instant.now();
        Instant expireAt = now.plus(properties.getAccessTokenExpireMinutes(), ChronoUnit.MINUTES);
        return Jwts.builder()
                .subject(String.valueOf(loginUser.getUserId()))
                .issuer(properties.getIssuer())
                .issuedAt(Date.from(now))
                .expiration(Date.from(expireAt))
                .claim("orgId", loginUser.getOrgId())
                .claim("staffId", loginUser.getStaffId())
                .claim("username", loginUser.getUsername())
                .claim("accountType", loginUser.getAccountType())
                .claim("clinicId", loginUser.getClinicId())
                .claim("clinicIds", loginUser.getClinicIds())
                .claim("roles", loginUser.getRoles())
                .claim("dataScopes", loginUser.getDataScopes())
                .signWith(secretKey)
                .compact();
    }

    public LoginUser parseToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            Object clinicIdValue = claims.get("clinicId");
            Object clinicIdsValue = claims.get("clinicIds");
            return LoginUser.builder()
                    .userId(Long.valueOf(claims.getSubject()))
                    .orgId(claims.get("orgId", Long.class))
                    .staffId(claims.get("staffId", Long.class))
                    .username(claims.get("username", String.class))
                    .accountType(claims.get("accountType", String.class))
                    .clinicId(clinicIdValue == null ? null : Long.valueOf(String.valueOf(clinicIdValue)))
                    .clinicIds(clinicIdsValue instanceof List<?> ids
                            ? ids.stream().map(item -> Long.valueOf(String.valueOf(item))).toList()
                            : null)
                    .roles(claims.get("roles", List.class))
                    .dataScopes(claims.get("dataScopes", List.class))
                    .build();
        } catch (Exception ex) {
            throw new BusinessException(ErrorCode.TOKEN_INVALID);
        }
    }
}
