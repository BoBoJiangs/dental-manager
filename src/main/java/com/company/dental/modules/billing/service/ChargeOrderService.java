package com.company.dental.modules.billing.service;

import com.company.dental.common.api.PageResult;
import com.company.dental.modules.billing.dto.ChargeOrderCreateRequest;
import com.company.dental.modules.billing.dto.PaymentCreateRequest;
import com.company.dental.modules.billing.dto.RefundCreateRequest;
import com.company.dental.modules.billing.query.ChargeOrderPageQuery;
import com.company.dental.modules.billing.vo.ChargeOrderDetailVO;
import com.company.dental.modules.billing.vo.ChargeOrderPageItemVO;

public interface ChargeOrderService {

    PageResult<ChargeOrderPageItemVO> pageChargeOrders(ChargeOrderPageQuery query);

    ChargeOrderDetailVO getChargeOrderDetail(Long chargeOrderId);

    ChargeOrderDetailVO createChargeOrder(ChargeOrderCreateRequest request);

    ChargeOrderDetailVO createPayment(Long chargeOrderId, PaymentCreateRequest request);

    ChargeOrderDetailVO createRefund(Long chargeOrderId, RefundCreateRequest request);
}
