package com.company.dental.modules.org.service;

import com.company.dental.modules.org.dto.ClinicSaveRequest;
import com.company.dental.modules.org.dto.DepartmentSaveRequest;
import com.company.dental.modules.org.dto.OrgProfileUpdateRequest;
import com.company.dental.modules.org.dto.RoleSaveRequest;
import com.company.dental.modules.org.dto.RoomSaveRequest;
import com.company.dental.modules.org.dto.StaffSaveRequest;
import com.company.dental.modules.org.query.OrgBaseQuery;
import com.company.dental.modules.org.query.StaffQuery;
import com.company.dental.modules.org.vo.ClinicVO;
import com.company.dental.modules.org.vo.DepartmentVO;
import com.company.dental.modules.org.vo.OrgProfileVO;
import com.company.dental.modules.org.vo.RoleVO;
import com.company.dental.modules.org.vo.RoomVO;
import com.company.dental.modules.org.vo.StaffVO;

import java.util.List;

public interface OrgSettingService {

    OrgProfileVO getOrgProfile();

    OrgProfileVO updateOrgProfile(OrgProfileUpdateRequest request);

    List<ClinicVO> listClinics();

    ClinicVO createClinic(ClinicSaveRequest request);

    ClinicVO updateClinic(Long clinicId, ClinicSaveRequest request);

    List<DepartmentVO> listDepartments(OrgBaseQuery query);

    DepartmentVO createDepartment(DepartmentSaveRequest request);

    DepartmentVO updateDepartment(Long departmentId, DepartmentSaveRequest request);

    List<RoomVO> listRooms(OrgBaseQuery query);

    RoomVO createRoom(RoomSaveRequest request);

    RoomVO updateRoom(Long roomId, RoomSaveRequest request);

    List<StaffVO> listStaff(StaffQuery query);

    StaffVO createStaff(StaffSaveRequest request);

    StaffVO updateStaff(Long staffId, StaffSaveRequest request);

    List<RoleVO> listRoles();

    RoleVO createRole(RoleSaveRequest request);

    RoleVO updateRole(Long roleId, RoleSaveRequest request);
}
