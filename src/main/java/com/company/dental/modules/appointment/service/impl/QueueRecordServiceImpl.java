package com.company.dental.modules.appointment.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.company.dental.common.enums.ErrorCode;
import com.company.dental.common.exception.BusinessException;
import com.company.dental.framework.security.AuthContext;
import com.company.dental.framework.security.DataScopeHelper;
import com.company.dental.framework.security.LoginUser;
import com.company.dental.modules.appointment.dto.QueueStatusUpdateRequest;
import com.company.dental.modules.appointment.entity.QueueRecordEntity;
import com.company.dental.modules.appointment.mapper.QueueRecordMapper;
import com.company.dental.modules.appointment.mapper.QueueRecordQueryMapper;
import com.company.dental.modules.appointment.query.QueueRecordQuery;
import com.company.dental.modules.appointment.service.QueueRecordService;
import com.company.dental.modules.appointment.vo.QueueRecordVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Service
public class QueueRecordServiceImpl implements QueueRecordService {

    private static final Set<String> ALLOWED_STATUSES = Set.of("WAITING", "CALLING", "IN_TREATMENT", "COMPLETED", "SKIPPED");

    private final QueueRecordMapper queueRecordMapper;
    private final QueueRecordQueryMapper queueRecordQueryMapper;
    private final DataScopeHelper dataScopeHelper;

    public QueueRecordServiceImpl(QueueRecordMapper queueRecordMapper,
                                  QueueRecordQueryMapper queueRecordQueryMapper,
                                  DataScopeHelper dataScopeHelper) {
        this.queueRecordMapper = queueRecordMapper;
        this.queueRecordQueryMapper = queueRecordQueryMapper;
        this.dataScopeHelper = dataScopeHelper;
    }

    @Override
    public List<QueueRecordVO> listQueues(QueueRecordQuery query) {
        LoginUser loginUser = currentLoginUser();
        if (query.getClinicId() != null) {
            dataScopeHelper.assertClinicAccess(loginUser, query.getClinicId());
        }
        return queueRecordQueryMapper.selectQueueList(
                query,
                loginUser.getOrgId(),
                dataScopeHelper.resolve(loginUser).name(),
                dataScopeHelper.resolveClinicIds(loginUser),
                loginUser.getStaffId()
        );
    }

    @Override
    @Transactional
    public QueueRecordVO updateQueueStatus(Long queueRecordId, QueueStatusUpdateRequest request) {
        LoginUser loginUser = currentLoginUser();
        QueueRecordVO detail = getQueueDetailOrThrow(queueRecordId, loginUser);
        QueueRecordEntity entity = queueRecordMapper.selectOne(new LambdaQueryWrapper<QueueRecordEntity>()
                .eq(QueueRecordEntity::getId, queueRecordId)
                .eq(QueueRecordEntity::getOrgId, loginUser.getOrgId())
                .last("LIMIT 1"));
        if (entity == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "候诊记录不存在");
        }
        dataScopeHelper.assertClinicAccess(loginUser, entity.getClinicId());
        String normalizedStatus = normalizeStatus(request.getQueueStatus());
        LocalDateTime now = LocalDateTime.now();
        entity.setQueueStatus(normalizedStatus);
        if (request.getRemark() != null && !request.getRemark().isBlank()) {
            entity.setRemark(request.getRemark());
        }
        switch (normalizedStatus) {
            case "CALLING" -> entity.setCallAt(now);
            case "IN_TREATMENT" -> {
                if (entity.getCallAt() == null) {
                    entity.setCallAt(now);
                }
                entity.setStartTreatmentAt(now);
            }
            case "COMPLETED" -> {
                if (entity.getStartTreatmentAt() == null) {
                    entity.setStartTreatmentAt(now);
                }
                entity.setEndTreatmentAt(now);
            }
            case "SKIPPED" -> entity.setEndTreatmentAt(now);
            default -> {
            }
        }
        queueRecordMapper.updateById(entity);
        return getQueueDetailOrThrow(queueRecordId, loginUser);
    }

    private QueueRecordVO getQueueDetailOrThrow(Long queueRecordId, LoginUser loginUser) {
        QueueRecordVO detail = queueRecordQueryMapper.selectQueueDetailById(
                queueRecordId,
                loginUser.getOrgId(),
                dataScopeHelper.resolve(loginUser).name(),
                dataScopeHelper.resolveClinicIds(loginUser),
                loginUser.getStaffId()
        );
        if (detail == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "候诊记录不存在");
        }
        return detail;
    }

    private String normalizeStatus(String status) {
        String normalized = status == null ? null : status.trim().toUpperCase();
        if (!ALLOWED_STATUSES.contains(normalized)) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "候诊状态非法");
        }
        return normalized;
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
