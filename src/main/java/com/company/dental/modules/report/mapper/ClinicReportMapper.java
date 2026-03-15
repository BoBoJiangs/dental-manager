package com.company.dental.modules.report.mapper;

import com.company.dental.modules.report.query.ClinicOverviewQuery;
import com.company.dental.modules.report.query.DoctorPerformanceQuery;
import com.company.dental.modules.report.vo.ClinicOverviewVO;
import com.company.dental.modules.report.vo.DoctorPerformanceVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ClinicReportMapper {

    ClinicOverviewVO selectClinicOverview(@Param("query") ClinicOverviewQuery query, @Param("orgId") Long orgId);

    List<DoctorPerformanceVO> selectDoctorPerformance(@Param("query") DoctorPerformanceQuery query,
                                                      @Param("orgId") Long orgId,
                                                      @Param("clinicIds") List<Long> clinicIds);
}
