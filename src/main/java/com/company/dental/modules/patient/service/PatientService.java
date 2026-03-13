package com.company.dental.modules.patient.service;

import com.company.dental.common.api.PageResult;
import com.company.dental.modules.patient.dto.PatientCreateRequest;
import com.company.dental.modules.patient.query.PatientPageQuery;
import com.company.dental.modules.patient.vo.PatientDetailVO;
import com.company.dental.modules.patient.vo.PatientPageItemVO;

public interface PatientService {

    PageResult<PatientPageItemVO> pagePatients(PatientPageQuery query);

    PatientDetailVO getPatientDetail(Long patientId);

    PatientDetailVO createPatient(PatientCreateRequest request);
}
