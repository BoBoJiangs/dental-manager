package com.company.dental.modules.patient.service;

import com.company.dental.common.api.PageResult;
import com.company.dental.modules.imaging.vo.PatientImageVO;
import com.company.dental.modules.patient.dto.PatientCreateRequest;
import com.company.dental.modules.patient.dto.PatientPrimaryDoctorUpdateRequest;
import com.company.dental.modules.patient.dto.PatientTagUpdateRequest;
import com.company.dental.modules.patient.dto.PatientStatusUpdateRequest;
import com.company.dental.modules.patient.dto.PatientUpdateRequest;
import com.company.dental.modules.patient.query.PatientPageQuery;
import com.company.dental.modules.patient.vo.PatientDetailVO;
import com.company.dental.modules.patient.vo.PatientDoctorVO;
import com.company.dental.modules.patient.vo.PatientMemberInfoVO;
import com.company.dental.modules.patient.vo.PatientPageItemVO;
import com.company.dental.modules.patient.vo.PatientTagVO;
import com.company.dental.modules.patient.vo.PatientVisitRecordVO;

import java.util.List;

public interface PatientService {

    PageResult<PatientPageItemVO> pagePatients(PatientPageQuery query);

    PatientDetailVO getPatientDetail(Long patientId);

    PatientDetailVO createPatient(PatientCreateRequest request);

    PatientDetailVO updatePatient(Long patientId, PatientUpdateRequest request);

    List<PatientTagVO> listTagOptions();

    PatientDetailVO updatePatientTags(Long patientId, PatientTagUpdateRequest request);

    List<PatientDoctorVO> listDoctorOptions();

    PatientDetailVO updatePrimaryDoctors(Long patientId, PatientPrimaryDoctorUpdateRequest request);

    List<PatientVisitRecordVO> listVisitRecords(Long patientId);

    List<PatientImageVO> listPatientImages(Long patientId);

    PatientMemberInfoVO getPatientMemberInfo(Long patientId);

    PatientDetailVO updatePatientStatus(Long patientId, PatientStatusUpdateRequest request);

    void deletePatient(Long patientId);
}
