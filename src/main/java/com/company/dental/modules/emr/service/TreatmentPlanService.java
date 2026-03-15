package com.company.dental.modules.emr.service;

import com.company.dental.common.api.PageResult;
import com.company.dental.modules.emr.dto.TreatmentPlanCreateRequest;
import com.company.dental.modules.emr.dto.TreatmentPlanStatusUpdateRequest;
import com.company.dental.modules.emr.query.TreatmentPlanPageQuery;
import com.company.dental.modules.emr.vo.TreatmentPlanDetailVO;
import com.company.dental.modules.emr.vo.TreatmentPlanPageItemVO;

public interface TreatmentPlanService {

    PageResult<TreatmentPlanPageItemVO> pageTreatmentPlans(TreatmentPlanPageQuery query);

    TreatmentPlanDetailVO getTreatmentPlanDetail(Long planId);

    TreatmentPlanDetailVO createTreatmentPlan(TreatmentPlanCreateRequest request);

    TreatmentPlanDetailVO updateTreatmentPlanStatus(Long planId, TreatmentPlanStatusUpdateRequest request);
}
