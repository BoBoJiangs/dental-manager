package com.company.dental.modules.member.mapper;

import com.company.dental.modules.patient.vo.PatientMemberBalanceRecordVO;
import com.company.dental.modules.patient.vo.PatientMemberInfoVO;
import com.company.dental.modules.patient.vo.PatientMemberPointsRecordVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface MemberAccountQueryMapper {

    @Select("""
            SELECT ma.id AS memberAccountId,
                   ma.member_no AS memberNo,
                   ma.level_id AS levelId,
                   ml.level_code AS levelCode,
                   ml.level_name AS levelName,
                   ml.discount_rate AS discountRate,
                   ml.points_rate AS pointsRate,
                   ma.balance_amount AS balanceAmount,
                   ma.points_balance AS pointsBalance,
                   ma.total_recharge_amount AS totalRechargeAmount,
                   ma.total_consume_amount AS totalConsumeAmount,
                   ma.member_status AS memberStatus,
                   ma.activated_at AS activatedAt,
                   ma.expired_at AS expiredAt
            FROM member_account ma
            LEFT JOIN member_level ml ON ml.id = ma.level_id
            WHERE ma.org_id = #{orgId}
              AND ma.patient_id = #{patientId}
            LIMIT 1
            """)
    PatientMemberInfoVO selectPatientMemberInfo(@Param("orgId") Long orgId, @Param("patientId") Long patientId);

    @Select("""
            SELECT id,
                   biz_type AS bizType,
                   biz_id AS bizId,
                   change_amount AS changeAmount,
                   before_balance AS beforeBalance,
                   after_balance AS afterBalance,
                   operator_id AS operatorId,
                   remark,
                   created_at AS createdAt
            FROM member_balance_record
            WHERE patient_id = #{patientId}
            ORDER BY created_at DESC, id DESC
            LIMIT 5
            """)
    List<PatientMemberBalanceRecordVO> selectRecentBalanceRecords(@Param("patientId") Long patientId);

    @Select("""
            SELECT id,
                   biz_type AS bizType,
                   biz_id AS bizId,
                   change_points AS changePoints,
                   before_points AS beforePoints,
                   after_points AS afterPoints,
                   operator_id AS operatorId,
                   remark,
                   created_at AS createdAt
            FROM member_points_record
            WHERE patient_id = #{patientId}
            ORDER BY created_at DESC, id DESC
            LIMIT 5
            """)
    List<PatientMemberPointsRecordVO> selectRecentPointsRecords(@Param("patientId") Long patientId);
}
