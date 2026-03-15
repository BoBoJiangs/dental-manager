package com.company.dental.modules.emr.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.company.dental.modules.emr.entity.TreatmentPlanEntity;
import com.company.dental.modules.emr.query.TreatmentPlanPageQuery;
import com.company.dental.modules.emr.vo.TreatmentPlanDetailVO;
import com.company.dental.modules.emr.vo.TreatmentPlanPageItemVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface TreatmentPlanMapper extends BaseMapper<TreatmentPlanEntity> {

    Page<TreatmentPlanPageItemVO> selectTreatmentPlanPage(Page<TreatmentPlanPageItemVO> page,
                                                          @Param("query") TreatmentPlanPageQuery query,
                                                          @Param("orgId") Long orgId,
                                                          @Param("accessScope") String accessScope,
                                                          @Param("clinicIds") List<Long> clinicIds,
                                                          @Param("staffId") Long staffId);

    TreatmentPlanDetailVO selectTreatmentPlanDetailById(@Param("id") Long id,
                                                        @Param("orgId") Long orgId,
                                                        @Param("accessScope") String accessScope,
                                                        @Param("clinicIds") List<Long> clinicIds,
                                                        @Param("staffId") Long staffId);
}
