package com.company.dental.modules.org.controller;

import com.company.dental.common.api.ApiResponse;
import com.company.dental.modules.org.dto.ClinicSaveRequest;
import com.company.dental.modules.org.dto.DepartmentSaveRequest;
import com.company.dental.modules.org.dto.OrgProfileUpdateRequest;
import com.company.dental.modules.org.dto.RoleSaveRequest;
import com.company.dental.modules.org.dto.RoomSaveRequest;
import com.company.dental.modules.org.dto.StaffSaveRequest;
import com.company.dental.modules.org.query.OrgBaseQuery;
import com.company.dental.modules.org.query.StaffQuery;
import com.company.dental.modules.org.service.OrgSettingService;
import com.company.dental.modules.org.vo.ClinicVO;
import com.company.dental.modules.org.vo.DepartmentVO;
import com.company.dental.modules.org.vo.OrgProfileVO;
import com.company.dental.modules.org.vo.RoleVO;
import com.company.dental.modules.org.vo.RoomVO;
import com.company.dental.modules.org.vo.StaffVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "组织基础设置")
@RestController
@RequestMapping("/api/org")
public class OrgSettingController {

    private final OrgSettingService orgSettingService;

    public OrgSettingController(OrgSettingService orgSettingService) {
        this.orgSettingService = orgSettingService;
    }

    @Operation(summary = "查询组织信息")
    @GetMapping("/profile")
    public ApiResponse<OrgProfileVO> profile() {
        return ApiResponse.success(orgSettingService.getOrgProfile());
    }

    @Operation(summary = "更新组织信息")
    @PreAuthorize("hasAuthority('ORG_EDIT')")
    @PutMapping("/profile")
    public ApiResponse<OrgProfileVO> updateProfile(@Valid @RequestBody OrgProfileUpdateRequest request) {
        return ApiResponse.success(orgSettingService.updateOrgProfile(request));
    }

    @Operation(summary = "查询门诊列表")
    @GetMapping("/clinics")
    public ApiResponse<List<ClinicVO>> clinics() {
        return ApiResponse.success(orgSettingService.listClinics());
    }

    @Operation(summary = "新增门诊")
    @PreAuthorize("hasAuthority('ORG_EDIT')")
    @PostMapping("/clinics")
    public ApiResponse<ClinicVO> createClinic(@Valid @RequestBody ClinicSaveRequest request) {
        return ApiResponse.success(orgSettingService.createClinic(request));
    }

    @Operation(summary = "更新门诊")
    @PreAuthorize("hasAuthority('ORG_EDIT')")
    @PutMapping("/clinics/{id}")
    public ApiResponse<ClinicVO> updateClinic(@PathVariable Long id, @Valid @RequestBody ClinicSaveRequest request) {
        return ApiResponse.success(orgSettingService.updateClinic(id, request));
    }

    @Operation(summary = "查询科室列表")
    @GetMapping("/departments")
    public ApiResponse<List<DepartmentVO>> departments(@ModelAttribute OrgBaseQuery query) {
        return ApiResponse.success(orgSettingService.listDepartments(query));
    }

    @Operation(summary = "新增科室")
    @PreAuthorize("hasAuthority('ORG_EDIT')")
    @PostMapping("/departments")
    public ApiResponse<DepartmentVO> createDepartment(@Valid @RequestBody DepartmentSaveRequest request) {
        return ApiResponse.success(orgSettingService.createDepartment(request));
    }

    @Operation(summary = "更新科室")
    @PreAuthorize("hasAuthority('ORG_EDIT')")
    @PutMapping("/departments/{id}")
    public ApiResponse<DepartmentVO> updateDepartment(@PathVariable Long id, @Valid @RequestBody DepartmentSaveRequest request) {
        return ApiResponse.success(orgSettingService.updateDepartment(id, request));
    }

    @Operation(summary = "查询诊室列表")
    @GetMapping("/rooms")
    public ApiResponse<List<RoomVO>> rooms(@ModelAttribute OrgBaseQuery query) {
        return ApiResponse.success(orgSettingService.listRooms(query));
    }

    @Operation(summary = "新增诊室")
    @PreAuthorize("hasAuthority('ORG_EDIT')")
    @PostMapping("/rooms")
    public ApiResponse<RoomVO> createRoom(@Valid @RequestBody RoomSaveRequest request) {
        return ApiResponse.success(orgSettingService.createRoom(request));
    }

    @Operation(summary = "更新诊室")
    @PreAuthorize("hasAuthority('ORG_EDIT')")
    @PutMapping("/rooms/{id}")
    public ApiResponse<RoomVO> updateRoom(@PathVariable Long id, @Valid @RequestBody RoomSaveRequest request) {
        return ApiResponse.success(orgSettingService.updateRoom(id, request));
    }

    @Operation(summary = "查询员工列表")
    @GetMapping("/staff")
    public ApiResponse<List<StaffVO>> staff(@ModelAttribute StaffQuery query) {
        return ApiResponse.success(orgSettingService.listStaff(query));
    }

    @Operation(summary = "新增员工")
    @PreAuthorize("hasAuthority('ORG_EDIT')")
    @PostMapping("/staff")
    public ApiResponse<StaffVO> createStaff(@Valid @RequestBody StaffSaveRequest request) {
        return ApiResponse.success(orgSettingService.createStaff(request));
    }

    @Operation(summary = "更新员工")
    @PreAuthorize("hasAuthority('ORG_EDIT')")
    @PutMapping("/staff/{id}")
    public ApiResponse<StaffVO> updateStaff(@PathVariable Long id, @Valid @RequestBody StaffSaveRequest request) {
        return ApiResponse.success(orgSettingService.updateStaff(id, request));
    }

    @Operation(summary = "查询角色列表")
    @PreAuthorize("hasAuthority('ORG_ROLE_VIEW')")
    @GetMapping("/roles")
    public ApiResponse<List<RoleVO>> roles() {
        return ApiResponse.success(orgSettingService.listRoles());
    }

    @Operation(summary = "新增角色")
    @PreAuthorize("hasAuthority('ORG_EDIT')")
    @PostMapping("/roles")
    public ApiResponse<RoleVO> createRole(@Valid @RequestBody RoleSaveRequest request) {
        return ApiResponse.success(orgSettingService.createRole(request));
    }

    @Operation(summary = "更新角色")
    @PreAuthorize("hasAuthority('ORG_EDIT')")
    @PutMapping("/roles/{id}")
    public ApiResponse<RoleVO> updateRole(@PathVariable Long id, @Valid @RequestBody RoleSaveRequest request) {
        return ApiResponse.success(orgSettingService.updateRole(id, request));
    }
}
