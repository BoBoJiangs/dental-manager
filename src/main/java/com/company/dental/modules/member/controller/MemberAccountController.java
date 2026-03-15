package com.company.dental.modules.member.controller;

import com.company.dental.common.api.ApiResponse;
import com.company.dental.common.api.PageResult;
import com.company.dental.modules.member.dto.MemberAccountCreateRequest;
import com.company.dental.modules.member.dto.MemberPointsAdjustRequest;
import com.company.dental.modules.member.dto.MemberRechargeRequest;
import com.company.dental.modules.member.dto.MemberStatusUpdateRequest;
import com.company.dental.modules.member.query.MemberAccountPageQuery;
import com.company.dental.modules.member.service.MemberAccountService;
import com.company.dental.modules.member.vo.MemberAccountDetailVO;
import com.company.dental.modules.member.vo.MemberAccountPageItemVO;
import com.company.dental.modules.member.vo.MemberLevelOptionVO;
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

import java.util.List;

@Tag(name = "会员管理")
@RestController
@RequestMapping("/api/members")
public class MemberAccountController {

    private final MemberAccountService memberAccountService;

    public MemberAccountController(MemberAccountService memberAccountService) {
        this.memberAccountService = memberAccountService;
    }

    @Operation(summary = "分页查询会员账户")
    @GetMapping
    public ApiResponse<PageResult<MemberAccountPageItemVO>> page(@Valid @ModelAttribute MemberAccountPageQuery query) {
        return ApiResponse.success(memberAccountService.pageMemberAccounts(query));
    }

    @Operation(summary = "查询会员账户详情")
    @GetMapping("/{id}")
    public ApiResponse<MemberAccountDetailVO> detail(@PathVariable Long id) {
        return ApiResponse.success(memberAccountService.getMemberAccountDetail(id));
    }

    @Operation(summary = "开通会员账户")
    @PreAuthorize("hasAuthority('MEMBER_CREATE')")
    @PostMapping
    public ApiResponse<MemberAccountDetailVO> create(@Valid @RequestBody MemberAccountCreateRequest request) {
        return ApiResponse.success(memberAccountService.createMemberAccount(request));
    }

    @Operation(summary = "会员充值")
    @PreAuthorize("hasAuthority('MEMBER_OPERATE')")
    @PutMapping("/{id}/recharge")
    public ApiResponse<MemberAccountDetailVO> recharge(@PathVariable Long id, @Valid @RequestBody MemberRechargeRequest request) {
        return ApiResponse.success(memberAccountService.rechargeMemberAccount(id, request));
    }

    @Operation(summary = "会员积分调整")
    @PreAuthorize("hasAuthority('MEMBER_OPERATE')")
    @PutMapping("/{id}/points")
    public ApiResponse<MemberAccountDetailVO> adjustPoints(@PathVariable Long id, @Valid @RequestBody MemberPointsAdjustRequest request) {
        return ApiResponse.success(memberAccountService.adjustMemberPoints(id, request));
    }

    @Operation(summary = "更新会员状态")
    @PreAuthorize("hasAuthority('MEMBER_OPERATE')")
    @PutMapping("/{id}/status")
    public ApiResponse<MemberAccountDetailVO> updateStatus(@PathVariable Long id, @Valid @RequestBody MemberStatusUpdateRequest request) {
        return ApiResponse.success(memberAccountService.updateMemberStatus(id, request));
    }

    @Operation(summary = "查询会员等级选项")
    @GetMapping("/level-options")
    public ApiResponse<List<MemberLevelOptionVO>> levelOptions() {
        return ApiResponse.success(memberAccountService.listLevelOptions());
    }
}
