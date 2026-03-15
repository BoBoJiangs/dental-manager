package com.company.dental.modules.file.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.company.dental.modules.file.entity.FileAttachmentEntity;
import com.company.dental.modules.file.query.FileAttachmentQuery;
import com.company.dental.modules.file.vo.FileAttachmentVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface FileAttachmentMapper extends BaseMapper<FileAttachmentEntity> {

    List<FileAttachmentVO> selectAttachmentList(@Param("query") FileAttachmentQuery query,
                                                @Param("orgId") Long orgId,
                                                @Param("accessScope") String accessScope,
                                                @Param("clinicIds") List<Long> clinicIds,
                                                @Param("staffId") Long staffId);

    FileAttachmentVO selectAttachmentDetailById(@Param("id") Long id,
                                                @Param("orgId") Long orgId,
                                                @Param("accessScope") String accessScope,
                                                @Param("clinicIds") List<Long> clinicIds,
                                                @Param("staffId") Long staffId);

    @Select("""
            SELECT COUNT(1)
            FROM clinic
            WHERE id = #{clinicId}
              AND org_id = #{orgId}
              AND status = 1
              AND is_deleted = 0
            """)
    Long countValidClinic(@Param("orgId") Long orgId, @Param("clinicId") Long clinicId);

    @Select("""
            SELECT COUNT(1)
            FROM patient
            WHERE id = #{patientId}
              AND org_id = #{orgId}
              AND is_deleted = 0
            """)
    Long countValidPatient(@Param("orgId") Long orgId, @Param("patientId") Long patientId);

    @Select("""
            SELECT COUNT(1)
            FROM medical_record
            WHERE id = #{medicalRecordId}
              AND org_id = #{orgId}
              AND is_deleted = 0
            """)
    Long countValidMedicalRecord(@Param("orgId") Long orgId, @Param("medicalRecordId") Long medicalRecordId);
}
