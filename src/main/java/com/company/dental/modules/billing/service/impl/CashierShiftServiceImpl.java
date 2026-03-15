package com.company.dental.modules.billing.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.company.dental.common.api.PageResult;
import com.company.dental.common.enums.ErrorCode;
import com.company.dental.common.exception.BusinessException;
import com.company.dental.common.util.BizNoGenerator;
import com.company.dental.framework.security.AuthContext;
import com.company.dental.framework.security.DataScopeHelper;
import com.company.dental.framework.security.LoginUser;
import com.company.dental.modules.billing.dto.CashierShiftCloseRequest;
import com.company.dental.modules.billing.dto.CashierShiftOpenRequest;
import com.company.dental.modules.billing.entity.CashierShiftRecordEntity;
import com.company.dental.modules.billing.mapper.CashierShiftRecordMapper;
import com.company.dental.modules.billing.query.CashierShiftQuery;
import com.company.dental.modules.billing.service.CashierShiftService;
import com.company.dental.modules.billing.vo.CashierShiftVO;
import com.company.dental.modules.org.mapper.ClinicMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class CashierShiftServiceImpl implements CashierShiftService {

    private final CashierShiftRecordMapper cashierShiftRecordMapper;
    private final ClinicMapper clinicMapper;
    private final BizNoGenerator bizNoGenerator;
    private final DataScopeHelper dataScopeHelper;

    public CashierShiftServiceImpl(CashierShiftRecordMapper cashierShiftRecordMapper,
                                   ClinicMapper clinicMapper,
                                   BizNoGenerator bizNoGenerator,
                                   DataScopeHelper dataScopeHelper) {
        this.cashierShiftRecordMapper = cashierShiftRecordMapper;
        this.clinicMapper = clinicMapper;
        this.bizNoGenerator = bizNoGenerator;
        this.dataScopeHelper = dataScopeHelper;
    }

    @Override
    public PageResult<CashierShiftVO> pageShifts(CashierShiftQuery query) {
        LoginUser loginUser = currentLoginUser();
        if (query.getClinicId() != null) {
            dataScopeHelper.assertClinicAccess(loginUser, query.getClinicId());
        }
        Page<CashierShiftVO> page = cashierShiftRecordMapper.selectShiftPage(
                Page.of(query.getCurrent(), query.getSize()),
                query,
                loginUser.getOrgId(),
                dataScopeHelper.resolve(loginUser).name(),
                dataScopeHelper.resolveClinicIds(loginUser),
                loginUser.getStaffId()
        );
        return PageResult.<CashierShiftVO>builder()
                .records(page.getRecords())
                .total(page.getTotal())
                .current(page.getCurrent())
                .size(page.getSize())
                .build();
    }

    @Override
    public CashierShiftVO getShiftDetail(Long shiftId) {
        return getShiftDetailOrThrow(shiftId, currentLoginUser());
    }

    @Override
    @Transactional
    public CashierShiftVO openShift(CashierShiftOpenRequest request) {
        LoginUser loginUser = currentLoginUser();
        Long orgId = loginUser.getOrgId();
        dataScopeHelper.assertClinicAccess(loginUser, request.getClinicId());
        if (clinicMapper.selectById(request.getClinicId()) == null) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "门诊不存在");
        }
        Long cashierCount = cashierShiftRecordMapper.countValidCashier(orgId, request.getCashierId());
        if (cashierCount == null || cashierCount == 0) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "收银员不存在或已停用");
        }
        LocalDate shiftDate = request.getShiftDate() == null ? LocalDate.now() : request.getShiftDate();
        CashierShiftRecordEntity existingOpenShift = cashierShiftRecordMapper.selectOne(new LambdaQueryWrapper<CashierShiftRecordEntity>()
                .eq(CashierShiftRecordEntity::getOrgId, orgId)
                .eq(CashierShiftRecordEntity::getClinicId, request.getClinicId())
                .eq(CashierShiftRecordEntity::getCashierId, request.getCashierId())
                .eq(CashierShiftRecordEntity::getShiftDate, shiftDate)
                .eq(CashierShiftRecordEntity::getShiftStatus, "OPEN")
                .last("LIMIT 1"));
        if (existingOpenShift != null) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "当前收银员已有未关班记录");
        }

        CashierShiftRecordEntity entity = new CashierShiftRecordEntity();
        entity.setOrgId(orgId);
        entity.setClinicId(request.getClinicId());
        entity.setShiftNo(bizNoGenerator.next("CS"));
        entity.setCashierId(request.getCashierId());
        entity.setShiftDate(shiftDate);
        entity.setStartTime(request.getStartTime() == null ? LocalDateTime.now() : request.getStartTime());
        entity.setCashAmount(BigDecimal.ZERO);
        entity.setWechatAmount(BigDecimal.ZERO);
        entity.setAlipayAmount(BigDecimal.ZERO);
        entity.setCardAmount(BigDecimal.ZERO);
        entity.setBalanceAmount(BigDecimal.ZERO);
        entity.setRefundAmount(BigDecimal.ZERO);
        entity.setNetAmount(BigDecimal.ZERO);
        entity.setShiftStatus("OPEN");
        entity.setRemark(request.getRemark());
        cashierShiftRecordMapper.insert(entity);
        return getShiftDetailOrThrow(entity.getId(), loginUser);
    }

    @Override
    @Transactional
    public CashierShiftVO closeShift(Long shiftId, CashierShiftCloseRequest request) {
        LoginUser loginUser = currentLoginUser();
        CashierShiftRecordEntity entity = cashierShiftRecordMapper.selectOne(new LambdaQueryWrapper<CashierShiftRecordEntity>()
                .eq(CashierShiftRecordEntity::getId, shiftId)
                .eq(CashierShiftRecordEntity::getOrgId, loginUser.getOrgId())
                .last("LIMIT 1"));
        if (entity == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "交班记录不存在");
        }
        dataScopeHelper.assertClinicAccess(loginUser, entity.getClinicId());
        if ("CLOSED".equals(entity.getShiftStatus())) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "交班记录已关闭");
        }

        LocalDateTime endTime = request.getEndTime() == null ? LocalDateTime.now() : request.getEndTime();
        BigDecimal cashAmount = defaultZero(cashierShiftRecordMapper.sumCashAmount(entity.getOrgId(), entity.getClinicId(), entity.getCashierId(), entity.getStartTime(), endTime));
        BigDecimal wechatAmount = defaultZero(cashierShiftRecordMapper.sumWechatAmount(entity.getOrgId(), entity.getClinicId(), entity.getCashierId(), entity.getStartTime(), endTime));
        BigDecimal alipayAmount = defaultZero(cashierShiftRecordMapper.sumAlipayAmount(entity.getOrgId(), entity.getClinicId(), entity.getCashierId(), entity.getStartTime(), endTime));
        BigDecimal cardAmount = defaultZero(cashierShiftRecordMapper.sumCardAmount(entity.getOrgId(), entity.getClinicId(), entity.getCashierId(), entity.getStartTime(), endTime));
        BigDecimal balanceAmount = defaultZero(cashierShiftRecordMapper.sumBalanceAmount(entity.getOrgId(), entity.getClinicId(), entity.getCashierId(), entity.getStartTime(), endTime));
        BigDecimal refundAmount = defaultZero(cashierShiftRecordMapper.sumRefundAmount(entity.getOrgId(), entity.getClinicId(), entity.getCashierId(), entity.getStartTime(), endTime));

        entity.setEndTime(endTime);
        entity.setCashAmount(cashAmount);
        entity.setWechatAmount(wechatAmount);
        entity.setAlipayAmount(alipayAmount);
        entity.setCardAmount(cardAmount);
        entity.setBalanceAmount(balanceAmount);
        entity.setRefundAmount(refundAmount);
        entity.setNetAmount(cashAmount.add(wechatAmount).add(alipayAmount).add(cardAmount).add(balanceAmount).subtract(refundAmount));
        entity.setShiftStatus("CLOSED");
        entity.setClosedBy(loginUser.getStaffId() == null ? loginUser.getUserId() : loginUser.getStaffId());
        entity.setClosedAt(LocalDateTime.now());
        if (request.getRemark() != null && !request.getRemark().isBlank()) {
            entity.setRemark(request.getRemark());
        }
        cashierShiftRecordMapper.updateById(entity);
        return getShiftDetailOrThrow(shiftId, loginUser);
    }

    private CashierShiftVO getShiftDetailOrThrow(Long shiftId, LoginUser loginUser) {
        CashierShiftVO detail = cashierShiftRecordMapper.selectShiftDetailById(
                shiftId,
                loginUser.getOrgId(),
                dataScopeHelper.resolve(loginUser).name(),
                dataScopeHelper.resolveClinicIds(loginUser),
                loginUser.getStaffId()
        );
        if (detail == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "交班记录不存在");
        }
        return detail;
    }

    private LoginUser currentLoginUser() {
        LoginUser loginUser = AuthContext.get();
        Long orgId = loginUser == null ? null : loginUser.getOrgId();
        if (orgId == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }
        return loginUser;
    }

    private BigDecimal defaultZero(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }
}
