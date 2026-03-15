package com.company.dental.modules.billing.service;

import com.company.dental.common.api.PageResult;
import com.company.dental.modules.billing.dto.CashierShiftCloseRequest;
import com.company.dental.modules.billing.dto.CashierShiftOpenRequest;
import com.company.dental.modules.billing.query.CashierShiftQuery;
import com.company.dental.modules.billing.vo.CashierShiftVO;

public interface CashierShiftService {

    PageResult<CashierShiftVO> pageShifts(CashierShiftQuery query);

    CashierShiftVO getShiftDetail(Long shiftId);

    CashierShiftVO openShift(CashierShiftOpenRequest request);

    CashierShiftVO closeShift(Long shiftId, CashierShiftCloseRequest request);
}
