package com.company.dental.modules.imaging.service;

import com.company.dental.modules.imaging.dto.PatientImageCreateRequest;
import com.company.dental.modules.imaging.query.PatientImageQuery;
import com.company.dental.modules.imaging.vo.PatientImageVO;

import java.util.List;

public interface PatientImageService {

    List<PatientImageVO> listPatientImages(PatientImageQuery query);

    PatientImageVO createPatientImage(PatientImageCreateRequest request);
}
