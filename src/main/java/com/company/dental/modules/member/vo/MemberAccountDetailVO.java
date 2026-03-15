package com.company.dental.modules.member.vo;

import com.company.dental.modules.patient.vo.PatientMemberBalanceRecordVO;
import com.company.dental.modules.patient.vo.PatientMemberPointsRecordVO;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Data
public class MemberAccountDetailVO {

    private Long id;
    private Long orgId;
    private Long patientId;
    private String patientName;
    private String patientMobile;
    private String memberNo;
    private Long levelId;
    private String levelCode;
    private String levelName;
    private BigDecimal discountRate;
    private BigDecimal pointsRate;
    private BigDecimal balanceAmount;
    private Integer pointsBalance;
    private BigDecimal totalRechargeAmount;
    private BigDecimal totalConsumeAmount;
    private String memberStatus;
    private LocalDateTime activatedAt;
    private LocalDateTime expiredAt;
    private List<PatientMemberBalanceRecordVO> recentBalanceRecords = Collections.emptyList();
    private List<PatientMemberPointsRecordVO> recentPointsRecords = Collections.emptyList();
}
