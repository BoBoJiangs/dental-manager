package com.company.dental.modules.emr.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.company.dental.modules.emr.entity.TreatmentItemEntity;
import com.company.dental.modules.emr.vo.TreatmentItemVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface TreatmentItemMapper extends BaseMapper<TreatmentItemEntity> {

    @Select("""
            SELECT id,
                   treatment_plan_id AS treatmentPlanId,
                   medical_record_id AS medicalRecordId,
                   item_code AS itemCode,
                   item_name AS itemName,
                   item_category AS itemCategory,
                   tooth_position AS toothPosition,
                   unit_price AS unitPrice,
                   quantity,
                   discount_amount AS discountAmount,
                   receivable_amount AS receivableAmount,
                   executed_flag AS executedFlag,
                   executed_at AS executedAt,
                   item_status AS itemStatus,
                   sort_no AS sortNo,
                   remark
            FROM treatment_item
            WHERE treatment_plan_id = #{treatmentPlanId}
            ORDER BY sort_no ASC, id ASC
            """)
    List<TreatmentItemVO> selectByTreatmentPlanId(@Param("treatmentPlanId") Long treatmentPlanId);
}
