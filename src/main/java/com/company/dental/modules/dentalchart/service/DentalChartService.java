package com.company.dental.modules.dentalchart.service;

import com.company.dental.modules.dentalchart.dto.DentalChartSaveRequest;
import com.company.dental.modules.dentalchart.vo.DentalChartVO;

public interface DentalChartService {

    DentalChartVO getByMedicalRecordId(Long medicalRecordId);

    DentalChartVO saveByMedicalRecordId(Long medicalRecordId, DentalChartSaveRequest request);
}
