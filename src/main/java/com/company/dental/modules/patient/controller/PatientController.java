package com.company.dental.modules.patient.controller;

import com.company.dental.common.api.ApiResponse;
import com.company.dental.common.api.PageResult;
import com.company.dental.modules.imaging.vo.PatientImageVO;
import com.company.dental.modules.patient.dto.PatientCreateRequest;
import com.company.dental.modules.patient.dto.PatientPrimaryDoctorUpdateRequest;
import com.company.dental.modules.patient.dto.PatientStatusUpdateRequest;
import com.company.dental.modules.patient.dto.PatientTagUpdateRequest;
import com.company.dental.modules.patient.dto.PatientUpdateRequest;
import com.company.dental.modules.patient.query.PatientPageQuery;
import com.company.dental.modules.patient.service.PatientService;
import com.company.dental.modules.patient.vo.PatientDetailVO;
import com.company.dental.modules.patient.vo.PatientDoctorVO;
import com.company.dental.modules.patient.vo.PatientMemberInfoVO;
import com.company.dental.modules.patient.vo.PatientPageItemVO;
import com.company.dental.modules.patient.vo.PatientTagVO;
import com.company.dental.modules.patient.vo.PatientVisitRecordVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "患者管理")
@RestController
@RequestMapping("/api/patients")
public class PatientController {

    private final PatientService patientService;

    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    @Operation(summary = "分页查询患者")
    @GetMapping
    public ApiResponse<PageResult<PatientPageItemVO>> page(@Valid @ModelAttribute PatientPageQuery query) {
        return ApiResponse.success(patientService.pagePatients(query));
    }

    @Operation(summary = "查询患者详情")
    @GetMapping("/{id}")
    public ApiResponse<PatientDetailVO> detail(@PathVariable Long id) {
        return ApiResponse.success(patientService.getPatientDetail(id));
    }

    @Operation(summary = "查询患者历史就诊记录")
    @GetMapping("/{id}/visit-records")
    public ApiResponse<List<PatientVisitRecordVO>> visitRecords(@PathVariable Long id) {
        return ApiResponse.success(patientService.listVisitRecords(id));
    }

    @Operation(summary = "查询患者影像列表")
    @GetMapping("/{id}/images")
    public ApiResponse<List<PatientImageVO>> images(@PathVariable Long id) {
        return ApiResponse.success(patientService.listPatientImages(id));
    }

    @Operation(summary = "查询患者会员信息")
    @GetMapping("/{id}/member")
    public ApiResponse<PatientMemberInfoVO> member(@PathVariable Long id) {
        return ApiResponse.success(patientService.getPatientMemberInfo(id));
    }

    @Operation(summary = "新建患者")
    @PreAuthorize("hasAuthority('PATIENT_EDIT')")
    @PostMapping
    public ApiResponse<PatientDetailVO> create(@Valid @RequestBody PatientCreateRequest request) {
        return ApiResponse.success(patientService.createPatient(request));
    }

    @Operation(summary = "查询患者标签选项")
    @GetMapping("/tag-options")
    public ApiResponse<List<PatientTagVO>> tagOptions() {
        return ApiResponse.success(patientService.listTagOptions());
    }

    @Operation(summary = "查询医生选项")
    @GetMapping("/doctor-options")
    public ApiResponse<List<PatientDoctorVO>> doctorOptions() {
        return ApiResponse.success(patientService.listDoctorOptions());
    }

    @Operation(summary = "编辑患者")
    @PreAuthorize("hasAuthority('PATIENT_EDIT')")
    @PutMapping("/{id}")
    public ApiResponse<PatientDetailVO> update(@PathVariable Long id, @Valid @RequestBody PatientUpdateRequest request) {
        return ApiResponse.success(patientService.updatePatient(id, request));
    }

    @Operation(summary = "更新患者状态")
    @PreAuthorize("hasAuthority('PATIENT_EDIT')")
    @PutMapping("/{id}/status")
    public ApiResponse<PatientDetailVO> updateStatus(@PathVariable Long id, @Valid @RequestBody PatientStatusUpdateRequest request) {
        return ApiResponse.success(patientService.updatePatientStatus(id, request));
    }

    @Operation(summary = "删除患者")
    @PreAuthorize("hasAuthority('PATIENT_DELETE')")
    @DeleteMapping("/{id}")
    public ApiResponse<String> delete(@PathVariable Long id) {
        patientService.deletePatient(id);
        return ApiResponse.success("删除成功");
    }

    @Operation(summary = "更新患者标签")
    @PreAuthorize("hasAuthority('PATIENT_EDIT')")
    @PutMapping("/{id}/tags")
    public ApiResponse<PatientDetailVO> updateTags(@PathVariable Long id, @Valid @RequestBody PatientTagUpdateRequest request) {
        return ApiResponse.success(patientService.updatePatientTags(id, request));
    }

    @Operation(summary = "更新主治医生")
    @PreAuthorize("hasAuthority('PATIENT_EDIT')")
    @PutMapping("/{id}/primary-doctors")
    public ApiResponse<PatientDetailVO> updatePrimaryDoctors(@PathVariable Long id,
                                                             @Valid @RequestBody PatientPrimaryDoctorUpdateRequest request) {
        return ApiResponse.success(patientService.updatePrimaryDoctors(id, request));
    }
}
