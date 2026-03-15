package com.company.dental.framework.web;

public final class TraceContext {

    public static final String TRACE_ID_KEY = "traceId";
    public static final String REQUEST_ID_KEY = "requestId";
    public static final String TRACE_ID_HEADER = "X-Trace-Id";
    public static final String REQUEST_ID_HEADER = "X-Request-Id";

    private TraceContext() {
    }
}
