package com.company.dental.modules.billing.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.company.dental.modules.billing.entity.PaymentRecordEntity;
import com.company.dental.modules.billing.vo.PaymentRecordVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface PaymentRecordMapper extends BaseMapper<PaymentRecordEntity> {

    @Select("""
            SELECT id,
                   payment_no AS paymentNo,
                   payment_method AS paymentMethod,
                   amount,
                   transaction_no AS transactionNo,
                   payer_name AS payerName,
                   payment_status AS paymentStatus,
                   paid_at AS paidAt,
                   cashier_id AS cashierId,
                   remark
            FROM payment_record
            WHERE charge_order_id = #{chargeOrderId}
            ORDER BY id DESC
            """)
    List<PaymentRecordVO> selectByChargeOrderId(@Param("chargeOrderId") Long chargeOrderId);
}
