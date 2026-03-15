package com.company.dental.modules.billing.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.company.dental.modules.billing.entity.ChargeItemEntity;
import com.company.dental.modules.billing.vo.ChargeItemVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ChargeItemMapper extends BaseMapper<ChargeItemEntity> {

    @Select("""
            SELECT id,
                   treatment_item_id,
                   item_code,
                   item_name,
                   item_category,
                   tooth_position,
                   unit_price,
                   quantity,
                   total_amount,
                   discount_amount,
                   receivable_amount,
                   doctor_id,
                   item_status,
                   remark
            FROM charge_item
            WHERE charge_order_id = #{chargeOrderId}
            ORDER BY id
            """)
    List<ChargeItemVO> selectByChargeOrderId(@Param("chargeOrderId") Long chargeOrderId);
}
