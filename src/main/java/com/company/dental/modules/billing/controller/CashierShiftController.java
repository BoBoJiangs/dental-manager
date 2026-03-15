package com.company.dental.modules.billing.controller;

import com.company.dental.common.api.ApiResponse;
import com.company.dental.common.api.PageResult;
import com.company.dental.modules.billing.dto.CashierShiftCloseRequest;
import com.company.dental.modules.billing.dto.CashierShiftOpenRequest;
import com.company.dental.modules.billing.query.CashierShiftQuery;
import com.company.dental.modules.billing.service.CashierShiftService;
import com.company.dental.modules.billing.vo.CashierShiftVO;
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

@Tag(name = "收银交班")
@RestController
@RequestMapping("/api/billing/cashier-shifts")
public class CashierShiftController {

    private final CashierShiftService cashierShiftService;

    public CashierShiftController(CashierShiftService cashierShiftService) {
        this.cashierShiftService = cashierShiftService;
    }

    @Operation(summary = "分页查询交班记录")
    @GetMapping
    public ApiResponse<PageResult<CashierShiftVO>> page(@ModelAttribute CashierShiftQuery query) {
        return ApiResponse.success(cashierShiftService.pageShifts(query));
    }

    @Operation(summary = "查询交班详情")
    @GetMapping("/{id}")
    public ApiResponse<CashierShiftVO> detail(@PathVariable Long id) {
        return ApiResponse.success(cashierShiftService.getShiftDetail(id));
    }

    @Operation(summary = "开班")
    @PreAuthorize("hasAuthority('BILLING_PAYMENT')")
    @PostMapping
    public ApiResponse<CashierShiftVO> open(@Valid @RequestBody CashierShiftOpenRequest request) {
        return ApiResponse.success(cashierShiftService.openShift(request));
    }

    @Operation(summary = "关班")
    @PreAuthorize("hasAuthority('BILLING_PAYMENT')")
    @PutMapping("/{id}/close")
    public ApiResponse<CashierShiftVO> close(@PathVariable Long id,
                                             @Valid @RequestBody CashierShiftCloseRequest request) {
        return ApiResponse.success(cashierShiftService.closeShift(id, request));
    }
}
