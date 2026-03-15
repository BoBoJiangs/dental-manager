package com.company.dental.modules.file.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.company.dental.common.enums.ErrorCode;
import com.company.dental.common.exception.BusinessException;
import com.company.dental.framework.security.AuthContext;
import com.company.dental.framework.security.DataScopeHelper;
import com.company.dental.framework.security.LoginUser;
import com.company.dental.integration.file.FileStorageService;
import com.company.dental.integration.file.MinioProperties;
import com.company.dental.modules.file.dto.FileAttachmentUploadRequest;
import com.company.dental.modules.file.entity.FileAttachmentEntity;
import com.company.dental.modules.file.mapper.FileAttachmentMapper;
import com.company.dental.modules.file.query.FileAttachmentQuery;
import com.company.dental.modules.file.service.FileAttachmentService;
import com.company.dental.modules.file.vo.FileAttachmentVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
public class FileAttachmentServiceImpl implements FileAttachmentService {

    private final FileAttachmentMapper fileAttachmentMapper;
    private final FileStorageService fileStorageService;
    private final MinioProperties minioProperties;
    private final DataScopeHelper dataScopeHelper;

    public FileAttachmentServiceImpl(FileAttachmentMapper fileAttachmentMapper,
                                     FileStorageService fileStorageService,
                                     MinioProperties minioProperties,
                                     DataScopeHelper dataScopeHelper) {
        this.fileAttachmentMapper = fileAttachmentMapper;
        this.fileStorageService = fileStorageService;
        this.minioProperties = minioProperties;
        this.dataScopeHelper = dataScopeHelper;
    }

    @Override
    public List<FileAttachmentVO> listAttachments(FileAttachmentQuery query) {
        LoginUser loginUser = currentLoginUser();
        if (query.getClinicId() != null) {
            dataScopeHelper.assertClinicAccess(loginUser, query.getClinicId());
        }
        return fileAttachmentMapper.selectAttachmentList(
                query,
                loginUser.getOrgId(),
                dataScopeHelper.resolve(loginUser).name(),
                dataScopeHelper.resolveClinicIds(loginUser),
                loginUser.getStaffId()
        );
    }

    @Override
    @Transactional
    public FileAttachmentVO uploadAttachment(FileAttachmentUploadRequest request) {
        LoginUser loginUser = currentLoginUser();
        Long orgId = loginUser.getOrgId();
        if (request.getClinicId() != null) {
            dataScopeHelper.assertClinicAccess(loginUser, request.getClinicId());
            Long clinicCount = fileAttachmentMapper.countValidClinic(orgId, request.getClinicId());
            if (clinicCount == null || clinicCount == 0) {
                throw new BusinessException(ErrorCode.BAD_REQUEST, "门诊不存在或已停用");
            }
        }
        if (request.getPatientId() != null) {
            Long patientCount = fileAttachmentMapper.countValidPatient(orgId, request.getPatientId());
            if (patientCount == null || patientCount == 0) {
                throw new BusinessException(ErrorCode.PATIENT_NOT_FOUND);
            }
        }
        if (request.getMedicalRecordId() != null) {
            Long recordCount = fileAttachmentMapper.countValidMedicalRecord(orgId, request.getMedicalRecordId());
            if (recordCount == null || recordCount == 0) {
                throw new BusinessException(ErrorCode.MEDICAL_RECORD_NOT_FOUND);
            }
        }

        MultipartFile multipartFile = request.getFile();
        String originalName = multipartFile.getOriginalFilename();
        String safeFileName = (originalName == null || originalName.isBlank()) ? "upload.bin" : originalName;
        String fileExt = resolveFileExt(safeFileName);
        String objectKey = buildObjectKey(fileExt);
        try {
            fileStorageService.upload(objectKey, multipartFile.getInputStream(), multipartFile.getContentType());
        } catch (IOException ex) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "读取上传文件失败");
        }

        FileAttachmentEntity entity = new FileAttachmentEntity();
        entity.setOrgId(orgId);
        entity.setClinicId(request.getClinicId());
        entity.setBizType(request.getBizType());
        entity.setBizId(request.getBizId());
        entity.setPatientId(request.getPatientId());
        entity.setMedicalRecordId(request.getMedicalRecordId());
        entity.setFileName(safeFileName);
        entity.setFileExt(fileExt);
        entity.setMimeType(multipartFile.getContentType());
        entity.setFileSize(multipartFile.getSize());
        entity.setStorageType("MINIO");
        entity.setBucketName(minioProperties.getBucketName());
        entity.setObjectKey(objectKey);
        entity.setFileUrl(buildFileUrl(objectKey));
        entity.setUploadSource(request.getUploadSource());
        entity.setUploaderId(loginUser.getUserId());
        entity.setFileStatus(1);
        fileAttachmentMapper.insert(entity);

        FileAttachmentVO detail = fileAttachmentMapper.selectAttachmentDetailById(
                entity.getId(),
                orgId,
                dataScopeHelper.resolve(loginUser).name(),
                dataScopeHelper.resolveClinicIds(loginUser),
                loginUser.getStaffId()
        );
        if (detail == null) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "附件登记失败");
        }
        return detail;
    }

    private String buildObjectKey(String fileExt) {
        String suffix = fileExt == null || fileExt.isBlank() ? "" : "." + fileExt;
        return "dental/" + UUID.randomUUID().toString().replace("-", "") + suffix;
    }

    private String resolveFileExt(String fileName) {
        int index = fileName.lastIndexOf('.');
        return index < 0 ? null : fileName.substring(index + 1).toLowerCase();
    }

    private String buildFileUrl(String objectKey) {
        String endpoint = minioProperties.getEndpoint();
        if (endpoint.endsWith("/")) {
            endpoint = endpoint.substring(0, endpoint.length() - 1);
        }
        return endpoint + "/" + minioProperties.getBucketName() + "/" + objectKey;
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
