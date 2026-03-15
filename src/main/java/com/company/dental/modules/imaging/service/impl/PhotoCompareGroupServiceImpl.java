package com.company.dental.modules.imaging.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.company.dental.common.enums.ErrorCode;
import com.company.dental.common.exception.BusinessException;
import com.company.dental.framework.security.AuthContext;
import com.company.dental.framework.security.DataScopeHelper;
import com.company.dental.framework.security.LoginUser;
import com.company.dental.modules.imaging.dto.PhotoCompareGroupCreateRequest;
import com.company.dental.modules.imaging.entity.PatientImageEntity;
import com.company.dental.modules.imaging.entity.PhotoCompareGroupEntity;
import com.company.dental.modules.imaging.mapper.PatientImageMapper;
import com.company.dental.modules.imaging.mapper.PhotoCompareGroupMapper;
import com.company.dental.modules.imaging.query.PhotoCompareGroupQuery;
import com.company.dental.modules.imaging.service.PhotoCompareGroupService;
import com.company.dental.modules.imaging.vo.PhotoCompareGroupVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
public class PhotoCompareGroupServiceImpl implements PhotoCompareGroupService {

    private final PhotoCompareGroupMapper photoCompareGroupMapper;
    private final PatientImageMapper patientImageMapper;
    private final DataScopeHelper dataScopeHelper;

    public PhotoCompareGroupServiceImpl(PhotoCompareGroupMapper photoCompareGroupMapper,
                                        PatientImageMapper patientImageMapper,
                                        DataScopeHelper dataScopeHelper) {
        this.photoCompareGroupMapper = photoCompareGroupMapper;
        this.patientImageMapper = patientImageMapper;
        this.dataScopeHelper = dataScopeHelper;
    }

    @Override
    public List<PhotoCompareGroupVO> listCompareGroups(PhotoCompareGroupQuery query) {
        LoginUser loginUser = currentLoginUser();
        return photoCompareGroupMapper.selectCompareGroupList(
                query,
                loginUser.getOrgId(),
                dataScopeHelper.resolve(loginUser).name(),
                dataScopeHelper.resolveClinicIds(loginUser),
                loginUser.getStaffId()
        );
    }

    @Override
    @Transactional
    public PhotoCompareGroupVO createCompareGroup(PhotoCompareGroupCreateRequest request) {
        LoginUser loginUser = currentLoginUser();
        Long orgId = loginUser.getOrgId();

        PatientImageEntity preImage = getImageOrThrow(request.getPreImageId(), orgId);
        PatientImageEntity postImage = getImageOrThrow(request.getPostImageId(), orgId);
        dataScopeHelper.assertClinicAccess(loginUser, preImage.getClinicId());
        dataScopeHelper.assertClinicAccess(loginUser, postImage.getClinicId());

        if (!Objects.equals(preImage.getPatientId(), request.getPatientId()) || !Objects.equals(postImage.getPatientId(), request.getPatientId())) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "对比图片必须属于同一患者");
        }
        if (request.getMedicalRecordId() != null) {
            if (!Objects.equals(preImage.getMedicalRecordId(), request.getMedicalRecordId()) || !Objects.equals(postImage.getMedicalRecordId(), request.getMedicalRecordId())) {
                throw new BusinessException(ErrorCode.BAD_REQUEST, "对比图片必须属于同一病历");
            }
        }

        PhotoCompareGroupEntity entity = new PhotoCompareGroupEntity();
        entity.setPatientId(request.getPatientId());
        entity.setMedicalRecordId(request.getMedicalRecordId());
        entity.setGroupName(request.getGroupName());
        entity.setPreImageId(request.getPreImageId());
        entity.setPostImageId(request.getPostImageId());
        entity.setCompareDesc(request.getCompareDesc());
        entity.setCreatedBy(loginUser.getUserId());
        photoCompareGroupMapper.insert(entity);

        PhotoCompareGroupVO detail = photoCompareGroupMapper.selectCompareGroupDetailById(
                entity.getId(),
                orgId,
                dataScopeHelper.resolve(loginUser).name(),
                dataScopeHelper.resolveClinicIds(loginUser),
                loginUser.getStaffId()
        );
        if (detail == null) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "对比组创建失败");
        }
        return detail;
    }

    private PatientImageEntity getImageOrThrow(Long imageId, Long orgId) {
        PatientImageEntity image = patientImageMapper.selectOne(new LambdaQueryWrapper<PatientImageEntity>()
                .eq(PatientImageEntity::getId, imageId)
                .eq(PatientImageEntity::getOrgId, orgId)
                .last("LIMIT 1"));
        if (image == null) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "影像不存在");
        }
        return image;
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
