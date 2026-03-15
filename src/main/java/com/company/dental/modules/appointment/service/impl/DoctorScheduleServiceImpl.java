package com.company.dental.modules.appointment.service.impl;

import com.company.dental.common.enums.ErrorCode;
import com.company.dental.common.exception.BusinessException;
import com.company.dental.framework.security.AuthContext;
import com.company.dental.framework.security.DataScopeHelper;
import com.company.dental.framework.security.LoginUser;
import com.company.dental.modules.appointment.dto.DoctorScheduleCreateRequest;
import com.company.dental.modules.appointment.entity.DoctorScheduleEntity;
import com.company.dental.modules.appointment.mapper.DoctorScheduleMapper;
import com.company.dental.modules.appointment.query.DoctorScheduleQuery;
import com.company.dental.modules.appointment.service.DoctorScheduleService;
import com.company.dental.modules.appointment.vo.DoctorScheduleVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DoctorScheduleServiceImpl implements DoctorScheduleService {

    private final DoctorScheduleMapper doctorScheduleMapper;
    private final DataScopeHelper dataScopeHelper;

    public DoctorScheduleServiceImpl(DoctorScheduleMapper doctorScheduleMapper, DataScopeHelper dataScopeHelper) {
        this.doctorScheduleMapper = doctorScheduleMapper;
        this.dataScopeHelper = dataScopeHelper;
    }

    @Override
    public List<DoctorScheduleVO> listSchedules(DoctorScheduleQuery query) {
        LoginUser loginUser = currentLoginUser();
        if (query.getClinicId() != null) {
            dataScopeHelper.assertClinicAccess(loginUser, query.getClinicId());
        }
        return doctorScheduleMapper.selectScheduleList(
                query,
                loginUser.getOrgId(),
                dataScopeHelper.resolve(loginUser).name(),
                dataScopeHelper.resolveClinicIds(loginUser),
                loginUser.getStaffId()
        );
    }

    @Override
    @Transactional
    public DoctorScheduleVO createSchedule(DoctorScheduleCreateRequest request) {
        LoginUser loginUser = currentLoginUser();
        Long orgId = loginUser.getOrgId();
        dataScopeHelper.assertClinicAccess(loginUser, request.getClinicId());
        validateParticipants(orgId, request);
        if (!request.getEndTime().isAfter(request.getStartTime())) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "结束时间必须晚于开始时间");
        }

        DoctorScheduleEntity entity = new DoctorScheduleEntity();
        entity.setOrgId(orgId);
        entity.setClinicId(request.getClinicId());
        entity.setDoctorId(request.getDoctorId());
        entity.setRoomId(request.getRoomId());
        entity.setScheduleDate(request.getScheduleDate());
        entity.setStartTime(request.getStartTime());
        entity.setEndTime(request.getEndTime());
        entity.setScheduleType(request.getScheduleType());
        entity.setMaxAppointmentCount(request.getMaxAppointmentCount() == null ? 0 : request.getMaxAppointmentCount());
        entity.setStatus(request.getStatus() == null ? 1 : request.getStatus());
        entity.setRemark(request.getRemark());
        doctorScheduleMapper.insert(entity);

        DoctorScheduleQuery query = new DoctorScheduleQuery();
        query.setScheduleDate(request.getScheduleDate());
        query.setClinicId(request.getClinicId());
        query.setDoctorId(request.getDoctorId());
        return listSchedules(query).stream()
                .filter(item -> entity.getId().equals(item.getId()))
                .findFirst()
                .orElseThrow(() -> new BusinessException(ErrorCode.SYSTEM_ERROR, "排班创建失败"));
    }

    private void validateParticipants(Long orgId, DoctorScheduleCreateRequest request) {
        Long clinicCount = doctorScheduleMapper.countValidClinic(orgId, request.getClinicId());
        if (clinicCount == null || clinicCount == 0) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "门诊不存在或已停用");
        }
        Long doctorCount = doctorScheduleMapper.countValidDoctor(orgId, request.getDoctorId());
        if (doctorCount == null || doctorCount == 0) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "医生不存在或已停用");
        }
        if (request.getRoomId() != null) {
            Long roomCount = doctorScheduleMapper.countValidRoom(orgId, request.getRoomId());
            if (roomCount == null || roomCount == 0) {
                throw new BusinessException(ErrorCode.BAD_REQUEST, "诊室不存在或已停用");
            }
        }
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
