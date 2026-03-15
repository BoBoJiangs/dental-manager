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
import com.company.dental.modules.billing.dto.ChargeItemCreateRequest;
import com.company.dental.modules.billing.dto.ChargeOrderCreateRequest;
import com.company.dental.modules.billing.dto.PaymentCreateRequest;
import com.company.dental.modules.billing.dto.RefundCreateRequest;
import com.company.dental.modules.billing.entity.ChargeItemEntity;
import com.company.dental.modules.billing.entity.ChargeOrderEntity;
import com.company.dental.modules.billing.entity.PaymentRecordEntity;
import com.company.dental.modules.billing.entity.ReceivableRecordEntity;
import com.company.dental.modules.billing.entity.RefundRecordEntity;
import com.company.dental.modules.billing.mapper.ChargeItemMapper;
import com.company.dental.modules.billing.mapper.ChargeOrderMapper;
import com.company.dental.modules.billing.mapper.PaymentRecordMapper;
import com.company.dental.modules.billing.mapper.ReceivableRecordMapper;
import com.company.dental.modules.billing.mapper.RefundRecordMapper;
import com.company.dental.modules.billing.query.ChargeOrderPageQuery;
import com.company.dental.modules.billing.service.ChargeOrderService;
import com.company.dental.modules.billing.vo.ChargeItemVO;
import com.company.dental.modules.billing.vo.ChargeOrderDetailVO;
import com.company.dental.modules.billing.vo.ChargeOrderPageItemVO;
import com.company.dental.modules.billing.vo.PaymentRecordVO;
import com.company.dental.modules.billing.vo.RefundRecordVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Service
public class ChargeOrderServiceImpl implements ChargeOrderService {

    private static final String STATUS_DRAFT = "DRAFT";
    private static final String STATUS_PART_PAID = "PART_PAID";
    private static final String STATUS_PAID = "PAID";
    private static final String STATUS_REFUNDED = "REFUNDED";

    private final ChargeOrderMapper chargeOrderMapper;
    private final ChargeItemMapper chargeItemMapper;
    private final PaymentRecordMapper paymentRecordMapper;
    private final RefundRecordMapper refundRecordMapper;
    private final ReceivableRecordMapper receivableRecordMapper;
    private final DataScopeHelper dataScopeHelper;
    private final BizNoGenerator bizNoGenerator;

    public ChargeOrderServiceImpl(ChargeOrderMapper chargeOrderMapper,
                                  ChargeItemMapper chargeItemMapper,
                                  PaymentRecordMapper paymentRecordMapper,
                                  RefundRecordMapper refundRecordMapper,
                                  ReceivableRecordMapper receivableRecordMapper,
                                  DataScopeHelper dataScopeHelper,
                                  BizNoGenerator bizNoGenerator) {
        this.chargeOrderMapper = chargeOrderMapper;
        this.chargeItemMapper = chargeItemMapper;
        this.paymentRecordMapper = paymentRecordMapper;
        this.refundRecordMapper = refundRecordMapper;
        this.receivableRecordMapper = receivableRecordMapper;
        this.dataScopeHelper = dataScopeHelper;
        this.bizNoGenerator = bizNoGenerator;
    }

    @Override
    public PageResult<ChargeOrderPageItemVO> pageChargeOrders(ChargeOrderPageQuery query) {
        LoginUser loginUser = currentLoginUser();
        Long orgId = loginUser.getOrgId();
        Page<ChargeOrderPageItemVO> page = chargeOrderMapper.selectChargeOrderPage(
                Page.of(query.getCurrent(), query.getSize()),
                query,
                orgId,
                dataScopeHelper.resolve(loginUser).name(),
                dataScopeHelper.resolveClinicIds(loginUser),
                loginUser.getStaffId()
        );
        return PageResult.<ChargeOrderPageItemVO>builder()
                .records(page.getRecords())
                .total(page.getTotal())
                .current(page.getCurrent())
                .size(page.getSize())
                .build();
    }

    @Override
    public ChargeOrderDetailVO getChargeOrderDetail(Long chargeOrderId) {
        return getChargeOrderDetailOrThrow(chargeOrderId, currentLoginUser());
    }

    @Override
    @Transactional
    public ChargeOrderDetailVO createChargeOrder(ChargeOrderCreateRequest request) {
        LoginUser loginUser = currentLoginUser();
        Long orgId = loginUser.getOrgId();
        dataScopeHelper.assertClinicAccess(loginUser, request.getClinicId());
        validateParticipants(orgId, request);
        if (request.getItems().isEmpty()) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "收费明细不能为空");
        }

        AmountSummary amountSummary = calculateAmounts(request.getItems());

        ChargeOrderEntity order = new ChargeOrderEntity();
        order.setOrgId(orgId);
        order.setClinicId(request.getClinicId());
        order.setChargeNo(bizNoGenerator.next("C"));
        order.setPatientId(request.getPatientId());
        order.setMedicalRecordId(request.getMedicalRecordId());
        order.setTreatmentPlanId(request.getTreatmentPlanId());
        order.setCashierId(request.getCashierId());
        order.setTotalAmount(amountSummary.totalAmount());
        order.setDiscountAmount(amountSummary.discountAmount());
        order.setReceivableAmount(amountSummary.receivableAmount());
        order.setPaidAmount(BigDecimal.ZERO);
        order.setArrearsAmount(amountSummary.receivableAmount());
        order.setOrderStatus(STATUS_DRAFT);
        order.setChargeTime(request.getChargeTime() == null ? LocalDateTime.now() : request.getChargeTime());
        order.setRemark(request.getRemark());
        chargeOrderMapper.insert(order);

        saveChargeItems(order.getId(), request.getItems());
        createReceivableRecord(order);
        return getChargeOrderDetailOrThrow(order.getId(), loginUser);
    }

    @Override
    @Transactional
    public ChargeOrderDetailVO createPayment(Long chargeOrderId, PaymentCreateRequest request) {
        LoginUser loginUser = currentLoginUser();
        Long orgId = loginUser.getOrgId();
        ChargeOrderEntity order = getChargeOrderEntityOrThrow(chargeOrderId, orgId);
        dataScopeHelper.assertClinicAccess(loginUser, order.getClinicId());
        if (order.getArrearsAmount() == null || order.getArrearsAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "当前收费单无可支付金额");
        }
        if (request.getAmount().compareTo(order.getArrearsAmount()) > 0) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "支付金额不能大于欠费金额");
        }

        PaymentRecordEntity payment = new PaymentRecordEntity();
        payment.setOrgId(orgId);
        payment.setClinicId(order.getClinicId());
        payment.setChargeOrderId(order.getId());
        payment.setPaymentNo(bizNoGenerator.next("PM"));
        payment.setPaymentMethod(request.getPaymentMethod());
        payment.setAmount(request.getAmount());
        payment.setTransactionNo(request.getTransactionNo());
        payment.setPayerName(request.getPayerName());
        payment.setPaymentStatus("SUCCESS");
        payment.setPaidAt(LocalDateTime.now());
        payment.setCashierId(request.getCashierId());
        payment.setRemark(request.getRemark());
        paymentRecordMapper.insert(payment);

        order.setPaidAmount(defaultZero(order.getPaidAmount()).add(request.getAmount()));
        order.setArrearsAmount(defaultZero(order.getReceivableAmount()).subtract(order.getPaidAmount()));
        order.setOrderStatus(order.getArrearsAmount().compareTo(BigDecimal.ZERO) == 0 ? STATUS_PAID : STATUS_PART_PAID);
        if (STATUS_PAID.equals(order.getOrderStatus())) {
            order.setSettledAt(LocalDateTime.now());
        }
        chargeOrderMapper.updateById(order);
        updateReceivableRecord(order);
        return getChargeOrderDetailOrThrow(order.getId(), loginUser);
    }

    @Override
    @Transactional
    public ChargeOrderDetailVO createRefund(Long chargeOrderId, RefundCreateRequest request) {
        LoginUser loginUser = currentLoginUser();
        Long orgId = loginUser.getOrgId();
        Long operatorId = currentUserId();
        ChargeOrderEntity order = getChargeOrderEntityOrThrow(chargeOrderId, orgId);
        dataScopeHelper.assertClinicAccess(loginUser, order.getClinicId());
        if (defaultZero(order.getPaidAmount()).compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "当前收费单无可退款金额");
        }
        if (request.getRefundAmount().compareTo(order.getPaidAmount()) > 0) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "退款金额不能大于已支付金额");
        }
        if (request.getPaymentRecordId() != null) {
            PaymentRecordEntity payment = paymentRecordMapper.selectOne(new LambdaQueryWrapper<PaymentRecordEntity>()
                    .eq(PaymentRecordEntity::getId, request.getPaymentRecordId())
                    .eq(PaymentRecordEntity::getChargeOrderId, chargeOrderId)
                    .last("LIMIT 1"));
            if (payment == null) {
                throw new BusinessException(ErrorCode.BAD_REQUEST, "支付记录不存在");
            }
        }

        RefundRecordEntity refund = new RefundRecordEntity();
        refund.setOrgId(orgId);
        refund.setClinicId(order.getClinicId());
        refund.setRefundNo(bizNoGenerator.next("RF"));
        refund.setChargeOrderId(order.getId());
        refund.setPaymentRecordId(request.getPaymentRecordId());
        refund.setPatientId(order.getPatientId());
        refund.setRefundAmount(request.getRefundAmount());
        refund.setRefundMethod(request.getRefundMethod());
        refund.setRefundReason(request.getRefundReason());
        refund.setRefundStatus("SUCCESS");
        refund.setApprovedBy(operatorId);
        refund.setRefundedAt(LocalDateTime.now());
        refund.setOperatorId(operatorId);
        refund.setRemark(request.getRemark());
        refundRecordMapper.insert(refund);

        order.setPaidAmount(order.getPaidAmount().subtract(request.getRefundAmount()));
        order.setArrearsAmount(defaultZero(order.getReceivableAmount()).subtract(order.getPaidAmount()));
        order.setOrderStatus(order.getPaidAmount().compareTo(BigDecimal.ZERO) == 0 ? STATUS_REFUNDED : STATUS_PART_PAID);
        if (order.getPaidAmount().compareTo(defaultZero(order.getReceivableAmount())) < 0) {
            order.setSettledAt(null);
        }
        chargeOrderMapper.updateById(order);
        updateReceivableRecord(order);
        return getChargeOrderDetailOrThrow(order.getId(), loginUser);
    }

    private void saveChargeItems(Long chargeOrderId, List<ChargeItemCreateRequest> items) {
        for (ChargeItemCreateRequest item : items) {
            BigDecimal totalAmount = item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
            BigDecimal discountAmount = defaultZero(item.getDiscountAmount());
            BigDecimal receivableAmount = totalAmount.subtract(discountAmount);
            if (receivableAmount.compareTo(BigDecimal.ZERO) < 0) {
                throw new BusinessException(ErrorCode.BAD_REQUEST, "收费明细优惠金额不能大于总金额");
            }

            ChargeItemEntity entity = new ChargeItemEntity();
            entity.setChargeOrderId(chargeOrderId);
            entity.setTreatmentItemId(item.getTreatmentItemId());
            entity.setItemCode(item.getItemCode());
            entity.setItemName(item.getItemName());
            entity.setItemCategory(item.getItemCategory());
            entity.setToothPosition(item.getToothPosition());
            entity.setUnitPrice(item.getUnitPrice());
            entity.setQuantity(item.getQuantity());
            entity.setTotalAmount(totalAmount);
            entity.setDiscountAmount(discountAmount);
            entity.setReceivableAmount(receivableAmount);
            entity.setDoctorId(item.getDoctorId());
            entity.setItemStatus("NORMAL");
            entity.setRemark(item.getRemark());
            chargeItemMapper.insert(entity);
        }
    }

    private AmountSummary calculateAmounts(List<ChargeItemCreateRequest> items) {
        BigDecimal totalAmount = BigDecimal.ZERO;
        BigDecimal discountAmount = BigDecimal.ZERO;
        for (ChargeItemCreateRequest item : items) {
            BigDecimal itemTotal = item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
            BigDecimal itemDiscount = defaultZero(item.getDiscountAmount());
            if (itemDiscount.compareTo(itemTotal) > 0) {
                throw new BusinessException(ErrorCode.BAD_REQUEST, "收费明细优惠金额不能大于总金额");
            }
            totalAmount = totalAmount.add(itemTotal);
            discountAmount = discountAmount.add(itemDiscount);
        }
        BigDecimal receivableAmount = totalAmount.subtract(discountAmount);
        return new AmountSummary(totalAmount, discountAmount, receivableAmount);
    }

    private void validateParticipants(Long orgId, ChargeOrderCreateRequest request) {
        Long clinicCount = chargeOrderMapper.countValidClinic(orgId, request.getClinicId());
        if (clinicCount == null || clinicCount == 0) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "门诊不存在或已停用");
        }
        Long patientCount = chargeOrderMapper.countValidPatient(orgId, request.getPatientId());
        if (patientCount == null || patientCount == 0) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "患者不存在");
        }
        if (request.getMedicalRecordId() != null) {
            Long medicalRecordCount = chargeOrderMapper.countValidMedicalRecord(orgId, request.getMedicalRecordId());
            if (medicalRecordCount == null || medicalRecordCount == 0) {
                throw new BusinessException(ErrorCode.BAD_REQUEST, "病历不存在");
            }
        }
        if (request.getTreatmentPlanId() != null) {
            Long treatmentPlanCount = chargeOrderMapper.countValidTreatmentPlan(orgId, request.getTreatmentPlanId());
            if (treatmentPlanCount == null || treatmentPlanCount == 0) {
                throw new BusinessException(ErrorCode.TREATMENT_PLAN_NOT_FOUND);
            }
        }
    }

    private ChargeOrderDetailVO getChargeOrderDetailOrThrow(Long chargeOrderId, LoginUser loginUser) {
        ChargeOrderDetailVO detail = chargeOrderMapper.selectChargeOrderDetailById(
                chargeOrderId,
                loginUser.getOrgId(),
                dataScopeHelper.resolve(loginUser).name(),
                dataScopeHelper.resolveClinicIds(loginUser),
                loginUser.getStaffId()
        );
        if (detail == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "收费单不存在");
        }
        List<ChargeItemVO> items = chargeItemMapper.selectByChargeOrderId(chargeOrderId);
        List<PaymentRecordVO> payments = paymentRecordMapper.selectByChargeOrderId(chargeOrderId);
        List<RefundRecordVO> refunds = refundRecordMapper.selectByChargeOrderId(chargeOrderId);
        detail.setItems(items == null ? Collections.emptyList() : items);
        detail.setPayments(payments == null ? Collections.emptyList() : payments);
        detail.setRefunds(refunds == null ? Collections.emptyList() : refunds);
        return detail;
    }

    private void createReceivableRecord(ChargeOrderEntity order) {
        ReceivableRecordEntity receivable = new ReceivableRecordEntity();
        receivable.setOrgId(order.getOrgId());
        receivable.setClinicId(order.getClinicId());
        receivable.setChargeOrderId(order.getId());
        receivable.setPatientId(order.getPatientId());
        receivable.setReceivableAmount(order.getReceivableAmount());
        receivable.setReceivedAmount(BigDecimal.ZERO);
        receivable.setOutstandingAmount(order.getReceivableAmount());
        receivable.setReceivableStatus("UNPAID");
        receivable.setRemark(order.getRemark());
        receivableRecordMapper.insert(receivable);
    }

    private void updateReceivableRecord(ChargeOrderEntity order) {
        ReceivableRecordEntity receivable = receivableRecordMapper.selectOne(new LambdaQueryWrapper<ReceivableRecordEntity>()
                .eq(ReceivableRecordEntity::getChargeOrderId, order.getId())
                .last("LIMIT 1"));
        if (receivable == null) {
            createReceivableRecord(order);
            return;
        }
        receivable.setReceivableAmount(order.getReceivableAmount());
        receivable.setReceivedAmount(order.getPaidAmount());
        receivable.setOutstandingAmount(order.getArrearsAmount());
        if (order.getArrearsAmount().compareTo(BigDecimal.ZERO) == 0) {
            receivable.setReceivableStatus("PAID");
        } else if (order.getPaidAmount().compareTo(BigDecimal.ZERO) > 0) {
            receivable.setReceivableStatus("PARTIAL");
        } else {
            receivable.setReceivableStatus("UNPAID");
        }
        receivableRecordMapper.updateById(receivable);
    }

    private ChargeOrderEntity getChargeOrderEntityOrThrow(Long chargeOrderId, Long orgId) {
        ChargeOrderEntity entity = chargeOrderMapper.selectOne(new LambdaQueryWrapper<ChargeOrderEntity>()
                .eq(ChargeOrderEntity::getId, chargeOrderId)
                .eq(ChargeOrderEntity::getOrgId, orgId)
                .last("LIMIT 1"));
        if (entity == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "收费单不存在");
        }
        return entity;
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

    private BigDecimal defaultZero(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    private Long currentUserId() {
        LoginUser loginUser = AuthContext.get();
        return loginUser == null ? null : loginUser.getUserId();
    }

    private record AmountSummary(BigDecimal totalAmount, BigDecimal discountAmount, BigDecimal receivableAmount) {
    }
}
