package com.company.dental.modules.appointment.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.company.dental.common.api.PageResult;
import com.company.dental.common.enums.ErrorCode;
import com.company.dental.common.exception.BusinessException;
import com.company.dental.common.util.BizNoGenerator;
import com.company.dental.framework.security.AuthContext;
import com.company.dental.framework.security.DataScopeHelper;
import com.company.dental.framework.security.DataScopeType;
import com.company.dental.framework.security.LoginUser;
import com.company.dental.modules.appointment.dto.AppointmentCancelRequest;
import com.company.dental.modules.appointment.dto.AppointmentCheckInRequest;
import com.company.dental.modules.appointment.dto.AppointmentCreateRequest;
import com.company.dental.modules.appointment.dto.AppointmentRescheduleRequest;
import com.company.dental.modules.appointment.entity.AppointmentEntity;
import com.company.dental.modules.appointment.entity.AppointmentLogEntity;
import com.company.dental.modules.appointment.entity.QueueRecordEntity;
import com.company.dental.modules.appointment.mapper.AppointmentLogMapper;
import com.company.dental.modules.appointment.mapper.AppointmentMapper;
import com.company.dental.modules.appointment.mapper.QueueRecordMapper;
import com.company.dental.modules.appointment.query.AppointmentPageQuery;
import com.company.dental.modules.appointment.service.AppointmentService;
import com.company.dental.modules.appointment.vo.AppointmentDetailVO;
import com.company.dental.modules.appointment.vo.AppointmentPageItemVO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

@Service
public class AppointmentServiceImpl implements AppointmentService {

    private static final String STATUS_CONFIRMED = "CONFIRMED";
    private static final String STATUS_CANCELLED = "CANCELLED";
    private static final String STATUS_ARRIVED = "ARRIVED";
    private static final String QUEUE_STATUS_WAITING = "WAITING";

    private final AppointmentMapper appointmentMapper;
    private final AppointmentLogMapper appointmentLogMapper;
    private final QueueRecordMapper queueRecordMapper;
    private final BizNoGenerator bizNoGenerator;
    private final ObjectMapper objectMapper;
    private final DataScopeHelper dataScopeHelper;

    public AppointmentServiceImpl(AppointmentMapper appointmentMapper,
                                  AppointmentLogMapper appointmentLogMapper,
                                  QueueRecordMapper queueRecordMapper,
                                  BizNoGenerator bizNoGenerator,
                                  ObjectMapper objectMapper,
                                  DataScopeHelper dataScopeHelper) {
        this.appointmentMapper = appointmentMapper;
        this.appointmentLogMapper = appointmentLogMapper;
        this.queueRecordMapper = queueRecordMapper;
        this.bizNoGenerator = bizNoGenerator;
        this.objectMapper = objectMapper;
        this.dataScopeHelper = dataScopeHelper;
    }

    @Override
    public PageResult<AppointmentPageItemVO> pageAppointments(AppointmentPageQuery query) {
        LoginUser loginUser = currentLoginUser();
        Long orgId = loginUser.getOrgId();

        Page<AppointmentPageItemVO> page = appointmentMapper.selectAppointmentPage(
                Page.of(query.getCurrent(), query.getSize()),
                query,
                orgId,
                dataScopeHelper.resolve(loginUser).name(),
                dataScopeHelper.resolveClinicIds(loginUser),
                loginUser.getStaffId()
        );
        return PageResult.<AppointmentPageItemVO>builder()
                .records(page.getRecords())
                .total(page.getTotal())
                .current(page.getCurrent())
                .size(page.getSize())
                .build();
    }

    @Override
    public AppointmentDetailVO getAppointmentDetail(Long appointmentId) {
        LoginUser loginUser = currentLoginUser();
        return getAppointmentDetailOrThrow(appointmentId, loginUser);
    }

    @Override
    @Transactional
    public AppointmentDetailVO createAppointment(AppointmentCreateRequest request) {
        LoginUser loginUser = currentLoginUser();
        Long orgId = loginUser.getOrgId();
        dataScopeHelper.assertClinicAccess(loginUser, request.getClinicId());
        validateAppointmentParticipants(orgId, request.getClinicId(), request.getPatientId(), request.getDoctorId());
        validateAppointmentTime(request.getAppointmentDate(), request.getStartTime(), request.getEndTime());

        AppointmentEntity appointment = new AppointmentEntity();
        appointment.setOrgId(orgId);
        appointment.setClinicId(request.getClinicId());
        appointment.setAppointmentNo(bizNoGenerator.next("A"));
        appointment.setPatientId(request.getPatientId());
        appointment.setDoctorId(request.getDoctorId());
        appointment.setRoomId(request.getRoomId());
        appointment.setSourceType(request.getSourceType());
        appointment.setAppointmentType(request.getAppointmentType());
        appointment.setAppointmentDate(request.getAppointmentDate());
        appointment.setStartTime(request.getStartTime());
        appointment.setEndTime(request.getEndTime());
        appointment.setTreatmentItemName(request.getTreatmentItemName());
        appointment.setStatus(STATUS_CONFIRMED);
        appointment.setNoShowFlag(0);
        appointment.setRemark(request.getRemark());
        appointmentMapper.insert(appointment);

        AppointmentDetailVO detail = getAppointmentDetailOrThrow(appointment.getId(), loginUser);
        writeAppointmentLog(appointment.getId(), "CREATE", null, detail, currentUserId(), request.getRemark());
        return detail;
    }

    @Override
    @Transactional
    public AppointmentDetailVO rescheduleAppointment(Long appointmentId, AppointmentRescheduleRequest request) {
        LoginUser loginUser = currentLoginUser();
        Long orgId = loginUser.getOrgId();
        dataScopeHelper.assertClinicAccess(loginUser, request.getClinicId());
        validateAppointmentParticipants(orgId, request.getClinicId(), null, request.getDoctorId());
        validateAppointmentTime(request.getAppointmentDate(), request.getStartTime(), request.getEndTime());

        AppointmentEntity appointment = getAppointmentEntityOrThrow(appointmentId, orgId);
        dataScopeHelper.assertClinicAccess(loginUser, appointment.getClinicId());
        ensureStatusAllowed(appointment.getStatus(), Set.of(STATUS_CONFIRMED), "当前预约状态不允许改约");
        AppointmentDetailVO beforeDetail = getAppointmentDetailOrThrow(appointmentId, loginUser);

        appointment.setClinicId(request.getClinicId());
        appointment.setDoctorId(request.getDoctorId());
        appointment.setRoomId(request.getRoomId());
        appointment.setAppointmentDate(request.getAppointmentDate());
        appointment.setStartTime(request.getStartTime());
        appointment.setEndTime(request.getEndTime());
        appointment.setTreatmentItemName(request.getTreatmentItemName());
        appointment.setStatus(STATUS_CONFIRMED);
        appointment.setCancelReason(null);
        appointment.setRemark(request.getRemark());
        appointmentMapper.updateById(appointment);

        AppointmentDetailVO afterDetail = getAppointmentDetailOrThrow(appointmentId, loginUser);
        writeAppointmentLog(appointmentId, "RESCHEDULE", beforeDetail, afterDetail, currentUserId(), request.getRemark());
        return afterDetail;
    }

    @Override
    @Transactional
    public AppointmentDetailVO cancelAppointment(Long appointmentId, AppointmentCancelRequest request) {
        LoginUser loginUser = currentLoginUser();
        Long orgId = loginUser.getOrgId();
        AppointmentEntity appointment = getAppointmentEntityOrThrow(appointmentId, orgId);
        dataScopeHelper.assertClinicAccess(loginUser, appointment.getClinicId());
        ensureStatusAllowed(appointment.getStatus(), Set.of(STATUS_CONFIRMED), "当前预约状态不允许取消");
        AppointmentDetailVO beforeDetail = getAppointmentDetailOrThrow(appointmentId, loginUser);

        appointment.setStatus(STATUS_CANCELLED);
        appointment.setCancelReason(request.getCancelReason());
        appointment.setRemark(request.getRemark());
        appointmentMapper.updateById(appointment);

        AppointmentDetailVO afterDetail = getAppointmentDetailOrThrow(appointmentId, loginUser);
        writeAppointmentLog(appointmentId, "CANCEL", beforeDetail, afterDetail, currentUserId(), request.getRemark());
        return afterDetail;
    }

    @Override
    @Transactional
    public AppointmentDetailVO checkInAppointment(Long appointmentId, AppointmentCheckInRequest request) {
        LoginUser loginUser = currentLoginUser();
        Long orgId = loginUser.getOrgId();
        AppointmentEntity appointment = getAppointmentEntityOrThrow(appointmentId, orgId);
        dataScopeHelper.assertClinicAccess(loginUser, appointment.getClinicId());
        ensureStatusAllowed(appointment.getStatus(), Set.of(STATUS_CONFIRMED), "当前预约状态不允许签到");
        AppointmentDetailVO beforeDetail = getAppointmentDetailOrThrow(appointmentId, loginUser);

        LocalDateTime now = LocalDateTime.now();
        appointment.setStatus(STATUS_ARRIVED);
        appointment.setArrivedAt(now);
        appointment.setRemark(request.getRemark());
        appointmentMapper.updateById(appointment);

        QueueRecordEntity queueRecord = queueRecordMapper.selectOne(new LambdaQueryWrapper<QueueRecordEntity>()
                .eq(QueueRecordEntity::getAppointmentId, appointmentId)
                .last("LIMIT 1"));
        if (queueRecord == null) {
            queueRecord = new QueueRecordEntity();
            queueRecord.setOrgId(orgId);
            queueRecord.setClinicId(appointment.getClinicId());
            queueRecord.setAppointmentId(appointmentId);
            queueRecord.setPatientId(appointment.getPatientId());
            queueRecord.setDoctorId(appointment.getDoctorId());
            queueRecord.setQueueNo(bizNoGenerator.next("Q"));
            queueRecord.setQueueStatus(QUEUE_STATUS_WAITING);
            queueRecord.setCheckinAt(now);
            queueRecord.setRemark(request.getRemark());
            queueRecordMapper.insert(queueRecord);
        } else {
            queueRecord.setClinicId(appointment.getClinicId());
            queueRecord.setDoctorId(appointment.getDoctorId());
            queueRecord.setQueueStatus(QUEUE_STATUS_WAITING);
            queueRecord.setCheckinAt(now);
            queueRecord.setRemark(request.getRemark());
            queueRecordMapper.updateById(queueRecord);
        }

        AppointmentDetailVO afterDetail = getAppointmentDetailOrThrow(appointmentId, loginUser);
        writeAppointmentLog(appointmentId, "CHECKIN", beforeDetail, afterDetail, currentUserId(), request.getRemark());
        return afterDetail;
    }

    private void validateAppointmentParticipants(Long orgId, Long clinicId, Long patientId, Long doctorId) {
        Long clinicCount = appointmentMapper.countValidClinic(orgId, clinicId);
        if (clinicCount == null || clinicCount == 0) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "门诊不存在或已停用");
        }
        if (patientId != null) {
            Long patientCount = appointmentMapper.countValidPatient(orgId, patientId);
            if (patientCount == null || patientCount == 0) {
                throw new BusinessException(ErrorCode.BAD_REQUEST, "患者不存在");
            }
        }
        Long doctorCount = appointmentMapper.countValidDoctor(orgId, doctorId);
        if (doctorCount == null || doctorCount == 0) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "医生不存在或已停用");
        }
    }

    private void validateAppointmentTime(java.time.LocalDate appointmentDate, LocalDateTime startTime, LocalDateTime endTime) {
        if (!appointmentDate.equals(startTime.toLocalDate()) || !appointmentDate.equals(endTime.toLocalDate())) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "预约日期必须与开始结束时间同一天");
        }
        if (!endTime.isAfter(startTime)) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "结束时间必须晚于开始时间");
        }
    }

    private void ensureStatusAllowed(String currentStatus, Set<String> allowedStatuses, String message) {
        if (!allowedStatuses.contains(currentStatus)) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, message);
        }
    }

    private AppointmentEntity getAppointmentEntityOrThrow(Long appointmentId, Long orgId) {
        AppointmentEntity appointment = appointmentMapper.selectOne(new LambdaQueryWrapper<AppointmentEntity>()
                .eq(AppointmentEntity::getId, appointmentId)
                .eq(AppointmentEntity::getOrgId, orgId)
                .last("LIMIT 1"));
        if (appointment == null) {
            throw new BusinessException(ErrorCode.APPOINTMENT_NOT_FOUND);
        }
        return appointment;
    }

    private AppointmentDetailVO getAppointmentDetailOrThrow(Long appointmentId, LoginUser loginUser) {
        AppointmentDetailVO detail = appointmentMapper.selectAppointmentDetailById(
                appointmentId,
                loginUser.getOrgId(),
                dataScopeHelper.resolve(loginUser).name(),
                dataScopeHelper.resolveClinicIds(loginUser),
                loginUser.getStaffId()
        );
        if (detail == null) {
            throw new BusinessException(ErrorCode.APPOINTMENT_NOT_FOUND);
        }
        return detail;
    }

    private void writeAppointmentLog(Long appointmentId,
                                     String actionType,
                                     AppointmentDetailVO beforeDetail,
                                     AppointmentDetailVO afterDetail,
                                     Long actionBy,
                                     String remark) {
        AppointmentLogEntity log = new AppointmentLogEntity();
        log.setAppointmentId(appointmentId);
        log.setActionType(actionType);
        log.setBeforeData(toJson(beforeDetail));
        log.setAfterData(toJson(afterDetail));
        log.setActionBy(actionBy);
        log.setActionAt(LocalDateTime.now());
        log.setRemark(remark);
        appointmentLogMapper.insert(log);
    }

    private String toJson(AppointmentDetailVO detail) {
        if (detail == null) {
            return null;
        }
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("id", detail.getId());
        payload.put("appointmentNo", detail.getAppointmentNo());
        payload.put("clinicId", detail.getClinicId());
        payload.put("patientId", detail.getPatientId());
        payload.put("doctorId", detail.getDoctorId());
        payload.put("appointmentDate", detail.getAppointmentDate());
        payload.put("startTime", detail.getStartTime());
        payload.put("endTime", detail.getEndTime());
        payload.put("status", detail.getStatus());
        payload.put("queueNo", detail.getQueueNo());
        payload.put("queueStatus", detail.getQueueStatus());
        try {
            return objectMapper.writeValueAsString(payload);
        } catch (JsonProcessingException ex) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "预约日志序列化失败");
        }
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

    private Long currentUserId() {
        LoginUser loginUser = AuthContext.get();
        return loginUser == null ? null : loginUser.getUserId();
    }
}
