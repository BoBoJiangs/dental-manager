package com.company.dental.modules.member.service;

import com.company.dental.common.api.PageResult;
import com.company.dental.modules.member.dto.MemberAccountCreateRequest;
import com.company.dental.modules.member.dto.MemberPointsAdjustRequest;
import com.company.dental.modules.member.dto.MemberRechargeRequest;
import com.company.dental.modules.member.dto.MemberStatusUpdateRequest;
import com.company.dental.modules.member.query.MemberAccountPageQuery;
import com.company.dental.modules.member.vo.MemberAccountDetailVO;
import com.company.dental.modules.member.vo.MemberAccountPageItemVO;
import com.company.dental.modules.member.vo.MemberLevelOptionVO;

import java.util.List;

public interface MemberAccountService {

    PageResult<MemberAccountPageItemVO> pageMemberAccounts(MemberAccountPageQuery query);

    MemberAccountDetailVO getMemberAccountDetail(Long memberAccountId);

    MemberAccountDetailVO createMemberAccount(MemberAccountCreateRequest request);

    MemberAccountDetailVO rechargeMemberAccount(Long memberAccountId, MemberRechargeRequest request);

    MemberAccountDetailVO adjustMemberPoints(Long memberAccountId, MemberPointsAdjustRequest request);

    MemberAccountDetailVO updateMemberStatus(Long memberAccountId, MemberStatusUpdateRequest request);

    List<MemberLevelOptionVO> listLevelOptions();
}
