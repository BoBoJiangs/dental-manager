package com.company.dental.modules.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.company.dental.common.enums.ErrorCode;
import com.company.dental.common.exception.BusinessException;
import com.company.dental.framework.security.AuthContext;
import com.company.dental.framework.security.LoginUser;
import com.company.dental.modules.appointment.entity.AppointmentEntity;
import com.company.dental.modules.appointment.entity.QueueRecordEntity;
import com.company.dental.modules.appointment.mapper.AppointmentMapper;
import com.company.dental.modules.appointment.mapper.QueueRecordMapper;
import com.company.dental.modules.billing.entity.ChargeOrderEntity;
import com.company.dental.modules.billing.mapper.ChargeOrderMapper;
import com.company.dental.modules.sms.entity.SmsTaskEntity;
import com.company.dental.modules.sms.mapper.SmsTaskMapper;
import com.company.dental.modules.system.service.SystemOverviewService;
import com.company.dental.modules.system.vo.WorkbenchOverviewVO;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class SystemOverviewServiceImpl implements SystemOverviewService {

    private final AppointmentMapper appointmentMapper;
    private final QueueRecordMapper queueRecordMapper;
    private final ChargeOrderMapper chargeOrderMapper;
    private final SmsTaskMapper smsTaskMapper;

    public SystemOverviewServiceImpl(AppointmentMapper appointmentMapper,
                                     QueueRecordMapper queueRecordMapper,
                                     ChargeOrderMapper chargeOrderMapper,
                                     SmsTaskMapper smsTaskMapper) {
        this.appointmentMapper = appointmentMapper;
        this.queueRecordMapper = queueRecordMapper;
        this.chargeOrderMapper = chargeOrderMapper;
        this.smsTaskMapper = smsTaskMapper;
    }

    @Override
    public WorkbenchOverviewVO getWorkbenchOverview() {
        Long orgId = currentOrgId();
        LocalDate today = LocalDate.now();
        WorkbenchOverviewVO overview = new WorkbenchOverviewVO();
        overview.setTodayAppointmentCount(countAppointments(orgId, today, null));
        overview.setTodayArrivedCount(countAppointments(orgId, today, "ARRIVED"));
        overview.setWaitingPatientCount(countWaitingPatients(orgId, today));
        overview.setTodayChargeOrderCount(countChargeOrders(orgId, today));
        overview.setTodayReceivableAmount(sumChargeField(orgId, today, "receivable_amount"));
        overview.setTodayPaidAmount(sumChargeField(orgId, today, "paid_amount"));
        overview.setTodaySmsTaskCount(countSmsTasks(orgId, today, null));
        overview.setPendingSmsTaskCount(countSmsTasks(orgId, today, "PENDING"));
        return overview;
    }

    private int countAppointments(Long orgId, LocalDate date, String status) {
        Long count = appointmentMapper.selectCount(new LambdaQueryWrapper<AppointmentEntity>()
                .eq(AppointmentEntity::getOrgId, orgId)
                .eq(AppointmentEntity::getAppointmentDate, date)
                .eq(status != null, AppointmentEntity::getStatus, status));
        return count == null ? 0 : count.intValue();
    }

    private int countWaitingPatients(Long orgId, LocalDate date) {
        Long count = queueRecordMapper.selectCount(new LambdaQueryWrapper<QueueRecordEntity>()
                .eq(QueueRecordEntity::getOrgId, orgId)
                .eq(QueueRecordEntity::getQueueStatus, "WAITING")
                .ge(QueueRecordEntity::getCreatedAt, date.atStartOfDay())
                .lt(QueueRecordEntity::getCreatedAt, date.plusDays(1).atStartOfDay()));
        return count == null ? 0 : count.intValue();
    }

    private int countChargeOrders(Long orgId, LocalDate date) {
        Long count = chargeOrderMapper.selectCount(new LambdaQueryWrapper<ChargeOrderEntity>()
                .eq(ChargeOrderEntity::getOrgId, orgId)
                .ge(ChargeOrderEntity::getChargeTime, date.atStartOfDay())
                .lt(ChargeOrderEntity::getChargeTime, date.plusDays(1).atStartOfDay()));
        return count == null ? 0 : count.intValue();
    }

    private BigDecimal sumChargeField(Long orgId, LocalDate date, String field) {
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.plusDays(1).atStartOfDay();
        return switch (field) {
            case "receivable_amount" -> defaultZero(chargeOrderMapper.sumReceivableAmount(orgId, start, end));
            case "paid_amount" -> defaultZero(chargeOrderMapper.sumPaidAmount(orgId, start, end));
            default -> BigDecimal.ZERO;
        };
    }

    private int countSmsTasks(Long orgId, LocalDate date, String status) {
        Long count = smsTaskMapper.selectCount(new LambdaQueryWrapper<SmsTaskEntity>()
                .eq(SmsTaskEntity::getOrgId, orgId)
                .eq(status != null, SmsTaskEntity::getTaskStatus, status)
                .ge(SmsTaskEntity::getCreatedAt, date.atStartOfDay())
                .lt(SmsTaskEntity::getCreatedAt, date.plusDays(1).atStartOfDay()));
        return count == null ? 0 : count.intValue();
    }

    private Long currentOrgId() {
        LoginUser loginUser = AuthContext.get();
        Long orgId = loginUser == null ? null : loginUser.getOrgId();
        if (orgId == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }
        return orgId;
    }

    private BigDecimal defaultZero(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }
}
