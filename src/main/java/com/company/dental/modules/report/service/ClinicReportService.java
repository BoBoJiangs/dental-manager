package com.company.dental.modules.report.service;

import com.company.dental.modules.report.query.ClinicOverviewQuery;
import com.company.dental.modules.report.query.DoctorPerformanceQuery;
import com.company.dental.modules.report.vo.ClinicOverviewVO;
import com.company.dental.modules.report.vo.DoctorPerformanceVO;

import java.util.List;

public interface ClinicReportService {

    ClinicOverviewVO getClinicOverview(ClinicOverviewQuery query);

    List<DoctorPerformanceVO> listDoctorPerformance(DoctorPerformanceQuery query);
}
