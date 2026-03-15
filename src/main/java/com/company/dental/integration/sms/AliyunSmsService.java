package com.company.dental.integration.sms;

import com.company.dental.common.enums.ErrorCode;
import com.company.dental.common.exception.BusinessException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

@Service
public class AliyunSmsService implements SmsService {

    private static final DateTimeFormatter TIMESTAMP_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")
            .withZone(ZoneOffset.UTC);

    private final AliyunSmsProperties properties;
    private final ObjectMapper objectMapper;
    private final HttpClient httpClient = HttpClient.newHttpClient();

    public AliyunSmsService(AliyunSmsProperties properties, ObjectMapper objectMapper) {
        this.properties = properties;
        this.objectMapper = objectMapper;
    }

    @Override
    public SmsSendResult send(SmsSendRequest request) {
        if (!properties.isEnabled() || properties.isMockEnabled()) {
            return SmsSendResult.builder()
                    .success(true)
                    .requestId(UUID.randomUUID().toString())
                    .message("短信发送已切到 mock 模式")
                    .build();
        }
        validateConfig(request);
        try {
            Map<String, String> queryParams = buildQueryParams(request);
            String canonicalizedQuery = canonicalizedQuery(queryParams);
            String stringToSign = "GET&%2F&" + percentEncode(canonicalizedQuery);
            String signature = sign(stringToSign, properties.getAccessKeySecret() + "&");
            String finalQuery = canonicalizedQuery + "&Signature=" + percentEncode(signature);

            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .GET()
                    .uri(URI.create(properties.getEndpoint() + "/?" + finalQuery))
                    .build();
            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            return parseResponse(response.body(), response.statusCode());
        } catch (BusinessException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "阿里云短信发送失败: " + ex.getMessage());
        }
    }

    private void validateConfig(SmsSendRequest request) {
        if (!StringUtils.hasText(properties.getAccessKeyId()) || !StringUtils.hasText(properties.getAccessKeySecret())) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "阿里云短信配置不完整");
        }
        if (!StringUtils.hasText(request.getPhone()) || !StringUtils.hasText(request.getTemplateCode())) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "短信发送参数不完整");
        }
        if (!StringUtils.hasText(resolveSignName(request))) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "短信签名未配置");
        }
    }

    private Map<String, String> buildQueryParams(SmsSendRequest request) {
        Map<String, String> params = new TreeMap<>();
        params.put("AccessKeyId", properties.getAccessKeyId());
        params.put("Action", properties.getAction());
        params.put("Format", "JSON");
        params.put("PhoneNumbers", request.getPhone());
        params.put("RegionId", properties.getRegionId());
        params.put("SignName", resolveSignName(request));
        params.put("SignatureMethod", "HMAC-SHA1");
        params.put("SignatureNonce", UUID.randomUUID().toString());
        params.put("SignatureVersion", "1.0");
        params.put("TemplateCode", request.getTemplateCode());
        params.put("Timestamp", TIMESTAMP_FORMATTER.format(Instant.now()));
        params.put("Version", properties.getVersion());
        if (request.getTemplateParams() != null && !request.getTemplateParams().isEmpty()) {
            params.put("TemplateParam", toJson(request.getTemplateParams()));
        }
        return params;
    }

    private SmsSendResult parseResponse(String body, int statusCode) {
        try {
            Map<String, Object> payload = objectMapper.readValue(body, new TypeReference<>() {
            });
            String code = payload.get("Code") == null ? null : String.valueOf(payload.get("Code"));
            String requestId = payload.get("RequestId") == null ? null : String.valueOf(payload.get("RequestId"));
            String message = payload.get("Message") == null ? "unknown" : String.valueOf(payload.get("Message"));
            boolean success = statusCode >= 200 && statusCode < 300 && "OK".equalsIgnoreCase(code);
            return SmsSendResult.builder()
                    .success(success)
                    .requestId(requestId)
                    .message(success ? "短信发送成功" : message)
                    .build();
        } catch (Exception ex) {
            return SmsSendResult.builder()
                    .success(false)
                    .requestId(null)
                    .message("短信响应解析失败")
                    .build();
        }
    }

    private String resolveSignName(SmsSendRequest request) {
        return StringUtils.hasText(request.getSignName()) ? request.getSignName() : properties.getDefaultSignName();
    }

    private String canonicalizedQuery(Map<String, String> params) {
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (builder.length() > 0) {
                builder.append('&');
            }
            builder.append(percentEncode(entry.getKey()))
                    .append('=')
                    .append(percentEncode(entry.getValue()));
        }
        return builder.toString();
    }

    private String sign(String content, String secret) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA1");
        mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA1"));
        return Base64.getEncoder().encodeToString(mac.doFinal(content.getBytes(StandardCharsets.UTF_8)));
    }

    private String percentEncode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8)
                .replace("+", "%20")
                .replace("*", "%2A")
                .replace("%7E", "~");
    }

    private String toJson(Map<String, String> params) {
        try {
            return objectMapper.writeValueAsString(new LinkedHashMap<>(params));
        } catch (JsonProcessingException ex) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "短信模板参数序列化失败");
        }
    }
}
