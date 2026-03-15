package com.company.dental.common.enums;

import lombok.Getter;

@Getter
public enum ErrorCode {

    SUCCESS(0, "success"),
    BAD_REQUEST(400, "请求参数错误"),
    UNAUTHORIZED(401, "未登录或登录已失效"),
    FORBIDDEN(403, "无权访问"),
    NOT_FOUND(404, "资源不存在"),
    INVALID_CREDENTIALS(1001, "用户名或密码错误"),
    ACCOUNT_DISABLED(1002, "账号已停用"),
    TOKEN_INVALID(1003, "Token 无效或已过期"),
    PATIENT_NOT_FOUND(2001, "患者不存在"),
    APPOINTMENT_NOT_FOUND(3001, "预约不存在"),
    MEDICAL_RECORD_NOT_FOUND(4001, "病历不存在"),
    TREATMENT_PLAN_NOT_FOUND(4002, "治疗计划不存在"),
    CONSENT_FORM_NOT_FOUND(4003, "知情同意书不存在"),
    SYSTEM_ERROR(5000, "系统异常，请稍后重试");

    private final int code;
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
