package com.company.dental.modules.emr.service;

import com.company.dental.common.api.PageResult;
import com.company.dental.modules.emr.dto.MedicalRecordCreateRequest;
import com.company.dental.modules.emr.query.MedicalRecordPageQuery;
import com.company.dental.modules.emr.vo.MedicalRecordDetailVO;
import com.company.dental.modules.emr.vo.MedicalRecordPageItemVO;

public interface MedicalRecordService {

    PageResult<MedicalRecordPageItemVO> pageMedicalRecords(MedicalRecordPageQuery query);

    MedicalRecordDetailVO getMedicalRecordDetail(Long recordId);

    MedicalRecordDetailVO createMedicalRecord(MedicalRecordCreateRequest request);

    MedicalRecordDetailVO updateMedicalRecord(Long recordId, MedicalRecordCreateRequest request);
}
