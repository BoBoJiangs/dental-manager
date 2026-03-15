package com.company.dental.modules.dentalchart.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.company.dental.modules.dentalchart.entity.DentalChartDetailEntity;
import com.company.dental.modules.dentalchart.vo.DentalChartDetailVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DentalChartDetailMapper extends BaseMapper<DentalChartDetailEntity> {

    @Select("""
            SELECT id,
                   tooth_no,
                   tooth_surface,
                   tooth_status,
                   diagnosis_flag,
                   treatment_flag,
                   treatment_item_id,
                   notes
            FROM dental_chart_detail
            WHERE dental_chart_id = #{dentalChartId}
            ORDER BY tooth_no, id
            """)
    List<DentalChartDetailVO> selectByDentalChartId(@Param("dentalChartId") Long dentalChartId);
}
