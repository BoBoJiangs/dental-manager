package com.company.dental.modules.org.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.company.dental.common.enums.ErrorCode;
import com.company.dental.common.exception.BusinessException;
import com.company.dental.framework.security.AuthContext;
import com.company.dental.framework.security.DataScopeHelper;
import com.company.dental.framework.security.DataScopeType;
import com.company.dental.framework.security.LoginUser;
import com.company.dental.framework.security.RolePermissionMatrix;
import com.company.dental.modules.org.dto.ClinicSaveRequest;
import com.company.dental.modules.org.dto.DepartmentSaveRequest;
import com.company.dental.modules.org.dto.OrgProfileUpdateRequest;
import com.company.dental.modules.org.dto.RoleSaveRequest;
import com.company.dental.modules.org.dto.RoomSaveRequest;
import com.company.dental.modules.org.dto.StaffSaveRequest;
import com.company.dental.modules.org.entity.ClinicEntity;
import com.company.dental.modules.org.entity.DepartmentEntity;
import com.company.dental.modules.org.entity.OrgEntity;
import com.company.dental.modules.org.entity.RoleEntity;
import com.company.dental.modules.org.entity.RolePermissionEntity;
import com.company.dental.modules.org.entity.RoomEntity;
import com.company.dental.modules.org.entity.StaffEntity;
import com.company.dental.modules.org.mapper.ClinicMapper;
import com.company.dental.modules.org.mapper.DepartmentMapper;
import com.company.dental.modules.org.mapper.OrgMapper;
import com.company.dental.modules.org.mapper.RoleMapper;
import com.company.dental.modules.org.mapper.RolePermissionMapper;
import com.company.dental.modules.org.mapper.RoomMapper;
import com.company.dental.modules.org.mapper.StaffMapper;
import com.company.dental.modules.org.query.OrgBaseQuery;
import com.company.dental.modules.org.query.StaffQuery;
import com.company.dental.modules.org.service.OrgSettingService;
import com.company.dental.modules.org.vo.ClinicVO;
import com.company.dental.modules.org.vo.DepartmentVO;
import com.company.dental.modules.org.vo.OrgProfileVO;
import com.company.dental.modules.org.vo.RoleVO;
import com.company.dental.modules.org.vo.RoomVO;
import com.company.dental.modules.org.vo.StaffVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class OrgSettingServiceImpl implements OrgSettingService {

    private final OrgMapper orgMapper;
    private final ClinicMapper clinicMapper;
    private final DepartmentMapper departmentMapper;
    private final RoomMapper roomMapper;
    private final StaffMapper staffMapper;
    private final RoleMapper roleMapper;
    private final RolePermissionMapper rolePermissionMapper;
    private final DataScopeHelper dataScopeHelper;
    private final RolePermissionMatrix rolePermissionMatrix;

    public OrgSettingServiceImpl(OrgMapper orgMapper,
                                 ClinicMapper clinicMapper,
                                 DepartmentMapper departmentMapper,
                                 RoomMapper roomMapper,
                                 StaffMapper staffMapper,
                                 RoleMapper roleMapper,
                                 RolePermissionMapper rolePermissionMapper,
                                 DataScopeHelper dataScopeHelper,
                                 RolePermissionMatrix rolePermissionMatrix) {
        this.orgMapper = orgMapper;
        this.clinicMapper = clinicMapper;
        this.departmentMapper = departmentMapper;
        this.roomMapper = roomMapper;
        this.staffMapper = staffMapper;
        this.roleMapper = roleMapper;
        this.rolePermissionMapper = rolePermissionMapper;
        this.dataScopeHelper = dataScopeHelper;
        this.rolePermissionMatrix = rolePermissionMatrix;
    }

    @Override
    public OrgProfileVO getOrgProfile() {
        Long orgId = currentOrgId();
        OrgEntity org = getOrgEntityOrThrow(orgId);
        OrgProfileVO profile = new OrgProfileVO();
        profile.setId(org.getId());
        profile.setOrgCode(org.getOrgCode());
        profile.setOrgName(org.getOrgName());
        profile.setContactName(org.getContactName());
        profile.setContactPhone(org.getContactPhone());
        profile.setStatus(org.getStatus());
        profile.setRemark(org.getRemark());
        return profile;
    }

    @Override
    @Transactional
    public OrgProfileVO updateOrgProfile(OrgProfileUpdateRequest request) {
        LoginUser loginUser = currentLoginUser();
        OrgEntity org = getOrgEntityOrThrow(loginUser.getOrgId());
        org.setOrgName(request.getOrgName());
        org.setContactName(request.getContactName());
        org.setContactPhone(request.getContactPhone());
        org.setStatus(request.getStatus());
        org.setRemark(request.getRemark());
        orgMapper.updateById(org);
        return getOrgProfile();
    }

    @Override
    public List<ClinicVO> listClinics() {
        LoginUser loginUser = currentLoginUser();
        LambdaQueryWrapper<ClinicEntity> wrapper = new LambdaQueryWrapper<ClinicEntity>()
                        .eq(ClinicEntity::getOrgId, loginUser.getOrgId())
                        .eq(ClinicEntity::getIsDeleted, 0);
        if (dataScopeHelper.resolve(loginUser) != DataScopeType.ALL) {
            List<Long> clinicIds = dataScopeHelper.resolveClinicIds(loginUser);
            if (clinicIds.isEmpty()) {
                return List.of();
            }
            wrapper.in(ClinicEntity::getId, clinicIds);
        }
        return clinicMapper.selectList(wrapper
                        .orderByAsc(ClinicEntity::getId))
                .stream()
                .map(this::toClinicVO)
                .toList();
    }

    @Override
    @Transactional
    public ClinicVO createClinic(ClinicSaveRequest request) {
        LoginUser loginUser = currentLoginUser();
        assertClinicCodeUnique(loginUser.getOrgId(), request.getClinicCode(), null);
        ClinicEntity entity = new ClinicEntity();
        entity.setOrgId(loginUser.getOrgId());
        applyClinic(entity, request);
        entity.setIsDeleted(0);
        clinicMapper.insert(entity);
        return toClinicVO(getClinicEntityOrThrow(entity.getId(), loginUser));
    }

    @Override
    @Transactional
    public ClinicVO updateClinic(Long clinicId, ClinicSaveRequest request) {
        LoginUser loginUser = currentLoginUser();
        ClinicEntity entity = getClinicEntityOrThrow(clinicId, loginUser);
        assertClinicCodeUnique(loginUser.getOrgId(), request.getClinicCode(), clinicId);
        applyClinic(entity, request);
        clinicMapper.updateById(entity);
        return toClinicVO(getClinicEntityOrThrow(clinicId, loginUser));
    }

    @Override
    public List<DepartmentVO> listDepartments(OrgBaseQuery query) {
        LoginUser loginUser = currentLoginUser();
        LambdaQueryWrapper<DepartmentEntity> wrapper = new LambdaQueryWrapper<DepartmentEntity>()
                        .eq(DepartmentEntity::getOrgId, loginUser.getOrgId())
                        .eq(DepartmentEntity::getIsDeleted, 0)
                        .eq(query.getClinicId() != null, DepartmentEntity::getClinicId, query.getClinicId());
        if (dataScopeHelper.resolve(loginUser) != DataScopeType.ALL) {
            List<Long> clinicIds = dataScopeHelper.resolveClinicIds(loginUser);
            if (clinicIds.isEmpty()) {
                return List.of();
            }
            wrapper.in(DepartmentEntity::getClinicId, clinicIds);
        }
        return departmentMapper.selectList(wrapper
                        .orderByAsc(DepartmentEntity::getClinicId, DepartmentEntity::getSortNo, DepartmentEntity::getId))
                .stream()
                .map(this::toDepartmentVO)
                .toList();
    }

    @Override
    @Transactional
    public DepartmentVO createDepartment(DepartmentSaveRequest request) {
        LoginUser loginUser = currentLoginUser();
        validateClinicAccess(loginUser, request.getClinicId());
        assertDepartmentCodeUnique(loginUser.getOrgId(), request.getClinicId(), request.getDeptCode(), null);
        DepartmentEntity entity = new DepartmentEntity();
        entity.setOrgId(loginUser.getOrgId());
        applyDepartment(entity, request);
        entity.setIsDeleted(0);
        departmentMapper.insert(entity);
        return toDepartmentVO(getDepartmentEntityOrThrow(entity.getId(), loginUser));
    }

    @Override
    @Transactional
    public DepartmentVO updateDepartment(Long departmentId, DepartmentSaveRequest request) {
        LoginUser loginUser = currentLoginUser();
        DepartmentEntity entity = getDepartmentEntityOrThrow(departmentId, loginUser);
        validateClinicAccess(loginUser, request.getClinicId());
        assertDepartmentCodeUnique(loginUser.getOrgId(), request.getClinicId(), request.getDeptCode(), departmentId);
        applyDepartment(entity, request);
        departmentMapper.updateById(entity);
        return toDepartmentVO(getDepartmentEntityOrThrow(departmentId, loginUser));
    }

    @Override
    public List<RoomVO> listRooms(OrgBaseQuery query) {
        LoginUser loginUser = currentLoginUser();
        LambdaQueryWrapper<RoomEntity> wrapper = new LambdaQueryWrapper<RoomEntity>()
                        .eq(RoomEntity::getOrgId, loginUser.getOrgId())
                        .eq(RoomEntity::getIsDeleted, 0)
                        .eq(query.getClinicId() != null, RoomEntity::getClinicId, query.getClinicId());
        if (dataScopeHelper.resolve(loginUser) != DataScopeType.ALL) {
            List<Long> clinicIds = dataScopeHelper.resolveClinicIds(loginUser);
            if (clinicIds.isEmpty()) {
                return List.of();
            }
            wrapper.in(RoomEntity::getClinicId, clinicIds);
        }
        return roomMapper.selectList(wrapper
                        .orderByAsc(RoomEntity::getClinicId, RoomEntity::getId))
                .stream()
                .map(this::toRoomVO)
                .toList();
    }

    @Override
    @Transactional
    public RoomVO createRoom(RoomSaveRequest request) {
        LoginUser loginUser = currentLoginUser();
        validateClinicAccess(loginUser, request.getClinicId());
        assertRoomCodeUnique(loginUser.getOrgId(), request.getClinicId(), request.getRoomCode(), null);
        RoomEntity entity = new RoomEntity();
        entity.setOrgId(loginUser.getOrgId());
        applyRoom(entity, request);
        entity.setIsDeleted(0);
        roomMapper.insert(entity);
        return toRoomVO(getRoomEntityOrThrow(entity.getId(), loginUser));
    }

    @Override
    @Transactional
    public RoomVO updateRoom(Long roomId, RoomSaveRequest request) {
        LoginUser loginUser = currentLoginUser();
        RoomEntity entity = getRoomEntityOrThrow(roomId, loginUser);
        validateClinicAccess(loginUser, request.getClinicId());
        assertRoomCodeUnique(loginUser.getOrgId(), request.getClinicId(), request.getRoomCode(), roomId);
        applyRoom(entity, request);
        roomMapper.updateById(entity);
        return toRoomVO(getRoomEntityOrThrow(roomId, loginUser));
    }

    @Override
    public List<StaffVO> listStaff(StaffQuery query) {
        LoginUser loginUser = currentLoginUser();
        LambdaQueryWrapper<StaffEntity> wrapper = new LambdaQueryWrapper<StaffEntity>()
                        .eq(StaffEntity::getOrgId, loginUser.getOrgId())
                        .eq(StaffEntity::getIsDeleted, 0)
                        .eq(query.getClinicId() != null, StaffEntity::getMainClinicId, query.getClinicId())
                        .eq(query.getStatus() != null, StaffEntity::getStatus, query.getStatus())
                        .eq(query.getStaffType() != null, StaffEntity::getStaffType, query.getStaffType())
                        .and(query.getKeyword() != null && !query.getKeyword().isBlank(), nested -> nested
                                .like(StaffEntity::getStaffName, query.getKeyword())
                                .or()
                                .like(StaffEntity::getStaffCode, query.getKeyword())
                                .or()
                                .like(StaffEntity::getMobile, query.getKeyword()));
        if (dataScopeHelper.resolve(loginUser) != DataScopeType.ALL) {
            List<Long> clinicIds = dataScopeHelper.resolveClinicIds(loginUser);
            if (clinicIds.isEmpty()) {
                return List.of();
            }
            wrapper.in(StaffEntity::getMainClinicId, clinicIds);
        }
        return staffMapper.selectList(wrapper
                        .orderByAsc(StaffEntity::getMainClinicId, StaffEntity::getId))
                .stream()
                .map(this::toStaffVO)
                .toList();
    }

    @Override
    @Transactional
    public StaffVO createStaff(StaffSaveRequest request) {
        LoginUser loginUser = currentLoginUser();
        validateStaffRequest(loginUser, request, null);
        assertStaffCodeUnique(loginUser.getOrgId(), request.getStaffCode(), null);
        StaffEntity entity = new StaffEntity();
        entity.setOrgId(loginUser.getOrgId());
        applyStaff(entity, request);
        entity.setIsDeleted(0);
        staffMapper.insert(entity);
        return toStaffVO(getStaffEntityOrThrow(entity.getId(), loginUser));
    }

    @Override
    @Transactional
    public StaffVO updateStaff(Long staffId, StaffSaveRequest request) {
        LoginUser loginUser = currentLoginUser();
        StaffEntity entity = getStaffEntityOrThrow(staffId, loginUser);
        validateStaffRequest(loginUser, request, staffId);
        assertStaffCodeUnique(loginUser.getOrgId(), request.getStaffCode(), staffId);
        applyStaff(entity, request);
        staffMapper.updateById(entity);
        return toStaffVO(getStaffEntityOrThrow(staffId, loginUser));
    }

    @Override
    public List<RoleVO> listRoles() {
        return roleMapper.selectList(new LambdaQueryWrapper<RoleEntity>()
                        .eq(RoleEntity::getOrgId, currentOrgId())
                        .eq(RoleEntity::getIsDeleted, 0)
                        .orderByAsc(RoleEntity::getId))
                .stream()
                .map(this::toRoleVO)
                .toList();
    }

    @Override
    @Transactional
    public RoleVO createRole(RoleSaveRequest request) {
        Long orgId = currentOrgId();
        assertRoleCodeUnique(orgId, request.getRoleCode(), null);
        RoleEntity entity = new RoleEntity();
        entity.setOrgId(orgId);
        applyRole(entity, request);
        entity.setIsDeleted(0);
        roleMapper.insert(entity);
        syncRolePermissions(entity.getId(), request);
        return toRoleVO(getRoleEntityOrThrow(entity.getId(), orgId));
    }

    @Override
    @Transactional
    public RoleVO updateRole(Long roleId, RoleSaveRequest request) {
        Long orgId = currentOrgId();
        RoleEntity entity = getRoleEntityOrThrow(roleId, orgId);
        assertRoleCodeUnique(orgId, request.getRoleCode(), roleId);
        applyRole(entity, request);
        roleMapper.updateById(entity);
        syncRolePermissions(roleId, request);
        return toRoleVO(getRoleEntityOrThrow(roleId, orgId));
    }

    private ClinicVO toClinicVO(ClinicEntity entity) {
        ClinicVO vo = new ClinicVO();
        vo.setId(entity.getId());
        vo.setClinicCode(entity.getClinicCode());
        vo.setClinicName(entity.getClinicName());
        vo.setClinicType(entity.getClinicType());
        vo.setProvince(entity.getProvince());
        vo.setCity(entity.getCity());
        vo.setDistrict(entity.getDistrict());
        vo.setAddress(entity.getAddress());
        vo.setPhone(entity.getPhone());
        vo.setBusinessHours(entity.getBusinessHours());
        vo.setStatus(entity.getStatus());
        return vo;
    }

    private DepartmentVO toDepartmentVO(DepartmentEntity entity) {
        DepartmentVO vo = new DepartmentVO();
        vo.setId(entity.getId());
        vo.setClinicId(entity.getClinicId());
        vo.setDeptCode(entity.getDeptCode());
        vo.setDeptName(entity.getDeptName());
        vo.setStatus(entity.getStatus());
        vo.setSortNo(entity.getSortNo());
        vo.setRemark(entity.getRemark());
        return vo;
    }

    private RoomVO toRoomVO(RoomEntity entity) {
        RoomVO vo = new RoomVO();
        vo.setId(entity.getId());
        vo.setClinicId(entity.getClinicId());
        vo.setRoomCode(entity.getRoomCode());
        vo.setRoomName(entity.getRoomName());
        vo.setRoomType(entity.getRoomType());
        vo.setFloorNo(entity.getFloorNo());
        vo.setStatus(entity.getStatus());
        return vo;
    }

    private StaffVO toStaffVO(StaffEntity entity) {
        StaffVO vo = new StaffVO();
        vo.setId(entity.getId());
        vo.setStaffCode(entity.getStaffCode());
        vo.setStaffName(entity.getStaffName());
        vo.setGender(entity.getGender());
        vo.setMobile(entity.getMobile());
        vo.setJobTitle(entity.getJobTitle());
        vo.setStaffType(entity.getStaffType());
        vo.setMainClinicId(entity.getMainClinicId());
        vo.setDeptId(entity.getDeptId());
        vo.setStatus(entity.getStatus());
        vo.setIsDoctor(entity.getIsDoctor());
        vo.setSpecialty(entity.getSpecialty());
        vo.setRemark(entity.getRemark());
        return vo;
    }

    private RoleVO toRoleVO(RoleEntity entity) {
        RoleVO vo = new RoleVO();
        vo.setId(entity.getId());
        vo.setRoleCode(entity.getRoleCode());
        vo.setRoleName(entity.getRoleName());
        vo.setDataScope(entity.getDataScope());
        vo.setStatus(entity.getStatus());
        vo.setRemark(entity.getRemark());
        vo.setMenuPermissions(resolveRolePermissions(entity.getId(), entity.getRoleCode(), "MENU"));
        vo.setButtonPermissions(resolveRolePermissions(entity.getId(), entity.getRoleCode(), "BUTTON"));
        return vo;
    }

    private void applyRole(RoleEntity entity, RoleSaveRequest request) {
        entity.setRoleCode(request.getRoleCode().trim().toUpperCase());
        entity.setRoleName(request.getRoleName());
        entity.setDataScope(request.getDataScope().trim().toUpperCase());
        entity.setStatus(request.getStatus());
        entity.setRemark(request.getRemark());
    }

    private List<String> resolveRolePermissions(Long roleId, String roleCode, String permissionType) {
        List<String> permissions = rolePermissionMapper.selectList(new LambdaQueryWrapper<RolePermissionEntity>()
                        .eq(RolePermissionEntity::getRoleId, roleId)
                        .eq(RolePermissionEntity::getPermissionType, permissionType)
                        .orderByAsc(RolePermissionEntity::getId))
                .stream()
                .map(RolePermissionEntity::getPermissionCode)
                .toList();
        if (!permissions.isEmpty()) {
            return permissions;
        }
        return ("MENU".equals(permissionType)
                ? rolePermissionMatrix.resolveMenuPermissions(List.of(roleCode))
                : rolePermissionMatrix.resolveButtonPermissions(List.of(roleCode)))
                .stream()
                .sorted()
                .toList();
    }

    private void syncRolePermissions(Long roleId, RoleSaveRequest request) {
        rolePermissionMapper.delete(new LambdaQueryWrapper<RolePermissionEntity>()
                .eq(RolePermissionEntity::getRoleId, roleId));
        insertRolePermissions(roleId, request.getMenuPermissions(), "MENU", rolePermissionMatrix.allMenuPermissions());
        insertRolePermissions(roleId, request.getButtonPermissions(), "BUTTON", rolePermissionMatrix.allButtonPermissions());
    }

    private void insertRolePermissions(Long roleId, List<String> permissions, String permissionType, java.util.Set<String> allowedPermissions) {
        if (permissions == null) {
            return;
        }
        for (String permission : permissions.stream().filter(item -> item != null && !item.isBlank()).map(String::trim).distinct().sorted().toList()) {
            if (!allowedPermissions.contains(permission)) {
                throw new BusinessException(ErrorCode.BAD_REQUEST, "非法权限编码: " + permission);
            }
            RolePermissionEntity entity = new RolePermissionEntity();
            entity.setRoleId(roleId);
            entity.setPermissionCode(permission);
            entity.setPermissionType(permissionType);
            rolePermissionMapper.insert(entity);
        }
    }

    private void applyClinic(ClinicEntity entity, ClinicSaveRequest request) {
        entity.setClinicCode(request.getClinicCode());
        entity.setClinicName(request.getClinicName());
        entity.setClinicType(request.getClinicType());
        entity.setProvince(request.getProvince());
        entity.setCity(request.getCity());
        entity.setDistrict(request.getDistrict());
        entity.setAddress(request.getAddress());
        entity.setPhone(request.getPhone());
        entity.setBusinessHours(request.getBusinessHours());
        entity.setStatus(request.getStatus());
    }

    private void applyDepartment(DepartmentEntity entity, DepartmentSaveRequest request) {
        entity.setClinicId(request.getClinicId());
        entity.setDeptCode(request.getDeptCode());
        entity.setDeptName(request.getDeptName());
        entity.setStatus(request.getStatus());
        entity.setSortNo(request.getSortNo() == null ? 0 : request.getSortNo());
        entity.setRemark(request.getRemark());
    }

    private void applyRoom(RoomEntity entity, RoomSaveRequest request) {
        entity.setClinicId(request.getClinicId());
        entity.setRoomCode(request.getRoomCode());
        entity.setRoomName(request.getRoomName());
        entity.setRoomType(request.getRoomType());
        entity.setFloorNo(request.getFloorNo());
        entity.setStatus(request.getStatus());
    }

    private void applyStaff(StaffEntity entity, StaffSaveRequest request) {
        entity.setStaffCode(request.getStaffCode());
        entity.setStaffName(request.getStaffName());
        entity.setGender(request.getGender());
        entity.setMobile(request.getMobile());
        entity.setIdNo(request.getIdNo());
        entity.setJobTitle(request.getJobTitle());
        entity.setStaffType(request.getStaffType());
        entity.setMainClinicId(request.getMainClinicId());
        entity.setDeptId(request.getDeptId());
        entity.setStatus(request.getStatus());
        entity.setEntryDate(request.getEntryDate());
        entity.setLeaveDate(request.getLeaveDate());
        entity.setIsDoctor(request.getIsDoctor());
        entity.setSpecialty(request.getSpecialty());
        entity.setRemark(request.getRemark());
    }

    private void validateStaffRequest(LoginUser loginUser, StaffSaveRequest request, Long currentStaffId) {
        if (request.getMainClinicId() != null) {
            validateClinicAccess(loginUser, request.getMainClinicId());
        }
        if (request.getDeptId() != null) {
            DepartmentEntity department = getDepartmentById(request.getDeptId(), loginUser.getOrgId());
            validateClinicAccess(loginUser, department.getClinicId());
            if (request.getMainClinicId() != null && !request.getMainClinicId().equals(department.getClinicId())) {
                throw new BusinessException(ErrorCode.BAD_REQUEST, "所属科室与主门诊不匹配");
            }
        }
        if (request.getLeaveDate() != null && request.getEntryDate() != null && request.getLeaveDate().isBefore(request.getEntryDate())) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "离职日期不能早于入职日期");
        }
    }

    private void validateClinicAccess(LoginUser loginUser, Long clinicId) {
        ClinicEntity clinic = getClinicEntityOrThrow(clinicId, loginUser);
        dataScopeHelper.assertClinicAccess(loginUser, clinic.getId());
    }

    private void assertClinicCodeUnique(Long orgId, String clinicCode, Long excludeId) {
        Long count = clinicMapper.selectCount(new LambdaQueryWrapper<ClinicEntity>()
                .eq(ClinicEntity::getOrgId, orgId)
                .eq(ClinicEntity::getClinicCode, clinicCode)
                .eq(ClinicEntity::getIsDeleted, 0)
                .ne(excludeId != null, ClinicEntity::getId, excludeId));
        if (count != null && count > 0) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "门诊编码已存在");
        }
    }

    private void assertDepartmentCodeUnique(Long orgId, Long clinicId, String deptCode, Long excludeId) {
        Long count = departmentMapper.selectCount(new LambdaQueryWrapper<DepartmentEntity>()
                .eq(DepartmentEntity::getOrgId, orgId)
                .eq(DepartmentEntity::getClinicId, clinicId)
                .eq(DepartmentEntity::getDeptCode, deptCode)
                .eq(DepartmentEntity::getIsDeleted, 0)
                .ne(excludeId != null, DepartmentEntity::getId, excludeId));
        if (count != null && count > 0) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "科室编码已存在");
        }
    }

    private void assertRoomCodeUnique(Long orgId, Long clinicId, String roomCode, Long excludeId) {
        Long count = roomMapper.selectCount(new LambdaQueryWrapper<RoomEntity>()
                .eq(RoomEntity::getOrgId, orgId)
                .eq(RoomEntity::getClinicId, clinicId)
                .eq(RoomEntity::getRoomCode, roomCode)
                .eq(RoomEntity::getIsDeleted, 0)
                .ne(excludeId != null, RoomEntity::getId, excludeId));
        if (count != null && count > 0) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "诊室编码已存在");
        }
    }

    private void assertStaffCodeUnique(Long orgId, String staffCode, Long excludeId) {
        Long count = staffMapper.selectCount(new LambdaQueryWrapper<StaffEntity>()
                .eq(StaffEntity::getOrgId, orgId)
                .eq(StaffEntity::getStaffCode, staffCode)
                .eq(StaffEntity::getIsDeleted, 0)
                .ne(excludeId != null, StaffEntity::getId, excludeId));
        if (count != null && count > 0) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "员工编码已存在");
        }
    }

    private void assertRoleCodeUnique(Long orgId, String roleCode, Long excludeId) {
        Long count = roleMapper.selectCount(new LambdaQueryWrapper<RoleEntity>()
                .eq(RoleEntity::getOrgId, orgId)
                .eq(RoleEntity::getRoleCode, roleCode.trim().toUpperCase())
                .eq(RoleEntity::getIsDeleted, 0)
                .ne(excludeId != null, RoleEntity::getId, excludeId));
        if (count != null && count > 0) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "角色编码已存在");
        }
    }

    private OrgEntity getOrgEntityOrThrow(Long orgId) {
        OrgEntity org = orgMapper.selectOne(new LambdaQueryWrapper<OrgEntity>()
                .eq(OrgEntity::getId, orgId)
                .eq(OrgEntity::getIsDeleted, 0)
                .last("LIMIT 1"));
        if (org == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "组织不存在");
        }
        return org;
    }

    private ClinicEntity getClinicEntityOrThrow(Long clinicId, LoginUser loginUser) {
        ClinicEntity clinic = clinicMapper.selectOne(new LambdaQueryWrapper<ClinicEntity>()
                .eq(ClinicEntity::getId, clinicId)
                .eq(ClinicEntity::getOrgId, loginUser.getOrgId())
                .eq(ClinicEntity::getIsDeleted, 0)
                .last("LIMIT 1"));
        if (clinic == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "门诊不存在");
        }
        dataScopeHelper.assertClinicAccess(loginUser, clinicId);
        return clinic;
    }

    private DepartmentEntity getDepartmentEntityOrThrow(Long departmentId, LoginUser loginUser) {
        DepartmentEntity entity = getDepartmentById(departmentId, loginUser.getOrgId());
        dataScopeHelper.assertClinicAccess(loginUser, entity.getClinicId());
        return entity;
    }

    private DepartmentEntity getDepartmentById(Long departmentId, Long orgId) {
        DepartmentEntity entity = departmentMapper.selectOne(new LambdaQueryWrapper<DepartmentEntity>()
                .eq(DepartmentEntity::getId, departmentId)
                .eq(DepartmentEntity::getOrgId, orgId)
                .eq(DepartmentEntity::getIsDeleted, 0)
                .last("LIMIT 1"));
        if (entity == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "科室不存在");
        }
        return entity;
    }

    private RoomEntity getRoomEntityOrThrow(Long roomId, LoginUser loginUser) {
        RoomEntity entity = roomMapper.selectOne(new LambdaQueryWrapper<RoomEntity>()
                .eq(RoomEntity::getId, roomId)
                .eq(RoomEntity::getOrgId, loginUser.getOrgId())
                .eq(RoomEntity::getIsDeleted, 0)
                .last("LIMIT 1"));
        if (entity == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "诊室不存在");
        }
        dataScopeHelper.assertClinicAccess(loginUser, entity.getClinicId());
        return entity;
    }

    private StaffEntity getStaffEntityOrThrow(Long staffId, LoginUser loginUser) {
        StaffEntity entity = staffMapper.selectOne(new LambdaQueryWrapper<StaffEntity>()
                .eq(StaffEntity::getId, staffId)
                .eq(StaffEntity::getOrgId, loginUser.getOrgId())
                .eq(StaffEntity::getIsDeleted, 0)
                .last("LIMIT 1"));
        if (entity == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "员工不存在");
        }
        if (entity.getMainClinicId() != null) {
            dataScopeHelper.assertClinicAccess(loginUser, entity.getMainClinicId());
        }
        return entity;
    }

    private RoleEntity getRoleEntityOrThrow(Long roleId, Long orgId) {
        RoleEntity entity = roleMapper.selectOne(new LambdaQueryWrapper<RoleEntity>()
                .eq(RoleEntity::getId, roleId)
                .eq(RoleEntity::getOrgId, orgId)
                .eq(RoleEntity::getIsDeleted, 0)
                .last("LIMIT 1"));
        if (entity == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "角色不存在");
        }
        return entity;
    }

    private Long currentOrgId() {
        return currentLoginUser().getOrgId();
    }

    private LoginUser currentLoginUser() {
        LoginUser loginUser = AuthContext.get();
        Long orgId = loginUser == null ? null : loginUser.getOrgId();
        if (orgId == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }
        return loginUser;
    }
}
