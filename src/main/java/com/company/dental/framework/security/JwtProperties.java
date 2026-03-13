package com.company.dental.framework.security;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "dental.jwt")
public class JwtProperties {

    private String secret;
    private long accessTokenExpireMinutes;
    private String issuer;
    private String headerName = "Authorization";
    private String tokenPrefix = "Bearer ";
}
