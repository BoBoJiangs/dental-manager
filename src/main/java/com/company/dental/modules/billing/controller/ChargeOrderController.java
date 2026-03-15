package com.company.dental.modules.billing.controller;

import com.company.dental.common.api.ApiResponse;
import com.company.dental.common.api.PageResult;
import com.company.dental.modules.billing.dto.ChargeOrderCreateRequest;
import com.company.dental.modules.billing.dto.PaymentCreateRequest;
import com.company.dental.modules.billing.dto.RefundCreateRequest;
import com.company.dental.modules.billing.query.ChargeOrderPageQuery;
import com.company.dental.modules.billing.service.ChargeOrderService;
import com.company.dental.modules.billing.vo.ChargeOrderDetailVO;
import com.company.dental.modules.billing.vo.ChargeOrderPageItemVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "收费管理")
@RestController
@RequestMapping("/api/billing/charge-orders")
public class ChargeOrderController {

    private final ChargeOrderService chargeOrderService;

    public ChargeOrderController(ChargeOrderService chargeOrderService) {
        this.chargeOrderService = chargeOrderService;
    }

    @Operation(summary = "分页查询收费单")
    @GetMapping
    public ApiResponse<PageResult<ChargeOrderPageItemVO>> page(@Valid @ModelAttribute ChargeOrderPageQuery query) {
        return ApiResponse.success(chargeOrderService.pageChargeOrders(query));
    }

    @Operation(summary = "查询收费单详情")
    @GetMapping("/{id}")
    public ApiResponse<ChargeOrderDetailVO> detail(@PathVariable Long id) {
        return ApiResponse.success(chargeOrderService.getChargeOrderDetail(id));
    }

    @Operation(summary = "创建收费单")
    @PreAuthorize("hasAuthority('BILLING_CREATE')")
    @PostMapping
    public ApiResponse<ChargeOrderDetailVO> create(@Valid @RequestBody ChargeOrderCreateRequest request) {
        return ApiResponse.success(chargeOrderService.createChargeOrder(request));
    }

    @Operation(summary = "登记支付")
    @PreAuthorize("hasAuthority('BILLING_PAYMENT')")
    @PutMapping("/{id}/payments")
    public ApiResponse<ChargeOrderDetailVO> pay(@PathVariable Long id, @Valid @RequestBody PaymentCreateRequest request) {
        return ApiResponse.success(chargeOrderService.createPayment(id, request));
    }

    @Operation(summary = "登记退款")
    @PreAuthorize("hasAuthority('BILLING_REFUND')")
    @PutMapping("/{id}/refunds")
    public ApiResponse<ChargeOrderDetailVO> refund(@PathVariable Long id, @Valid @RequestBody RefundCreateRequest request) {
        return ApiResponse.success(chargeOrderService.createRefund(id, request));
    }
}
