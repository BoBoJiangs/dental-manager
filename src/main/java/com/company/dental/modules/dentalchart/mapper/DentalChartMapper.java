package com.company.dental.modules.dentalchart.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.company.dental.modules.dentalchart.entity.DentalChartEntity;
import com.company.dental.modules.dentalchart.vo.DentalChartVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface DentalChartMapper extends BaseMapper<DentalChartEntity> {

    DentalChartVO selectByMedicalRecordId(@Param("medicalRecordId") Long medicalRecordId,
                                          @Param("orgId") Long orgId);
}
