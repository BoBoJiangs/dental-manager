package com.company.dental.modules.billing.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.company.dental.modules.billing.entity.RefundRecordEntity;
import com.company.dental.modules.billing.vo.RefundRecordVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface RefundRecordMapper extends BaseMapper<RefundRecordEntity> {

    @Select("""
            SELECT id,
                   refund_no AS refundNo,
                   payment_record_id AS paymentRecordId,
                   refund_amount AS refundAmount,
                   refund_method AS refundMethod,
                   refund_reason AS refundReason,
                   refund_status AS refundStatus,
                   approved_by AS approvedBy,
                   refunded_at AS refundedAt,
                   operator_id AS operatorId,
                   remark
            FROM refund_record
            WHERE charge_order_id = #{chargeOrderId}
            ORDER BY id DESC
            """)
    List<RefundRecordVO> selectByChargeOrderId(@Param("chargeOrderId") Long chargeOrderId);
}
