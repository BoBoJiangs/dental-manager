package com.company.dental.modules.billing.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.company.dental.modules.billing.entity.ChargeOrderEntity;
import com.company.dental.modules.billing.query.ChargeOrderPageQuery;
import com.company.dental.modules.billing.vo.ChargeOrderDetailVO;
import com.company.dental.modules.billing.vo.ChargeOrderPageItemVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface ChargeOrderMapper extends BaseMapper<ChargeOrderEntity> {

    Page<ChargeOrderPageItemVO> selectChargeOrderPage(Page<ChargeOrderPageItemVO> page,
                                                      @Param("query") ChargeOrderPageQuery query,
                                                      @Param("orgId") Long orgId,
                                                      @Param("accessScope") String accessScope,
                                                      @Param("clinicIds") List<Long> clinicIds,
                                                      @Param("staffId") Long staffId);

    ChargeOrderDetailVO selectChargeOrderDetailById(@Param("id") Long id,
                                                    @Param("orgId") Long orgId,
                                                    @Param("accessScope") String accessScope,
                                                    @Param("clinicIds") List<Long> clinicIds,
                                                    @Param("staffId") Long staffId);

    @Select("""
            SELECT COUNT(1)
            FROM patient
            WHERE id = #{patientId}
              AND org_id = #{orgId}
              AND is_deleted = 0
            """)
    Long countValidPatient(@Param("orgId") Long orgId, @Param("patientId") Long patientId);

    @Select("""
            SELECT COUNT(1)
            FROM clinic
            WHERE id = #{clinicId}
              AND org_id = #{orgId}
              AND status = 1
              AND is_deleted = 0
            """)
    Long countValidClinic(@Param("orgId") Long orgId, @Param("clinicId") Long clinicId);

    @Select("""
            SELECT COUNT(1)
            FROM medical_record
            WHERE id = #{medicalRecordId}
              AND org_id = #{orgId}
              AND is_deleted = 0
            """)
    Long countValidMedicalRecord(@Param("orgId") Long orgId, @Param("medicalRecordId") Long medicalRecordId);

    @Select("""
            SELECT COUNT(1)
            FROM treatment_plan
            WHERE id = #{treatmentPlanId}
              AND org_id = #{orgId}
              AND is_deleted = 0
            """)
    Long countValidTreatmentPlan(@Param("orgId") Long orgId, @Param("treatmentPlanId") Long treatmentPlanId);

    @Select("""
            SELECT COALESCE(SUM(receivable_amount), 0)
            FROM charge_order
            WHERE org_id = #{orgId}
              AND is_deleted = 0
              AND charge_time >= #{start}
              AND charge_time < #{end}
            """)
    BigDecimal sumReceivableAmount(@Param("orgId") Long orgId,
                                   @Param("start") LocalDateTime start,
                                   @Param("end") LocalDateTime end);

    @Select("""
            SELECT COALESCE(SUM(paid_amount), 0)
            FROM charge_order
            WHERE org_id = #{orgId}
              AND is_deleted = 0
              AND charge_time >= #{start}
              AND charge_time < #{end}
            """)
    BigDecimal sumPaidAmount(@Param("orgId") Long orgId,
                             @Param("start") LocalDateTime start,
                             @Param("end") LocalDateTime end);
}
