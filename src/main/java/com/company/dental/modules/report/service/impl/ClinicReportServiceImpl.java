package com.company.dental.modules.report.service.impl;

import com.company.dental.common.enums.ErrorCode;
import com.company.dental.common.exception.BusinessException;
import com.company.dental.framework.security.AuthContext;
import com.company.dental.framework.security.DataScopeHelper;
import com.company.dental.framework.security.DataScopeType;
import com.company.dental.framework.security.LoginUser;
import com.company.dental.modules.report.mapper.ClinicReportMapper;
import com.company.dental.modules.report.query.ClinicOverviewQuery;
import com.company.dental.modules.report.query.DoctorPerformanceQuery;
import com.company.dental.modules.report.service.ClinicReportService;
import com.company.dental.modules.report.vo.ClinicOverviewVO;
import com.company.dental.modules.report.vo.DoctorPerformanceVO;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ClinicReportServiceImpl implements ClinicReportService {

    private final ClinicReportMapper clinicReportMapper;
    private final DataScopeHelper dataScopeHelper;

    public ClinicReportServiceImpl(ClinicReportMapper clinicReportMapper, DataScopeHelper dataScopeHelper) {
        this.clinicReportMapper = clinicReportMapper;
        this.dataScopeHelper = dataScopeHelper;
    }

    @Override
    public ClinicOverviewVO getClinicOverview(ClinicOverviewQuery query) {
        LoginUser loginUser = currentLoginUser();
        Long orgId = loginUser.getOrgId();
        if (query.getStatDate() == null) {
            query.setStatDate(LocalDate.now());
        }
        if (dataScopeHelper.resolve(loginUser) != DataScopeType.ALL) {
            List<Long> clinicIds = dataScopeHelper.resolveClinicIds(loginUser);
            if (clinicIds.isEmpty()) {
                throw new BusinessException(ErrorCode.FORBIDDEN, "无权访问报表数据");
            }
            if (query.getClinicId() == null) {
                query.setClinicId(clinicIds.get(0));
            } else if (!clinicIds.contains(query.getClinicId())) {
                throw new BusinessException(ErrorCode.FORBIDDEN, "无权访问该门诊报表");
            }
        }
        ClinicOverviewVO overview = clinicReportMapper.selectClinicOverview(query, orgId);
        if (overview == null) {
            ClinicOverviewVO empty = new ClinicOverviewVO();
            empty.setStatDate(query.getStatDate());
            empty.setClinicId(query.getClinicId());
            return empty;
        }
        return overview;
    }

    @Override
    public List<DoctorPerformanceVO> listDoctorPerformance(DoctorPerformanceQuery query) {
        LoginUser loginUser = currentLoginUser();
        if (query.getStatDate() == null) {
            query.setStatDate(LocalDate.now());
        }
        List<Long> clinicIds = null;
        if (dataScopeHelper.resolve(loginUser) != DataScopeType.ALL) {
            clinicIds = dataScopeHelper.resolveClinicIds(loginUser);
            if (clinicIds.isEmpty()) {
                throw new BusinessException(ErrorCode.FORBIDDEN, "无权访问医生业绩数据");
            }
            if (query.getClinicId() != null && !clinicIds.contains(query.getClinicId())) {
                throw new BusinessException(ErrorCode.FORBIDDEN, "无权访问该门诊医生业绩");
            }
        }
        return clinicReportMapper.selectDoctorPerformance(query, loginUser.getOrgId(), clinicIds);
    }

    private Long currentOrgId() {
        return currentLoginUser().getOrgId();
    }

    private LoginUser currentLoginUser() {
        LoginUser loginUser = AuthContext.get();
        Long orgId = loginUser == null ? null : loginUser.getOrgId();
        if (orgId == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }
        return loginUser;
    }
}
