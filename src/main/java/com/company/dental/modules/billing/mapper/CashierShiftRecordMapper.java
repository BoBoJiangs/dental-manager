package com.company.dental.modules.billing.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.company.dental.modules.billing.entity.CashierShiftRecordEntity;
import com.company.dental.modules.billing.query.CashierShiftQuery;
import com.company.dental.modules.billing.vo.CashierShiftVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface CashierShiftRecordMapper extends BaseMapper<CashierShiftRecordEntity> {

    Page<CashierShiftVO> selectShiftPage(Page<CashierShiftVO> page,
                                         @Param("query") CashierShiftQuery query,
                                         @Param("orgId") Long orgId,
                                         @Param("accessScope") String accessScope,
                                         @Param("clinicIds") List<Long> clinicIds,
                                         @Param("staffId") Long staffId);

    CashierShiftVO selectShiftDetailById(@Param("id") Long id,
                                         @Param("orgId") Long orgId,
                                         @Param("accessScope") String accessScope,
                                         @Param("clinicIds") List<Long> clinicIds,
                                         @Param("staffId") Long staffId);

    @Select("""
            SELECT COUNT(1)
            FROM staff
            WHERE id = #{cashierId}
              AND org_id = #{orgId}
              AND status = 1
              AND is_deleted = 0
            """)
    Long countValidCashier(@Param("orgId") Long orgId, @Param("cashierId") Long cashierId);

    @Select("""
            SELECT COALESCE(SUM(CASE WHEN payment_method = 'CASH' THEN amount ELSE 0 END), 0)
            FROM payment_record
            WHERE org_id = #{orgId}
              AND clinic_id = #{clinicId}
              AND cashier_id = #{cashierId}
              AND payment_status = 'SUCCESS'
              AND paid_at >= #{start}
              AND paid_at <= #{end}
            """)
    BigDecimal sumCashAmount(@Param("orgId") Long orgId, @Param("clinicId") Long clinicId, @Param("cashierId") Long cashierId,
                             @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Select("""
            SELECT COALESCE(SUM(CASE WHEN payment_method = 'WECHAT' THEN amount ELSE 0 END), 0)
            FROM payment_record
            WHERE org_id = #{orgId}
              AND clinic_id = #{clinicId}
              AND cashier_id = #{cashierId}
              AND payment_status = 'SUCCESS'
              AND paid_at >= #{start}
              AND paid_at <= #{end}
            """)
    BigDecimal sumWechatAmount(@Param("orgId") Long orgId, @Param("clinicId") Long clinicId, @Param("cashierId") Long cashierId,
                               @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Select("""
            SELECT COALESCE(SUM(CASE WHEN payment_method = 'ALIPAY' THEN amount ELSE 0 END), 0)
            FROM payment_record
            WHERE org_id = #{orgId}
              AND clinic_id = #{clinicId}
              AND cashier_id = #{cashierId}
              AND payment_status = 'SUCCESS'
              AND paid_at >= #{start}
              AND paid_at <= #{end}
            """)
    BigDecimal sumAlipayAmount(@Param("orgId") Long orgId, @Param("clinicId") Long clinicId, @Param("cashierId") Long cashierId,
                               @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Select("""
            SELECT COALESCE(SUM(CASE WHEN payment_method = 'CARD' THEN amount ELSE 0 END), 0)
            FROM payment_record
            WHERE org_id = #{orgId}
              AND clinic_id = #{clinicId}
              AND cashier_id = #{cashierId}
              AND payment_status = 'SUCCESS'
              AND paid_at >= #{start}
              AND paid_at <= #{end}
            """)
    BigDecimal sumCardAmount(@Param("orgId") Long orgId, @Param("clinicId") Long clinicId, @Param("cashierId") Long cashierId,
                             @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Select("""
            SELECT COALESCE(SUM(CASE WHEN payment_method = 'BALANCE' THEN amount ELSE 0 END), 0)
            FROM payment_record
            WHERE org_id = #{orgId}
              AND clinic_id = #{clinicId}
              AND cashier_id = #{cashierId}
              AND payment_status = 'SUCCESS'
              AND paid_at >= #{start}
              AND paid_at <= #{end}
            """)
    BigDecimal sumBalanceAmount(@Param("orgId") Long orgId, @Param("clinicId") Long clinicId, @Param("cashierId") Long cashierId,
                                @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Select("""
            SELECT COALESCE(SUM(refund_amount), 0)
            FROM refund_record
            WHERE org_id = #{orgId}
              AND clinic_id = #{clinicId}
              AND operator_id = #{cashierId}
              AND refund_status = 'SUCCESS'
              AND refunded_at >= #{start}
              AND refunded_at <= #{end}
            """)
    BigDecimal sumRefundAmount(@Param("orgId") Long orgId, @Param("clinicId") Long clinicId, @Param("cashierId") Long cashierId,
                               @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}
